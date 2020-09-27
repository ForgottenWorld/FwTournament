package me.kaotich00.fwtournament.command.services;

import me.kaotich00.fwtournament.tournament.Tournament;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class SimpleTournamentService {

    private HashMap<String, Tournament> tournamentList;
    private static SimpleTournamentService simpleTournamentService;

    private SimpleTournamentService() {
        if(simpleTournamentService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.tournamentList = new HashMap<>();
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

    public boolean addPlayerToTournament(String tournamentName, UUID uuid) {
        Tournament tournament = tournamentList.get(tournamentName);
        if(tournament.getPlayersList().contains(uuid)) {
            return false;
        }

        tournament.addPlayer(uuid);
        return true;
    }

    public boolean removePlayerFromTournament(String tournamentName, UUID uuid) {
        Tournament tournament = tournamentList.get(tournamentName);
        if(!tournament.getPlayersList().contains(uuid)) {
            return false;
        }

        tournament.removePlayer(uuid);
        return true;
    }

}
