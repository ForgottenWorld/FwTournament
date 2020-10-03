package me.kaotich00.fwtournament.challonge;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.challonge.objects.ChallongeTournament;
import me.kaotich00.fwtournament.http.HTTPClient;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.HTTPUtils;
import me.kaotich00.fwtournament.utils.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.shanerx.mojang.Mojang;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ChallongeIntegrationFactory {

    public static ChallongeTournament createTournament(Player sender, String name, String description, String tournamentType, String openSignup, String challongeUrl) throws ParseException {
        Multimap<String,String> postDataParams = ArrayListMultimap.create();
        postDataParams.put("api_key", HTTPUtils.API_KEY);
        postDataParams.put("tournament[name]", name);
        postDataParams.put("tournament[tournament_type]", tournamentType);
        postDataParams.put("tournament[description]", description);
        postDataParams.put("tournament[open_signup]", openSignup);
        postDataParams.put("tournament[url]", challongeUrl);

        String URI = HTTPUtils.CHALLONGE_CREATE_TOURNAMENT_ENDPOINT;
        String requestMethod = "POST";

        String response = HTTPClient.fetchHttpRequest(URI, requestMethod, postDataParams, sender);

        JSONParser parser = new JSONParser();
        JSONObject responseData = null;
        try {
            responseData = (JSONObject) parser.parse(response);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if( responseData == null ) {
            Optional<Tournament> optTournament = SimpleTournamentService.getInstance().getTournament(name);
            if(optTournament.isPresent()) {
                return optTournament.get().getChallongeTournament();
            }
            return null;
        }

        JSONObject tournamentData = (JSONObject) responseData.get("tournament");
        String challongeID = tournamentData.get("id").toString();
        String challongeName = tournamentData.get("name").toString();
        String challongeDescription = tournamentData.get("description").toString();
        String challongeLink = tournamentData.get("full_challonge_url").toString();

        ChallongeTournament tournament = new ChallongeTournament(BigInteger.valueOf(Long.parseLong(challongeID)), challongeName, challongeDescription, challongeLink);
        return tournament;
    }

    public static void addParticipantsToTournament(Player sender, Tournament tournament) throws ParseException {
        ChallongeTournament challongeTournament = tournament.getChallongeTournament();

        Multimap<String,String> postDataParams = ArrayListMultimap.create();
        postDataParams.put("api_key", HTTPUtils.API_KEY);
        for(Map.Entry<UUID,String> entry : tournament.getPlayersList().entrySet()) {
            postDataParams.put("participants[][name]", entry.getValue());
        }

        String URI = HTTPUtils.CHALLONGE_ADD_PARTICIPANTS_ENDPOINT.replace("{tournament}",challongeTournament.getId().toString());
        String requestMethod = "POST";
        String response = HTTPClient.fetchHttpRequest(URI, requestMethod,postDataParams, sender);

        JSONParser parser = new JSONParser();
        JSONArray responseData = null;
        try {
            responseData = (JSONArray) parser.parse(response);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void startTournament(Player sender, Tournament tournament) throws ParseException {
        ChallongeTournament challongeTournament = tournament.getChallongeTournament();

        Multimap<String,String> postDataParams = ArrayListMultimap.create();
        postDataParams.put("api_key", HTTPUtils.API_KEY);

        String URI = HTTPUtils.CHALLONGE_START_TOURNAMENT_ENDPOINT.replace("{tournament}",challongeTournament.getId().toString());
        String requestMethod = "POST";

        String response = HTTPClient.fetchHttpRequest(URI, requestMethod, postDataParams, sender);
        JSONParser parser = new JSONParser();
        JSONObject responseData = null;
        try {
            responseData = (JSONObject) parser.parse(response);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void getTournamentBrackets(Player sender, Tournament tournament) throws ParseException {
        ChallongeTournament challongeTournament = tournament.getChallongeTournament();
        tournament.clearBrackets();

        Multimap<String,String> postDataParams = ArrayListMultimap.create();
        postDataParams.put("api_key", HTTPUtils.API_KEY);
        postDataParams.put("state", "open");

        String URI = HTTPUtils.CHALLONGE_GET_TOURNAMENT_MATCHES_ENDPOINT.replace("{tournament}",challongeTournament.getId().toString());
        String requestMethod = "GET";

        String response = HTTPClient.fetchHttpRequest(URI, requestMethod, postDataParams, sender);
        JSONParser parser = new JSONParser();
        JSONArray responseData = null;
        try {
            responseData = (JSONArray) parser.parse(response);

            // If response size is empty
            // the tournament has ended
            // Therefore the winner is announced
            if(responseData.size() == 0) {
                // Close the tournament
                endTournament(sender, tournament);

                Bukkit.getServer().broadcastMessage("The tournament has ended");
            }

            for(int i = 0; i < responseData.size(); i++) {
                JSONObject match = (JSONObject) responseData.get(i);
                match = (JSONObject) match.get("match");

                String matchId = match.get("id").toString();
                String playerOneId = match.get("player1_id").toString();
                String playerTwoId = match.get("player2_id").toString();

                String playerOneName = getParticipantName(sender, tournament, playerOneId);
                String playerTwoName = getParticipantName(sender, tournament, playerTwoId);

                Mojang api = new Mojang().connect();

                UUID playerOneUUID = UUID.fromString(UUIDUtils.parseUUID(api.getUUIDOfUsername(playerOneName)));
                UUID playerTwoUUID = UUID.fromString(UUIDUtils.parseUUID(api.getUUIDOfUsername(playerTwoName)));

                SimpleTournamentService.getInstance().pushNewBracket(matchId, tournament, playerOneName, playerOneUUID, playerTwoName, playerTwoUUID, playerOneId, playerTwoId);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static String getParticipantName(Player sender, Tournament tournament, String playerId) throws ParseException {
        ChallongeTournament challongeTournament = tournament.getChallongeTournament();

        Multimap<String,String> postDataParams = ArrayListMultimap.create();
        postDataParams.put("api_key", HTTPUtils.API_KEY);

        String URI = HTTPUtils.CHALLONGE_GET_MATCH_PARTICIPANTS.replace("{tournament}",challongeTournament.getId().toString()).replace("{player}", playerId);
        String requestMethod = "GET";

        String response = HTTPClient.fetchHttpRequest(URI, requestMethod, postDataParams, sender);
        JSONParser parser = new JSONParser();
        JSONObject responseData = null;
        try {
            responseData = (JSONObject) parser.parse(response);
        } catch(Exception e) {
            e.printStackTrace();
        }

        JSONObject playerData = (JSONObject) responseData.get("participant");
        String playerName = playerData.get("name").toString();
        return playerName;
    }

    public static void updateMatchResult(Player sender, Tournament tournament, Bracket bracket) throws ParseException {
        ChallongeTournament challongeTournament = tournament.getChallongeTournament();

        Multimap<String,String> postDataParams = ArrayListMultimap.create();
        postDataParams.put("api_key", HTTPUtils.API_KEY);
        if(bracket.getWinner().equals(bracket.getFirstPlayerUUID())) {
            postDataParams.put("match[scores_csv]", "1-0");
        } else {
            postDataParams.put("match[scores_csv]", "0-1");
        }
        postDataParams.put("match[winner_id]", bracket.getWinnerChallongeId());

        String URI = HTTPUtils.CHALLONGE_POST_MATCH_RESULT.replace("{tournament}",challongeTournament.getId().toString()).replace("{match_id}", bracket.getChallongeMatchId());
        String requestMethod = "PUT";

        HTTPClient.fetchHttpRequest(URI, requestMethod, postDataParams, sender);
    }

    public static void endTournament(Player sender, Tournament tournament) throws ParseException {
        ChallongeTournament challongeTournament = tournament.getChallongeTournament();

        Multimap<String,String> postDataParams = ArrayListMultimap.create();
        postDataParams.put("api_key", HTTPUtils.API_KEY);

        String URI = HTTPUtils.CHALLONGE_END_TOURNAMENT.replace("{tournament}",challongeTournament.getId().toString());
        String requestMethod = "POST";

        String response = HTTPClient.fetchHttpRequest(URI, requestMethod, postDataParams, sender);
        JSONParser parser = new JSONParser();
        JSONObject responseData = null;
        try {
            responseData = (JSONObject) parser.parse(response);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
