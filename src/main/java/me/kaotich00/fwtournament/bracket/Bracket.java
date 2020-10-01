package me.kaotich00.fwtournament.bracket;

import java.util.UUID;

public class Bracket {

    private String firstPlayerName;

    private UUID firstPlayerUUID;

    private String secondPlayerName;
    private UUID secondPlayerUUID;

    public Bracket(String firstPlayerName, String secondPlayerName) {
        this.firstPlayerName = firstPlayerName;
        this.secondPlayerName = secondPlayerName;
    }

    public String getFirstPlayerName() {
        return firstPlayerName;
    }

    public void setFirstPlayerName(String firstPlayerName) {
        this.firstPlayerName = firstPlayerName;
    }

    public UUID getFirstPlayerUUID() {
        return firstPlayerUUID;
    }

    public void setFirstPlayerUUID(UUID firstPlayerUUID) {
        this.firstPlayerUUID = firstPlayerUUID;
    }

    public String getSecondPlayerName() {
        return secondPlayerName;
    }

    public void setSecondPlayerName(String secondPlayerName) {
        this.secondPlayerName = secondPlayerName;
    }

    public UUID getSecondPlayerUUID() {
        return secondPlayerUUID;
    }

    public void setSecondPlayerUUID(UUID secondPlayerUUID) {
        this.secondPlayerUUID = secondPlayerUUID;
    }

}
