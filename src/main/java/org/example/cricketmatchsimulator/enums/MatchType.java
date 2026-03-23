package org.example.cricketmatchsimulator.enums;

import lombok.Getter;

@Getter
public enum MatchType {
    T20(20),
    ODI(50),
    TEST(Integer.MAX_VALUE);

    private final int overs;

    MatchType(int overs){
        this.overs = overs;
    }

}
