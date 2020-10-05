package me.kaotich00.fwtournament.bracket;

import java.util.Objects;
import java.util.UUID;

public class Bracket {

    private String tournamentName;

    private String challongeMatchId;

    private String firstPlayerName;
    private String firstPlayerChallongeId;
    private UUID firstPlayerUUID;

    private String secondPlayerName;
    private String secondPlayerChallongeId;
    private UUID secondPlayerUUID;

    private UUID winner = null;
    private String winnerChallongeId;

    public Bracket(String challongeMatchId, String tournamentName, String firstPlayerName, String firstPlayerChallongeId, UUID firstPlayerUUID, String secondPlayerName, String secondPlayerChallongeId, UUID secondPlayerUUID) {
        this.challongeMatchId = challongeMatchId;
        this.tournamentName = tournamentName;
        this.firstPlayerName = firstPlayerName;
        this.firstPlayerUUID = firstPlayerUUID;
        this.firstPlayerChallongeId = firstPlayerChallongeId;
        this.secondPlayerName = secondPlayerName;
        this.secondPlayerUUID = secondPlayerUUID;
        this.secondPlayerChallongeId = secondPlayerChallongeId;
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

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getChallongeMatchId() {
        return challongeMatchId;
    }

    public void setChallongeMatchId(String challongeMatchId) {
        this.challongeMatchId = challongeMatchId;
    }

    public String getFirstPlayerChallongeId() {
        return firstPlayerChallongeId;
    }

    public void setFirstPlayerChallongeId(String firstPlayerChallongeId) {
        this.firstPlayerChallongeId = firstPlayerChallongeId;
    }

    public String getSecondPlayerChallongeId() {
        return secondPlayerChallongeId;
    }

    public void setSecondPlayerChallongeId(String secondPlayerChallongeId) {
        this.secondPlayerChallongeId = secondPlayerChallongeId;
    }

    public String getWinnerChallongeId() {
        return winnerChallongeId;
    }

    public void setWinnerChallongeId(String winnerChallongeId) {
        this.winnerChallongeId = winnerChallongeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bracket bracket = (Bracket) o;
        return getTournamentName().equals(bracket.getTournamentName()) &&
                getChallongeMatchId().equals(bracket.getChallongeMatchId()) &&
                getFirstPlayerName().equals(bracket.getFirstPlayerName()) &&
                getFirstPlayerChallongeId().equals(bracket.getFirstPlayerChallongeId()) &&
                getFirstPlayerUUID().equals(bracket.getFirstPlayerUUID()) &&
                getSecondPlayerName().equals(bracket.getSecondPlayerName()) &&
                getSecondPlayerChallongeId().equals(bracket.getSecondPlayerChallongeId()) &&
                getSecondPlayerUUID().equals(bracket.getSecondPlayerUUID()) &&
                Objects.equals(getWinner(), bracket.getWinner()) &&
                Objects.equals(getWinnerChallongeId(), bracket.getWinnerChallongeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTournamentName(), getChallongeMatchId(), getFirstPlayerName(), getFirstPlayerChallongeId(), getFirstPlayerUUID(), getSecondPlayerName(), getSecondPlayerChallongeId(), getSecondPlayerUUID(), getWinner(), getWinnerChallongeId());
    }
}
