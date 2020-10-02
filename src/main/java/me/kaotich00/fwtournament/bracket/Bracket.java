package me.kaotich00.fwtournament.bracket;

import java.util.UUID;

public class Bracket {

    private String firstPlayerName;
    private UUID firstPlayerUUID;

    private String secondPlayerName;
    private UUID secondPlayerUUID;

    private UUID winner = null;

    public Bracket(String firstPlayerName, UUID firstPlayerUUID, String secondPlayerName, UUID secondPlayerUUID) {
        this.firstPlayerName = firstPlayerName;
        this.firstPlayerUUID = firstPlayerUUID;
        this.secondPlayerName = secondPlayerName;
        this.secondPlayerUUID = secondPlayerUUID;
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

    public UUID getWinner() {
        return winner;
    }

    public void setWinner(UUID winner) {
        this.winner = winner;
    }

}
