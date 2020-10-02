package me.kaotich00.fwtournament.utils;

import me.kaotich00.fwtournament.Fwtournament;

public class HTTPUtils {

    public static final String API_KEY = Fwtournament.getDefaultConfig().getString("challonge_api_key");

    public static final String CHALLONGE_CREATE_TOURNAMENT_ENDPOINT = "https://api.challonge.com/v1/tournaments.json";
    public static final String CHALLONGE_ADD_PARTICIPANTS_ENDPOINT = "https://api.challonge.com/v1/tournaments/{tournament}/participants/bulk_add.json";
    public static final String CHALLONGE_START_TOURNAMENT_ENDPOINT = "https://api.challonge.com/v1/tournaments/{tournament}/start.json";
    public static final String CHALLONGE_GET_TOURNAMENT_MATCHES_ENDPOINT = "https://api.challonge.com/v1/tournaments/{tournament}/matches.json";
    public static final String CHALLONGE_GET_MATCH_PARTICIPANTS = "https://api.challonge.com/v1/tournaments/{tournament}/participants/{player}.json";

}
