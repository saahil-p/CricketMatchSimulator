# Cricket Match Simulator

A Spring Boot application that simulates cricket matches between teams with realistic ball-by-ball simulation, player roles, and match statistics.

## Table of Contents
- [Overview](#overview)
- [High-Level Design (HLD)](#high-level-design-hld)
- [Low-Level Design (LLD)](#low-level-design-lld)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Sample cURL Commands](#sample-curl-commands)
- [Architecture](#architecture)

---

## Overview

The Cricket Match Simulator is a RESTful API application that allows users to:
- Create teams with players having different roles (Batter, Bowler, All-Rounder)
- Simulate cricket matches (T20, ODI, TEST formats)
- Track ball-by-ball match progression
- View match statistics and results

---

## High-Level Design (HLD)

### System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Client Layer                             │
│              (REST API Consumers - cURL, Postman)            │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  API Controller Layer                        │
│         ┌──────────────────┬──────────────────┐             │
│         │ MatchApiController│ TeamAPIController│             │
│         └──────────────────┴──────────────────┘             │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                   Service Layer                              │
│    ┌──────────────┬──────────────┬──────────────┐           │
│    │ MatchService │ TeamService  │PlayerService │           │
│    └──────────────┴──────────────┴──────────────┘           │
│         (Business Logic & Match Simulation)                  │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                 Repository Layer                             │
│  ┌────────────────────┬────────────────────┬──────────────┐ │
│  │MatchRepository     │TeamRepository      │PlayerRepo    │ │
│  │(InMemory)          │(InMemory)          │(InMemory)    │ │
│  └────────────────────┴────────────────────┴──────────────┘ │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                   Data Storage                               │
│                  In-Memory HashMap                           │
└─────────────────────────────────────────────────────────────┘
```

### Key Components

1. **API Layer**: Exposes REST endpoints for team and match management
2. **Service Layer**: Contains business logic for match simulation
3. **Repository Layer**: Handles data persistence (In-Memory by default)
4. **Entity Layer**: Domain models (Team, Player, Match, Innings, Ball)

---

## Low-Level Design (LLD)

### Core Entities

#### 1. **Player**
```java
- playerId: String (UUID)
- name: String
- role: PlayerRole (BATTER, BOWLER, ALL_ROUNDER, FIELDER, SUBSTITUTE)
```

#### 2. **Team**
```java
- teamId: String (UUID)
- name: String
- players: List<Player> (11 players)
```

#### 3. **Match**
```java
- matchId: String (UUID)
- team1: Team
- team2: Team
- matchType: MatchType (T20, ODI, TEST)
- team1Score: int
- team2Score: int
- matchWinner: String
- innings: List<Innings>
- tossWinner: String
```

#### 4. **Innings**
```java
- battingTeam: Team
- bowlingTeam: Team
- battingOrder: List<Player>
- striker: Player
- nonStriker: Player
- bowler: Player
- balls: List<Ball>
- totalRuns: int
- currentOver: int
- currentBallInOver: int
- isAllOut: Boolean
- oversBowled: Map<Player, Integer>
```

#### 5. **Ball**
```java
- overNumber: Integer
- ballNumber: Integer
- batter: Player
- bowler: Player
- runs: Integer (0-6)
- isWicket: Boolean
- isExtra: Boolean
```

### Match Simulation Algorithm

1. **Match Creation**
   - Validate teams exist
   - Ensure teams are different
   - Create match with specified format

2. **Toss Simulation**
   - Random selection (0 or 1)
   - Determine batting/bowling order

3. **Innings Simulation**
   - Set batting order (Batters → All-Rounders → Bowlers)
   - Initialize bowlers from bowling team
   - Simulate each ball:
     - Random outcome (0-7): 0-6 runs, 7 = wicket
     - Rotate strike on odd runs
     - Handle wickets (bring next batsman)
     - Track over completion
     - Change bowler after each over
   - Continue until overs complete or all out

4. **Winner Determination**
   - Compare team1Score vs team2Score
   - Set matchWinner

### Class Diagram

```
┌─────────────────┐         ┌─────────────────┐
│     Player      │         │      Team       │
├─────────────────┤         ├─────────────────┤
│ - playerId      │◄────────┤ - teamId        │
│ - name          │  *      │ - name          │
│ - role          │         │ - players       │
└─────────────────┘         └─────────────────┘
                                    ▲
                                    │
                                    │ 2
                            ┌───────┴────────┐
                            │     Match      │
                            ├────────────────┤
                            │ - matchId      │
                            │ - team1        │
                            │ - team2        │
                            │ - matchType    │
                            │ - team1Score   │
                            │ - team2Score   │
                            │ - matchWinner  │
                            │ - innings      │
                            └────────┬───────┘
                                     │
                                     │ *
                            ┌────────▼───────┐
                            │    Innings     │
                            ├────────────────┤
                            │ - battingTeam  │
                            │ - bowlingTeam  │
                            │ - striker      │
                            │ - nonStriker   │
                            │ - bowler       │
                            │ - balls        │
                            │ - totalRuns    │
                            └────────┬───────┘
                                     │
                                     │ *
                            ┌────────▼───────┐
                            │      Ball      │
                            ├────────────────┤
                            │ - overNumber   │
                            │ - ballNumber   │
                            │ - batter       │
                            │ - bowler       │
                            │ - runs         │
                            │ - isWicket     │
                            │ - isExtra      │
                            └────────────────┘
```

---

## Technology Stack

- **Framework**: Spring Boot 4.0.3
- **Language**: Java 17
- **Build Tool**: Gradle 9.3.1
- **Dependencies**:
  - Spring Boot Starter Web
  - Lombok (for boilerplate reduction)
  - JUnit 5 (for testing)
- **Storage**: In-Memory HashMap (default)

---

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle 9.3.1 or higher (or use included Gradle wrapper)

### Installation & Running

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd CricketMatchSimulator
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Application will start on**
   ```
   http://localhost:8080
   ```

---

## API Endpoints

### Team Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/teams` | Create a new team with players |

### Match Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/matches` | Create a match between two teams |
| GET | `/api/v1/matches` | Get matches between two specific teams |
| GET | `/api/v1/matches/by-team` | Get all matches for a specific team |
| GET | `/api/v1/matches/simulate` | Simulate a complete match |

---

## Sample cURL Commands

### 1. Create Team 1 (India)

```bash
curl -X POST "http://localhost:8080/api/v1/teams?name=India" \
  -H "Content-Type: application/json" \
  -d '[
    {"name": "Rohit Sharma", "role": "BATTER"},
    {"name": "Shubman Gill", "role": "BATTER"},
    {"name": "Virat Kohli", "role": "BATTER"},
    {"name": "KL Rahul", "role": "BATTER"},
    {"name": "Hardik Pandya", "role": "ALL_ROUNDER"},
    {"name": "Ravindra Jadeja", "role": "ALL_ROUNDER"},
    {"name": "Rishabh Pant", "role": "BATTER"},
    {"name": "Jasprit Bumrah", "role": "BOWLER"},
    {"name": "Mohammed Shami", "role": "BOWLER"},
    {"name": "Kuldeep Yadav", "role": "BOWLER"},
    {"name": "Mohammed Siraj", "role": "BOWLER"}
  ]'
```

### 2. Create Team 2 (Australia)

```bash
curl -X POST "http://localhost:8080/api/v1/teams?name=Australia" \
  -H "Content-Type: application/json" \
  -d '[
    {"name": "David Warner", "role": "BATTER"},
    {"name": "Travis Head", "role": "BATTER"},
    {"name": "Steve Smith", "role": "BATTER"},
    {"name": "Marnus Labuschagne", "role": "BATTER"},
    {"name": "Glenn Maxwell", "role": "ALL_ROUNDER"},
    {"name": "Marcus Stoinis", "role": "ALL_ROUNDER"},
    {"name": "Alex Carey", "role": "BATTER"},
    {"name": "Pat Cummins", "role": "BOWLER"},
    {"name": "Mitchell Starc", "role": "BOWLER"},
    {"name": "Josh Hazlewood", "role": "BOWLER"},
    {"name": "Adam Zampa", "role": "BOWLER"}
  ]'
```



### 3. Simulate a T20 Match

```bash
curl -X GET "http://localhost:8080/api/v1/matches/simulate?team1=India&team2=Australia&matchType=T20"
```

**Expected Response:**
```json
{
  "matchId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "team1": {
    "teamId": "team-uuid-1",
    "name": "India",
    "players": [...]
  },
  "team2": {
    "teamId": "team-uuid-2",
    "name": "Australia",
    "players": [...]
  },
  "winner": null,
  "team1Score": 165,
  "team2Score": 158,
  "matchType": "T20",
  "innings": [
    {
      "bowlingTeam": {...},
      "battingTeam": {...},
      "battingOrder": [...],
      "nextBatsmanIndex": 5,
      "striker": {...},
      "nonStriker": {...},
      "bowler": {...},
      "balls": [...],
      "totalRuns": 165,
      "currentOver": 20,
      "currentBallInOver": 0,
      "allOut": false,
      "oversBowled": {...}
    },
    {...}
  ],
  "tossWinner": null,
  "matchWinner": "India",
  "overs": 20
}
```

### 4. Simulate an ODI Match

```bash
curl -X GET "http://localhost:8080/api/v1/matches/simulate?team1=India&team2=Australia&matchType=ODI"
```

### 5. Create a Match (Without Simulation)

```bash
curl -X POST "http://localhost:8080/api/v1/matches?team1=India&team2=Australia&matchType=T20"
```

**Expected Response:**
```json
{
  "matchId": "match-uuid",
  "team1": {
    "teamId": "team-uuid-1",
    "name": "India",
    "players": [...]
  },
  "team2": {
    "teamId": "team-uuid-2",
    "name": "Australia",
    "players": [...]
  },
  "winner": null,
  "team1Score": 0,
  "team2Score": 0,
  "matchType": "T20",
  "innings": [],
  "tossWinner": null,
  "matchWinner": null,
  "overs": 20
}
```

### 6. Get Matches Between Two Teams

```bash
curl -X GET "http://localhost:8080/api/v1/matches?team1=India&team2=Australia"
```

**Expected Response:**
```json
[
  {
    "matchId": "match-uuid-1",
    "team1": {...},
    "team2": {...},
    "team1Score": 165,
    "team2Score": 158,
    "matchWinner": "India",
    "matchType": "T20"
  },
  {
    "matchId": "match-uuid-2",
    "team1": {...},
    "team2": {...},
    "team1Score": 285,
    "team2Score": 290,
    "matchWinner": "Australia",
    "matchType": "ODI"
  }
]
```

### 7. Get All Matches for a Team

```bash
curl -X GET "http://localhost:8080/api/v1/matches/by-team?team=India"
```

**Expected Response:**
```json
[
  {
    "matchId": "match-uuid-1",
    "team1": {"name": "India", ...},
    "team2": {"name": "Australia", ...},
    "matchWinner": "India"
  },
  {
    "matchId": "match-uuid-2",
    "team1": {"name": "England", ...},
    "team2": {"name": "India", ...},
    "matchWinner": "England"
  }
]
```

---

## Architecture

### Design Patterns Used

1. **Repository Pattern**: Abstraction layer for data access
   - `MatchRepositoryInterface`, `TeamRepositoryInterface`, `PlayerRepositoryInterface`
   - Implementations: `InMemoryMatchRepositoryImpl`, `InMemoryTeamRepositoryImpl`, `InMemoryPlayerRepositoryImpl`
   - Allows easy switching between different storage implementations

2. **Service Layer Pattern**: Business logic separation
   - `MatchService`: Match creation and simulation logic
   - `TeamService`: Team management
   - `PlayerService`: Player management

3. **Dependency Injection**: Spring Boot's IoC container
   - Constructor-based injection for all services and repositories

### Data Flow

```
Client Request
    ↓
Controller (validates request params)
    ↓
Service (business logic, match simulation)
    ↓
Repository (data persistence)
    ↓
In-Memory Storage
    ↓
Response back to Client
```

### Match Simulation Logic

1. **Ball Outcome Generation**
   - Random number (0-7)
   - 0-6: Runs scored
   - 7: Wicket

2. **Strike Rotation**
   - Odd runs (1, 3, 5): Batsmen swap
   - Even runs (0, 2, 4, 6): No swap
   - End of over: Batsmen swap

3. **Bowling Changes**
   - After each over (6 balls)
   - Bowlers selected from BOWLER and ALL_ROUNDER roles
   - Tracks overs bowled per bowler

4. **Wicket Handling**
   - Next batsman from batting order
   - All out when 10 wickets fall

---

## Project Structure

```
CricketMatchSimulator/
├── src/
│   ├── main/
│   │   ├── java/org/example/cricketmatchsimulator/
│   │   │   ├── api/
│   │   │   │   ├── MatchApiController.java
│   │   │   │   └── TeamAPIController.java
│   │   │   ├── dto/
│   │   │   │   ├── BowlerStats.java
│   │   │   │   ├── PlayerStats.java
│   │   │   │   └── ScoreCardResponse.java
│   │   │   ├── entities/
│   │   │   │   ├── Ball.java
│   │   │   │   ├── Innings.java
│   │   │   │   ├── Match.java
│   │   │   │   ├── Player.java
│   │   │   │   └── Team.java
│   │   │   ├── enums/
│   │   │   │   ├── MatchType.java
│   │   │   │   └── PlayerRole.java
│   │   │   ├── repository/
│   │   │   │   ├── InMemoryMatchRepositoryImpl.java
│   │   │   │   ├── InMemoryPlayerRepositoryImpl.java
│   │   │   │   ├── InMemoryTeamRepositoryImpl.java
│   │   │   │   ├── MatchRepositoryInterface.java
│   │   │   │   ├── PlayerRepositoryInterface.java
│   │   │   │   └── TeamRepositoryInterface.java
│   │   │   ├── services/
│   │   │   │   ├── MatchService.java
│   │   │   │   ├── PlayerService.java
│   │   │   │   └── TeamService.java
│   │   │   └── CricketMatchSimulatorApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/org/example/cricketmatchsimulator/
│           └── CricketMatchSimulatorApplicationTests.java
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md
```

---

## Enums

### MatchType
- `T20`: 20 overs per innings
- `ODI`: 50 overs per innings
- `TEST`: Unlimited overs (Integer.MAX_VALUE)

### PlayerRole
- `BATTER`: Specialist batsman
- `BOWLER`: Specialist bowler
- `ALL_ROUNDER`: Can bat and bowl
- `FIELDER`: Specialist fielder
- `SUBSTITUTE`: Substitute player

---

## Features

✅ **Team Management**
- Create teams with 11 players
- Assign roles to players (Batter, Bowler, All-Rounder)

✅ **Match Simulation**
- Support for T20, ODI, and TEST formats
- Ball-by-ball simulation
- Random outcome generation (0-6 runs or wicket)
- Automatic strike rotation
- Bowling changes after each over
- Wicket handling and all-out scenarios

✅ **Match Tracking**
- Store match history
- Query matches by team
- Query matches between specific teams

✅ **Extensible Architecture**
- Repository pattern for easy storage switching
- Service layer for business logic
- Clean separation of concerns

---

## Future Enhancements

🔮 **Potential Features**
- Player statistics (batting average, strike rate, bowling economy)
- Detailed scorecard generation
- Extras (wides, no-balls, byes, leg-byes)
- DLS method for rain-affected matches
- Player form and performance tracking
- Match commentary generation
- WebSocket support for live match updates
- Database integration for persistent storage
- Authentication and authorization
- Match scheduling and tournaments
- Advanced bowling strategies (yorkers, bouncers)
- Fielding positions and catches

---

## Quick Start Testing Guide

### Complete Workflow Example

Follow these steps to test the complete application:

**Step 1: Start the Application**
```bash
./gradlew bootRun
```

**Step 2: Create First Team (India)**
```bash
curl -X POST "http://localhost:8080/api/v1/teams?name=India" \
  -H "Content-Type: application/json" \
  -d '[
    {"name": "Rohit Sharma", "role": "BATTER"},
    {"name": "Shubman Gill", "role": "BATTER"},
    {"name": "Virat Kohli", "role": "BATTER"},
    {"name": "KL Rahul", "role": "BATTER"},
    {"name": "Hardik Pandya", "role": "ALL_ROUNDER"},
    {"name": "Ravindra Jadeja", "role": "ALL_ROUNDER"},
    {"name": "Rishabh Pant", "role": "BATTER"},
    {"name": "Jasprit Bumrah", "role": "BOWLER"},
    {"name": "Mohammed Shami", "role": "BOWLER"},
    {"name": "Kuldeep Yadav", "role": "BOWLER"},
    {"name": "Mohammed Siraj", "role": "BOWLER"}
  ]'
```

**Step 3: Create Second Team (Australia)**
```bash
curl -X POST "http://localhost:8080/api/v1/teams?name=Australia" \
  -H "Content-Type: application/json" \
  -d '[
    {"name": "David Warner", "role": "BATTER"},
    {"name": "Travis Head", "role": "BATTER"},
    {"name": "Steve Smith", "role": "BATTER"},
    {"name": "Marnus Labuschagne", "role": "BATTER"},
    {"name": "Glenn Maxwell", "role": "ALL_ROUNDER"},
    {"name": "Marcus Stoinis", "role": "ALL_ROUNDER"},
    {"name": "Alex Carey", "role": "BATTER"},
    {"name": "Pat Cummins", "role": "BOWLER"},
    {"name": "Mitchell Starc", "role": "BOWLER"},
    {"name": "Josh Hazlewood", "role": "BOWLER"},
    {"name": "Adam Zampa", "role": "BOWLER"}
  ]'
```

**Step 4: Simulate a T20 Match**
```bash
curl -X GET "http://localhost:8080/api/v1/matches/simulate?team1=India&team2=Australia&matchType=T20"
```

**Step 5: View Match History**
```bash
# Get all matches between India and Australia
curl -X GET "http://localhost:8080/api/v1/matches?team1=India&team2=Australia"

# Get all matches for India
curl -X GET "http://localhost:8080/api/v1/matches/by-team?team=India"
```

### Testing Different Match Types

**T20 Match (20 overs)**
```bash
curl -X GET "http://localhost:8080/api/v1/matches/simulate?team1=India&team2=Australia&matchType=T20"
```

**ODI Match (50 overs)**
```bash
curl -X GET "http://localhost:8080/api/v1/matches/simulate?team1=India&team2=Australia&matchType=ODI"
```

**TEST Match (unlimited overs)**
```bash
curl -X GET "http://localhost:8080/api/v1/matches/simulate?team1=India&team2=Australia&matchType=TEST"
```

### Error Scenarios

**Team Not Found**
```bash
curl -X GET "http://localhost:8080/api/v1/matches/simulate?team1=India&team2=Pakistan&matchType=T20"
# Response: 500 Internal Server Error - "Team not found: Pakistan"
```

**Same Team Playing**
```bash
curl -X POST "http://localhost:8080/api/v1/matches?team1=India&team2=India&matchType=T20"
# Response: 500 Internal Server Error - "A team can not play a match against itself"
```

---

## API Response Examples

### Successful Team Creation Response
```json
{
  "teamId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "name": "India",
  "players": [
    {
      "playerId": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Rohit Sharma",
      "role": "BATTER"
    },
    {
      "playerId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
      "name": "Jasprit Bumrah",
      "role": "BOWLER"
    }
    // ... more players
  ]
}
```

### Successful Match Simulation Response
```json
{
  "matchId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "team1": {
    "teamId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "name": "India",
    "players": [...]
  },
  "team2": {
    "teamId": "8e9f0a1b-2c3d-4e5f-6789-0a1b2c3d4e5f",
    "name": "Australia",
    "players": [...]
  },
  "winner": null,
  "team1Score": 165,
  "team2Score": 158,
  "matchType": "T20",
  "innings": [
    {
      "bowlingTeam": {...},
      "battingTeam": {...},
      "battingOrder": [...],
      "nextBatsmanIndex": 5,
      "striker": {
        "playerId": "...",
        "name": "Hardik Pandya",
        "role": "ALL_ROUNDER"
      },
      "nonStriker": {
        "playerId": "...",
        "name": "Ravindra Jadeja",
        "role": "ALL_ROUNDER"
      },
      "bowler": {
        "playerId": "...",
        "name": "Pat Cummins",
        "role": "BOWLER"
      },
      "balls": [
        {
          "overNumber": 0,
          "ballNumber": 0,
          "batter": {...},
          "bowler": {...},
          "runs": 4,
          "wicket": false,
          "extra": false
        }
        // ... more balls
      ],
      "totalRuns": 165,
      "currentOver": 20,
      "currentBallInOver": 0,
      "allOut": false,
      "oversBowled": {
        "Pat Cummins": 4,
        "Mitchell Starc": 4,
        "Josh Hazlewood": 4,
        "Adam Zampa": 4,
        "Glenn Maxwell": 2,
        "Marcus Stoinis": 2
      }
    },
    {
      // Second innings data
    }
  ],
  "tossWinner": null,
  "matchWinner": "India",
  "overs": 20
}
```

---

## Troubleshooting

### Common Issues

**Issue 1: Port 8080 already in use**
```bash
# Solution: Change port in application.properties
echo "server.port=8081" >> src/main/resources/application.properties
```

**Issue 2: Java version mismatch**
```bash
# Check Java version
java -version
# Should be Java 17 or higher
```

**Issue 3: Gradle build fails**
```bash
# Clean and rebuild
./gradlew clean build
```

**Issue 4: Teams not persisting**
- The application uses in-memory storage by default
- Data is lost when the application restarts
- For persistent storage, implement a database repository

---

## Performance Considerations

- **In-Memory Storage**: Fast but limited by available RAM
- **Match Simulation**: T20 matches simulate ~240 balls, ODI ~600 balls
- **Concurrent Requests**: Spring Boot handles multiple requests concurrently
- **Scalability**: For production, consider:
  - Database persistence
  - Caching layer
  - Load balancing
  - Async processing for long simulations

---

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## License

This project is created for educational purposes.

---

## Contact

For questions or suggestions, please open an issue in the repository.

---

## Acknowledgments

- Spring Boot Framework
- Lombok for reducing boilerplate code
- Gradle build system

