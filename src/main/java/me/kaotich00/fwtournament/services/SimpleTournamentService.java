package me.kaotich00.fwtournament.services;

import me.kaotich00.fwtournament.tournament.Tournament;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class SimpleTournamentService {

    private HashMap<String, Tournament> tournamentList;
    private static SimpleTournamentService simpleTournamentService;
    private HashMap<UUID, Tournament> currentModifyingPlayer;

    private SimpleTournamentService() {
        if(simpleTournamentService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.tournamentList = new HashMap<>();
        this.currentModifyingPlayer = new HashMap<>();
    }

    public static SimpleTournamentService getInstance() {
        if(simpleTournamentService == null) {
            simpleTournamentService = new SimpleTournamentService();
        }
        return simpleTournamentService;
    }

    public boolean newTournament(String name) {
        if(tournamentList.containsKey(name)) {
            return false;
        }

        Tournament tournament = new Tournament(name);
        tournamentList.put(name, tournament);

        return true;
    }

    public Optional<Tournament> getTournament(String name) {
        return tournamentList.containsKey(name) ? Optional.of(tournamentList.get(name)) : Optional.empty();
    }

    public boolean addPlayerToTournament(String tournamentName, String playerName) {
        Tournament tournament = tournamentList.get(tournamentName);
        if(tournament.getPlayersList().containsKey(playerName)) {
            return false;
        }

        tournament.addPlayer(playerName, null);
        return true;
    }

    public boolean removePlayerFromTournament(String tournamentName, String playerName) {
        Tournament tournament = tournamentList.get(tournamentName);
        if(!tournament.getPlayersList().containsKey(playerName)) {
            return false;
        }

        tournament.removePlayer(playerName);
        return true;
    }

    public void addModifyingPlayer(UUID player, Tournament tournament) {
        this.currentModifyingPlayer.put(player, tournament);
    }

    public void removeModifyingPlayer(UUID player) {
        this.currentModifyingPlayer.remove(player);
    }

    public Tournament getTournamentByModifyingPlayer(UUID player) {
        return this.currentModifyingPlayer.get(player);
    }

    public HashMap<String, Tournament> getTournamentList() {
        return this.tournamentList;
    }

}
