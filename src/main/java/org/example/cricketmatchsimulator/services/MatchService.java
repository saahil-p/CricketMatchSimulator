package org.example.cricketmatchsimulator.services;

import org.example.cricketmatchsimulator.dto.BowlerStats;
import org.example.cricketmatchsimulator.dto.PlayerStats;
import org.example.cricketmatchsimulator.dto.ScoreCardResponse;
import org.example.cricketmatchsimulator.entities.*;
import org.example.cricketmatchsimulator.enums.MatchType;
import org.example.cricketmatchsimulator.enums.PlayerRole;
import org.example.cricketmatchsimulator.repository.MatchRepositoryInterface;
import org.example.cricketmatchsimulator.repository.TeamRepositoryInterface;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static org.example.cricketmatchsimulator.enums.PlayerRole.*;

@Service
public class MatchService {
    private final Random random = new Random();
    private final MatchRepositoryInterface matchRepository;
    private final TeamRepositoryInterface teamRepository;

    public MatchService(MatchRepositoryInterface matchRepository, TeamRepositoryInterface teamRepository) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
    }

    public Match createMatch(String team1, String team2, MatchType matchType, LocalDate matchDate) {
        Team firstTeam = teamRepository.findByName(team1)
                .orElseThrow(() -> new RuntimeException("Team not found: " + team1));

        Team secondTeam = teamRepository.findByName(team2)
                .orElseThrow(() -> new RuntimeException("Team not found: " + team2));

        if (firstTeam.getTeamId().equals(secondTeam.getTeamId())) {
            throw new RuntimeException("A team can not play a match against itself");
        }

        Match match = new Match(firstTeam, secondTeam, matchType);
        match.setMatchDate(matchDate);

        return matchRepository.save(match);
    }

    public Optional<Match> getMatchById(String matchId) {
        return matchRepository.findById(matchId);
    }

    public List<Match> getAllMatchesByTeam(String teamName) {
        return matchRepository.findAllMatchesByTeam(teamName).orElseGet(List::of);
    }

    public List<Match> getMatchByTeam1AndTeam2(String team1, String team2, LocalDate matchDate) {
        return matchRepository.findByTeam1AndTeam2(team1, team2, matchDate).orElseGet(List::of);
    }

    private Match determineWinner(Match match){
        if(match.getTeam1Score() > match.getTeam2Score()){
            match.setMatchWinner(match.getTeam1().getName());
        }
        else{
            match.setMatchWinner(match.getTeam2().getName());
        }

        return match;
    }

    private void pickNextBowler(Match currentMatch, int inningsNumber, Player currentBowler){
        Map<String, Integer> oversBowled = currentMatch.getInnings().get(inningsNumber).getOversBowled();
        oversBowled.put(currentBowler.getPlayerId(), oversBowled.getOrDefault(currentBowler.getPlayerId(), 0) + 1);

        Innings innings = currentMatch.getInnings().get(inningsNumber);
        int maxOvers = currentMatch.getMatchType() == MatchType.T20 ? 4 : 10;

        Player nextBowler = innings.getBowlingTeam().getPlayers().stream()
                .filter(player -> player.getRole() == PlayerRole.BOWLER ||
                        player.getRole() == PlayerRole.ALL_ROUNDER)
                .filter(player -> !player.equals(currentBowler))
                .filter(player -> oversBowled.getOrDefault(player.getPlayerId(), 0) < maxOvers)  // Check quota in filter!
                .findAny()
                .orElseThrow(() -> new RuntimeException("No more bowlers available"));

        innings.setBowler(nextBowler);
        innings.setCurrentOver(innings.getCurrentOver() + 1);
        innings.setCurrentBallInOver(0);
    }

    private void simulateInnings(Match currentMatch, int inningsNumber){
        int inningsScore = 0;
        int targetScore = -1;

        // If this is the second innings, get the target score
        if(inningsNumber == 1){
            targetScore = currentMatch.getTeam1Score();
        }

        while(currentMatch.getMatchType() == MatchType.T20 && currentMatch.getInnings().get(inningsNumber).getCurrentOver() < currentMatch.getOvers() || currentMatch.getMatchType() == MatchType.ODI && currentMatch.getInnings().get(inningsNumber).getCurrentOver() < currentMatch.getOvers()) {
            Player currentBowler = currentMatch.getInnings().get(inningsNumber).getBowler();
            while(!isOverCompleted(currentMatch,inningsNumber)){
                int score = simulateBall(currentMatch, inningsNumber);
                if(score == 7){
                    inningsScore += 0;
                }
                else{
                    inningsScore += score;
                }
                updateScore(currentMatch, inningsNumber);

                // Check if chasing team has won (second innings only)
                if(inningsNumber == 1 && inningsScore > targetScore){
                    // Chasing team has exceeded the target, match ends
                    if(currentMatch.getTeam1().getTeamId().equals(currentMatch.getInnings().get(inningsNumber).getBattingTeam().getTeamId())) {
                        currentMatch.setTeam1Score(inningsScore);
                    }
                    else{
                        currentMatch.setTeam2Score(inningsScore);
                    }
                    return;
                }

                if(currentMatch.getInnings().get(inningsNumber).getBalls().get(currentMatch.getInnings().get(inningsNumber).getBalls().size() - 1).getIsWicket()){
                    handleWicket(currentMatch, inningsNumber);
                }
            }
            if (currentMatch.getMatchType() == MatchType.T20 && currentMatch.getInnings().get(inningsNumber).getCurrentOver() < currentMatch.getOvers() || currentMatch.getMatchType() == MatchType.ODI && currentMatch.getInnings().get(inningsNumber).getCurrentOver() < currentMatch.getOvers()) {
                pickNextBowler(currentMatch, inningsNumber, currentBowler);
            }

            if(currentMatch.getInnings().get(inningsNumber).getIsAllOut()){
                break;
            }

            rotateStrike(currentMatch, inningsNumber);
        }
        if(currentMatch.getTeam1().getTeamId().equals(currentMatch.getInnings().get(inningsNumber).getBattingTeam().getTeamId())) {
            currentMatch.setTeam1Score(inningsScore);
        }
        else{
            currentMatch.setTeam2Score(inningsScore);
        }
        return;
    }

    public Match simulateMatch(String t1, String t2, MatchType matchType, LocalDate matchDate){
        Team team1 = teamRepository.findByName(t1)
                .orElseThrow(() -> new RuntimeException("Team not found: " + t1));
        Team team2 = teamRepository.findByName(t2)
                .orElseThrow(() -> new RuntimeException("Team not found: " + t2));


        Match currentMatch;

        Optional<List<Match>> existingMatches = matchRepository.findByTeam1AndTeam2(team1.getName(), team2.getName(), matchDate);
        if(existingMatches.isPresent() && !existingMatches.get().isEmpty()){
            currentMatch = existingMatches.get().get(0);
        }
        else {
            currentMatch = createMatch(team1.getName(), team2.getName(), matchType, matchDate);
        }
        int toss = random.nextInt(2);
        Innings firstInnings, secondInnings;


        List<Player> team1BattingOrder = setBattingOrder(team1);
        List<Player> team2BattingOrder = setBattingOrder(team2);
        if(toss == 0){
            firstInnings = new Innings(team1, team2, team2BattingOrder, team2BattingOrder.get(0), team2BattingOrder.get(1), team1.getPlayers().stream().filter(player -> player.getRole() == BOWLER || player.getRole() == ALL_ROUNDER).findFirst().orElseThrow(() -> new RuntimeException("No bowler found")));
            secondInnings = new Innings(team2, team1, team1BattingOrder, team1BattingOrder.get(0), team1BattingOrder.get(1), team2.getPlayers().stream().filter(player -> player.getRole() == BOWLER || player.getRole() == ALL_ROUNDER).findFirst().orElseThrow(() -> new RuntimeException("No bowler found")));
        }
        else{
            firstInnings = new Innings(team2, team1, team1BattingOrder, team1BattingOrder.get(0), team1BattingOrder.get(1), team2.getPlayers().stream().filter(player -> player.getRole() == BOWLER || player.getRole() == ALL_ROUNDER).findFirst().orElseThrow(() -> new RuntimeException("No bowler found")));
            secondInnings = new Innings(team1, team2, team2BattingOrder, team2BattingOrder.get(0), team2BattingOrder.get(1), team1.getPlayers().stream().filter(player -> player.getRole() == BOWLER || player.getRole() == ALL_ROUNDER).findFirst().orElseThrow(() -> new RuntimeException("No bowler found")));
        }

        firstInnings.getBowlingTeam().getPlayers().stream().filter(player -> player.getRole() == BOWLER || player.getRole() == ALL_ROUNDER).forEach(player -> firstInnings.getOversBowled().put(player.getPlayerId(), 0));
        secondInnings.getBowlingTeam().getPlayers().stream().filter(player -> player.getRole() == BOWLER || player.getRole() == ALL_ROUNDER).forEach(player -> secondInnings.getOversBowled().put(player.getPlayerId(), 0));

        currentMatch.getInnings().add(firstInnings);
        simulateInnings(currentMatch, 0);
        currentMatch.getInnings().add(secondInnings);
        simulateInnings(currentMatch, 1);

        matchRepository.save(determineWinner(currentMatch));

        return determineWinner(currentMatch);
    }

    private List<Player> setBattingOrder(Team team) {

        return team.getPlayers()
                .stream()
                .sorted(Comparator.comparingInt(player -> {
                    return switch (player.getRole()) {
                        case BATTER -> 0;
                        case ALL_ROUNDER -> 1;
                        case BOWLER -> 2;
                        default -> 3;
                    };
                }))
                .toList();
    }
    private int simulateBall(Match currentMatch, int inningsNumber){
        int outcome = random.nextInt(8);
        Innings currentInnings = currentMatch.getInnings().get(inningsNumber);

        Ball ball;
        if(outcome == 7){
            ball = new Ball(currentInnings.getCurrentOver(), currentInnings.getCurrentBallInOver(), currentInnings.getStriker(), currentInnings.getBowler(), 0, true, false);
        }
        else{
            if(outcome % 2 == 1){
                rotateStrike(currentMatch, inningsNumber);
            }
            ball = new Ball(currentInnings.getCurrentOver(), currentInnings.getCurrentBallInOver(), currentInnings.getStriker(), currentInnings.getBowler(), outcome, false, false);
        }
        currentInnings.addBall(ball);
        currentInnings.setCurrentBallInOver(currentInnings.getCurrentBallInOver() + 1);

        return outcome;
    }

    private void updateScore(Match currentMatch, int inningsNumber){
        Innings currentInnings = currentMatch.getInnings().get(inningsNumber);
        currentInnings.setTotalRuns(currentInnings.getTotalRuns() + currentInnings.getBalls().get(currentInnings.getBalls().size() - 1).getRuns());
    }

    private void handleWicket(Match currentMatch, int inningsNumber){
        Innings currentInnings = currentMatch.getInnings().get(inningsNumber);
        Player nextBatsman = currentInnings.getBattingOrder().get(currentInnings.getNextBatsmanIndex());

        if(currentInnings.getNextBatsmanIndex() == currentInnings.getBattingOrder().size() - 1){
            currentInnings.setIsAllOut(true);
            return;
        }
        currentInnings.setStriker(nextBatsman);
        currentInnings.setNextBatsmanIndex(currentInnings.getNextBatsmanIndex() + 1);
    }

    private void rotateStrike(Match currentMatch, int inningsNumber){
        Innings currentInnings = currentMatch.getInnings().get(inningsNumber);
        Player temp = currentInnings.getStriker();
        currentInnings.setStriker(currentInnings.getNonStriker());
        currentInnings.setNonStriker(temp);
    }

    private boolean isOverCompleted(Match currentMatch, int inningsNumber){
        Innings currentInnings = currentMatch.getInnings().get(inningsNumber);
        return currentInnings.getCurrentBallInOver() == 6;
    }


    private void buildScoreCard(Innings innings, ScoreCardResponse scoreCardResponse, int inningsNumber){
        Map<String, PlayerStats> battingStats = inningsNumber == 0 ?
            scoreCardResponse.getBattingStatsInnings1() : scoreCardResponse.getBattingStatsInnings2();
        Map<String, BowlerStats> bowlingStats = inningsNumber == 0 ?
            scoreCardResponse.getBowlingStatsInnings1() : scoreCardResponse.getBowlingStatsInnings2();

        int totalBalls = 0;
        int wickets = 0;

        for(Ball ball : innings.getBalls()){
            Player currentBatsman = ball.getBatter();
            Player currentBowler = ball.getBowler();
            int runs = ball.getRuns();
            boolean isWicket = ball.getIsWicket();

            // Update batting stats
            battingStats.putIfAbsent(currentBatsman.getName(), new PlayerStats(currentBatsman.getName()));
            PlayerStats batsmanStats = battingStats.get(currentBatsman.getName());
            batsmanStats.addRuns(runs);

            if(isWicket){
                batsmanStats.setOut(totalBalls);
                wickets++;
            }

            // Update bowling stats
            bowlingStats.putIfAbsent(currentBowler.getName(), new BowlerStats(currentBowler.getName()));
            BowlerStats bowlerStats = bowlingStats.get(currentBowler.getName());
            bowlerStats.addBall(runs, isWicket);

            totalBalls++;
        }

        // Update wickets count
        if(inningsNumber == 0){
            scoreCardResponse.setTeam1Wickets(wickets);
        } else {
            scoreCardResponse.setTeam2Wickets(wickets);
        }

        // Update overs bowled for each bowler
        for(Map.Entry<String, Integer> entry : innings.getOversBowled().entrySet()){
            String playerId = entry.getKey();
            // Find the player by ID to get their name
            Player bowler = innings.getBowlingTeam().getPlayers().stream()
                    .filter(p -> p.getPlayerId().equals(playerId))
                    .findFirst()
                    .orElse(null);

            if(bowler != null && bowlingStats.containsKey(bowler.getName())){
                BowlerStats bowlerStats = bowlingStats.get(bowler.getName());
                int completedOvers = entry.getValue();
                int ballsInCurrentOver = bowlerStats.getBallsBowled() % 6;
                bowlerStats.setOversBowled(completedOvers);
                bowlerStats.setBallsBowled(ballsInCurrentOver);
            }
        }
    }

    public ScoreCardResponse generateMatchScoreCard(Match currentMatch){
        if(currentMatch.getMatchWinner() == null){
            throw new RuntimeException("Match is not over yet");
        }

        ScoreCardResponse scoreCardResponse = new ScoreCardResponse();

        scoreCardResponse.setTeam1Name(currentMatch.getTeam1().getName());
        scoreCardResponse.setTeam2Name(currentMatch.getTeam2().getName());

        scoreCardResponse.setTeam1Score(currentMatch.getTeam1Score());
        scoreCardResponse.setTeam2Score(currentMatch.getTeam2Score());

        scoreCardResponse.setMatchWinner(currentMatch.getMatchWinner());

        buildScoreCard(currentMatch.getInnings().get(0), scoreCardResponse, 0);
        buildScoreCard(currentMatch.getInnings().get(1), scoreCardResponse, 1);

        return scoreCardResponse;
    }

}