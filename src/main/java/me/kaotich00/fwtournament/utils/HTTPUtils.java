package me.kaotich00.fwtournament.utils;

import me.kaotich00.fwtournament.Fwtournament;

public class HTTPUtils {

    public static final String API_KEY = Fwtournament.getDefaultConfig().getString("challonge_api_key");

    public static final String CHALLONGE_CREATE_TOURNAMENT_ENDPOINT = "https://api.challonge.com/v1/tournaments.json";
    public static final String CHALLONGE_ADD_PARTICIPANTS_ENDPOINT = "https://api.challonge.com/v1/tournaments/{tournament}/participants/bulk_add.json";
    public static final String CHALLONGE_START_TOURNAMENT_ENDPOINT = "https://api.challonge.com/v1/tournaments/{tournament}/start.json";
    public static final String CHALLONGE_GET_TOURNAMENT_MATCHES_ENDPOINT = "https://api.challonge.com/v1/tournaments/{tournament}/matches.json";
    public static final String CHALLONGE_GET_MATCH_PARTICIPANTS = "https://api.challonge.com/v1/tournaments/{tournament}/participants/{player}.json";
    public static final String CHALLONGE_POST_MATCH_RESULT = "https://api.challonge.com/v1/tournaments/{tournament}/matches/{match_id}.json";
    public static final String CHALLONGE_END_TOURNAMENT = "https://api.challonge.com/v1/tournaments/{tournament}/finalize.json";
    public static final String CHALLONGE_RANDOMIZE_BRACKET = "https://api.challonge.com/v1/tournaments/{tournament}/participants/randomize.json";
    public static final String CHALLONGE_FIX_USER_NAME = "https://api.challonge.com/v1/tournaments/{tournament}/participants/{participant_id}.json";

}
