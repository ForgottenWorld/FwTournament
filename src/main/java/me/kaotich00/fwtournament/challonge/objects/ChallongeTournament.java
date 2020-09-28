package me.kaotich00.fwtournament.challonge.objects;

import java.math.BigInteger;

public class ChallongeTournament {

    private BigInteger id;
    private String name;
    private String description;
    private String challongeLink;

    public ChallongeTournament(BigInteger id, String name, String description, String challongeLink) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.challongeLink = challongeLink;
    }

    public BigInteger getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getChallongeLink() {
        return challongeLink;
    }

}
