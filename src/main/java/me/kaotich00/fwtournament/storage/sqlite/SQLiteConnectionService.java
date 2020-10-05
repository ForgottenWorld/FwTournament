package me.kaotich00.fwtournament.storage.sqlite;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.arena.Arena;
import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.challonge.objects.ChallongeTournament;
import me.kaotich00.fwtournament.services.SimpleArenaService;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.SerializationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SQLiteConnectionService {

    private static SQLiteConnectionService sqLiteConnectionService;

    private SQLiteConnectionService() {
        if(sqLiteConnectionService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SQLiteConnectionService getInstance() {
        if(sqLiteConnectionService == null) {
            sqLiteConnectionService = new SQLiteConnectionService();
        }
        return sqLiteConnectionService;
    }

    public void createNewDatabase(Fwtournament plugin, String fileName) {

        String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/" + fileName + ".db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setupDefaultTables(Fwtournament plugin, String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/" + fileName + ".db";

        String[] tablesSql = new String[6];

        tablesSql[0] = "CREATE TABLE IF NOT EXISTS fw_challonge_tournament ("+
                "id  INTEGER NOT NULL," +
                "description  varchar(500), " +
                "name  varchar(100) NOT NULL," +
                "challonge_link  varchar(250) NOT NULL," +
                "PRIMARY KEY (id)" +
                ")";

        tablesSql[1] = "CREATE TABLE IF NOT EXISTS fw_tournament ("+
                "name  varchar(50) NOT NULL," +
                "id_challonge INTEGER," +
                "is_started INTEGER," +
                "is_generated INTEGER," +
                "PRIMARY KEY (name ASC)," +
                "CONSTRAINT fk_fw_tournament_x_fw_challonge_tournament FOREIGN KEY (id_challonge) REFERENCES fw_challonge_tournament (id)" +
                ")";

        tablesSql[2] = "CREATE TABLE IF NOT EXISTS fw_kit ("+
                "itemstack varchar(500)," +
                "tournament_name  varchar(50)," +
                "CONSTRAINT fk_fw_kit_x_fw_tournament FOREIGN KEY (tournament_name) REFERENCES fw_tournament (name)" +
                ")";

        tablesSql[3] = "CREATE TABLE IF NOT EXISTS fw_players (" +
                "uuid  varchar(36)," +
                "nickname  varchar(16)," +
                "tournament_name  varchar(50)," +
                "PRIMARY KEY (uuid)," +
                "CONSTRAINT fk_fw_players_x_fw_tournament FOREIGN KEY (tournament_name) REFERENCES fw_tournament (name)" +
                ");";

        tablesSql[4] = "CREATE TABLE IF NOT EXISTS fw_arena (" +
                "name varchar(100)," +
                "player_one_spawn_x  INTEGER," +
                "player_one_spawn_y  INTEGER," +
                "player_one_spawn_z  INTEGER," +
                "player_one_spawn_world  varchar(50)," +
                "player_two_spawn_x  INTEGER," +
                "player_two_spawn_y  INTEGER," +
                "player_two_spawn_z  INTEGER," +
                "player_two_spawn_world  varchar(50)," +
                "player_one_battle_x INTEGER," +
                "player_one_battle_y INTEGER," +
                "player_one_battle_z  INTEGER," +
                "player_one_battle_world  varchar(50)," +
                "player_two_battle_x  INTEGER," +
                "player_two_battle_y  INTEGER," +
                "player_two_battle_z  INTEGER," +
                "player_two_battle_world  varchar," +
                "is_occupied  INTEGER," +
                "PRIMARY KEY (name)" +
                ")";

        tablesSql[5] = "CREATE TABLE IF NOT EXISTS fw_brackets (" +
                "player_one_uuid  varchar(36)," +
                "player_two_uuid  varchar(36)," +
                "player_one_name  varchar(16)," +
                "player_two_name  varchar(16)," +
                "tournament_name  varchar(50)," +
                "player_winner_uuid  varchar(36)," +
                "challonge_match_id  INTEGER," +
                "first_player_challonge_id  varchar(20)," +
                "second_player_challonge_id  varchar(20)," +
                "CONSTRAINT fk_fw_brackets_x_fw_tournament FOREIGN KEY (tournament_name) REFERENCES fw_tournament (name)" +
                ")";


        for(String sql: tablesSql) {
            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement()) {
                // create a new table
                stmt.execute(sql);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Connect to the test.db database
     * @return the Connection object
     */
    private Connection connect(Fwtournament plugin, String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/" + fileName + ".db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void loadTournamentsFromDatabase(Fwtournament plugin, String fileName) {
        // Load Challonge Tournaments from DB
        String sql =    "SELECT" +
                        " tournament.name as tournament_name," +
                        " tournament.is_started as tournament_is_started," +
                        " tournament.is_generated as tournament_is_generated," +
                        " challonge.id as challonge_id," +
                        " challonge.name as challonge_name," +
                        " challonge.description as challonge_description," +
                        " challonge.challonge_link as challonge_link" +
                        " FROM fw_tournament tournament" +
                        " JOIN fw_challonge_tournament challonge ON challonge.id = tournament.id_challonge";
        try (Connection conn = this.connect(plugin, fileName);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                // Creating new tournament
                String tournamentName = rs.getString("tournament_name");
                Boolean tournamentIsStarted = rs.getInt("tournament_is_started") == 1 ? true : false;
                Boolean tournamentIsGenerated = rs.getInt("tournament_is_generated") == 1 ? true : false;
                SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
                simpleTournamentService.newTournament(tournamentName);
                Tournament tournament = simpleTournamentService.getTournament(tournamentName).get();
                tournament.setStarted(tournamentIsStarted);
                tournament.setGenerated(tournamentIsGenerated);

                // Creating new challonge tournament
                Integer challongeTournamentId = rs.getInt("challonge_id");
                String challongeTournamentName = rs.getString("challonge_name");
                String challongeTournamentDescription = rs.getString("challonge_description");
                String challongeTournamentLink = rs.getString("challonge_link");
                ChallongeTournament challongeTournament = new ChallongeTournament(BigInteger.valueOf(challongeTournamentId), challongeTournamentName, challongeTournamentDescription, challongeTournamentLink);

                tournament.setChallongeTournament(challongeTournament);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Load FW Kits from DB
        sql =   "SELECT *" +
                " FROM fw_kit";
        try (Connection conn = this.connect(plugin, fileName);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                String tournamentName = rs.getString("tournament_name");
                String itemStackText = rs.getString("itemstack");

                SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
                simpleTournamentService.newTournament(tournamentName);
                Optional<Tournament> opTournament = simpleTournamentService.getTournament(tournamentName);
                if(opTournament.isPresent()) {
                    Tournament tournament = opTournament.get();
                    ItemStack itemStack = SerializationUtil.fromBase64(itemStackText);
                    tournament.getKit().addItemToKit(itemStack);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load FW Players from DB
        sql =   "SELECT *" +
                " FROM fw_players";
        try (Connection conn = this.connect(plugin, fileName);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                String playerUUID = rs.getString("uuid");
                String playerNickname = rs.getString("nickname");
                String tournamentName = rs.getString("tournament_name");

                SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
                simpleTournamentService.newTournament(tournamentName);
                Optional<Tournament> opTournament = simpleTournamentService.getTournament(tournamentName);
                if(opTournament.isPresent()) {
                    Tournament tournament = opTournament.get();
                    tournament.addPlayer(UUID.fromString(playerUUID), playerNickname);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Load FW Brackets from DB
        sql =   "SELECT *" +
                " FROM fw_brackets";
        try (Connection conn = this.connect(plugin, fileName);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {
                String challongeMatchId = rs.getString("challonge_match_id");
                String firstPlayerUUID = rs.getString("player_one_uuid");
                String secondPlayerUUID = rs.getString("player_two_uuid");
                String firstPlayerName = rs.getString("player_one_name");
                String secondPlayerName = rs.getString("player_two_name");
                String tournamentName = rs.getString("tournament_name");
                String player_winner_uuid = rs.getString("player_winner_uuid");
                String firstPlayerChallongeId = rs.getString("first_player_challonge_id");
                String secondPlayerChallongeId = rs.getString("second_player_challonge_id");

                SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
                simpleTournamentService.newTournament(tournamentName);
                Optional<Tournament> opTournament = simpleTournamentService.getTournament(tournamentName);
                if(opTournament.isPresent()) {
                    Tournament tournament = opTournament.get();

                    Bracket bracket = SimpleTournamentService.getInstance().pushNewBracket(challongeMatchId, tournament, firstPlayerName, UUID.fromString(firstPlayerUUID), secondPlayerName, UUID.fromString(secondPlayerUUID), firstPlayerChallongeId, secondPlayerChallongeId);

                    if(player_winner_uuid != null) {
                        bracket.setWinner(UUID.fromString(player_winner_uuid));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Load FW Arenas from DB
        sql =   "SELECT *" +
                " FROM fw_arena";
        try (Connection conn = this.connect(plugin, fileName);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {
                String arenaName = rs.getString("name");

                int playerOneSpawnX = rs.getInt("player_one_spawn_x");
                int playerOneSpawnY = rs.getInt("player_one_spawn_y");
                int playerOneSpawnZ = rs.getInt("player_one_spawn_z");
                String playerOneSpawnWorld = rs.getString("player_one_spawn_world");

                int playerTwoSpawnX = rs.getInt("player_two_spawn_x");
                int playerTwoSpawnY = rs.getInt("player_two_spawn_y");
                int playerTwoSpawnZ = rs.getInt("player_two_spawn_z");
                String playerTwoSpawnWorld = rs.getString("player_two_spawn_world");

                int playerOneBattleX = rs.getInt("player_one_battle_x");
                int playerOneBattleY = rs.getInt("player_one_battle_y");
                int playerOneBattleZ = rs.getInt("player_one_battle_z");
                String playerOneBattleWorld = rs.getString("player_one_battle_world");

                int playerTwoBattleX = rs.getInt("player_two_battle_x");
                int playerTwoBattleY = rs.getInt("player_two_battle_y");
                int playerTwoBattleZ = rs.getInt("player_two_battle_z");
                String playerTwoBattleWorld = rs.getString("player_two_battle_world");

                Location playerOneSpawn = new Location(Bukkit.getServer().getWorld(playerOneSpawnWorld), playerOneSpawnX, playerOneSpawnY, playerOneSpawnZ);
                Location playerTwoSpawn = new Location(Bukkit.getServer().getWorld(playerTwoSpawnWorld), playerTwoSpawnX, playerTwoSpawnY, playerTwoSpawnZ);

                Location playerOneBattle = new Location(Bukkit.getServer().getWorld(playerOneBattleWorld), playerOneBattleX, playerOneBattleY, playerOneBattleZ);
                Location playerTwoBattle = new Location(Bukkit.getServer().getWorld(playerTwoBattleWorld), playerTwoBattleX, playerTwoBattleY, playerTwoBattleZ);

                SimpleArenaService.getInstance().newArena(arenaName, playerOneSpawn, playerTwoSpawn, playerOneBattle, playerTwoBattle);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveTournamentsToDatabase(Fwtournament plugin, String dbName) {
        HashMap<String, Tournament> tournamentsList = SimpleTournamentService.getInstance().getTournamentList();
        for (Map.Entry<String, Tournament> entry : tournamentsList.entrySet()) {
            Tournament tournament = entry.getValue();
            ChallongeTournament challongeTournament = tournament.getChallongeTournament();

            String insertChallongeTournamentSql = "INSERT OR IGNORE INTO fw_challonge_tournament(id,name,description,challonge_link) VALUES(?,?,?,?)";
            try (Connection conn = this.connect(plugin, dbName);
                 PreparedStatement pstmt = conn.prepareStatement(insertChallongeTournamentSql)) {
                pstmt.setInt(1, challongeTournament.getId().intValue());
                pstmt.setString(2, challongeTournament.getName());
                pstmt.setString(3, challongeTournament.getDescription());
                pstmt.setString(4, challongeTournament.getChallongeLink());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            String insertTournamentSql = "INSERT OR IGNORE INTO fw_tournament(name,id_challonge,is_started,is_generated) VALUES(?,?,?,?)";
            try (Connection conn = this.connect(plugin, dbName);
                 PreparedStatement pstmt = conn.prepareStatement(insertTournamentSql)) {
                pstmt.setString(1, tournament.getName());
                pstmt.setInt(2, tournament.getChallongeTournament().getId().intValue());
                pstmt.setInt(3, tournament.isStarted() ? 1 : 0);
                pstmt.setInt(4, tournament.isGenerated() ? 1 : 0);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            String deleteKitSql = "DELETE FROM fw_kit";
            try (Connection conn = this.connect(plugin, dbName);
                 PreparedStatement pstmt = conn.prepareStatement(deleteKitSql)) {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            String insertKitsSql = "INSERT INTO fw_kit(itemstack,tournament_name) VALUES(?,?)";
            try (Connection conn = this.connect(plugin, dbName);
                 PreparedStatement pstmt = conn.prepareStatement(insertKitsSql)) {
                for(ItemStack itemStack: tournament.getKit().getItemsList()) {
                    pstmt.setString(1, SerializationUtil.toBase64(itemStack));
                    pstmt.setString(2, tournament.getName());
                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            String insertPlayersSql = "INSERT OR IGNORE INTO fw_players(uuid,nickname,tournament_name) VALUES(?,?,?)";
            try (Connection conn = this.connect(plugin, dbName);
                 PreparedStatement pstmt = conn.prepareStatement(insertPlayersSql)) {
                for (Map.Entry<UUID, String> player : tournament.getPlayersList().entrySet()) {
                    String playerUUID = player.getKey().toString();
                    String playerName = player.getValue();
                    pstmt.setString(1, playerUUID);
                    pstmt.setString(2, playerName);
                    pstmt.setString(3, tournament.getName());
                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            String deleteBracketSql = "DELETE FROM fw_brackets";
            try (Connection conn = this.connect(plugin, dbName);
                 PreparedStatement pstmt = conn.prepareStatement(deleteBracketSql)) {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            String insertBracketSql = "INSERT OR IGNORE INTO fw_brackets(player_one_uuid,player_two_uuid,player_one_name,player_two_name,tournament_name,player_winner_uuid,challonge_match_id, first_player_challonge_id, second_player_challonge_id) VALUES (?,?,?,?,?,?,?,?,?)";
            try (Connection conn = this.connect(plugin, dbName);
                 PreparedStatement pstmt = conn.prepareStatement(insertBracketSql)) {
                for (Bracket bracket: tournament.getBracketsList()) {
                    pstmt.setString(1, bracket.getFirstPlayerUUID().toString());
                    pstmt.setString(2, bracket.getSecondPlayerUUID().toString());
                    pstmt.setString(3, bracket.getFirstPlayerName());
                    pstmt.setString(4, bracket.getSecondPlayerName());
                    pstmt.setString(5, tournament.getName());
                    pstmt.setString(6, bracket.getWinner() != null ? bracket.getWinner().toString() : null);
                    pstmt.setInt(7, Integer.parseInt(bracket.getChallongeMatchId()));
                    pstmt.setString(8, bracket.getFirstPlayerChallongeId());
                    pstmt.setString(9, bracket.getSecondPlayerChallongeId());

                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            String insertArenaSql = "INSERT OR IGNORE INTO fw_arena(name, " +
                                    "player_one_spawn_x, player_one_spawn_y, player_one_spawn_z, player_one_spawn_world," +
                                    "player_two_spawn_x, player_two_spawn_y, player_two_spawn_z, player_two_spawn_world," +
                                    "player_one_battle_x, player_one_battle_y, player_one_battle_z, player_one_battle_world," +
                                    "player_two_battle_x, player_two_battle_y, player_two_battle_z, player_two_battle_world) " +
                                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            try (Connection conn = this.connect(plugin, dbName);
                 PreparedStatement pstmt = conn.prepareStatement(insertArenaSql)) {
                for (Map.Entry<String, Arena> arenaEntry : SimpleArenaService.getInstance().getArenas().entrySet()) {
                    String arenaName = arenaEntry.getKey();
                    Arena arena = arenaEntry.getValue();

                    int playerOneSpawnX = arena.getPlayerOneSpawn().getBlockX();
                    int playerOneSpawnY = arena.getPlayerOneSpawn().getBlockY();
                    int playerOneSpawnZ = arena.getPlayerOneSpawn().getBlockZ();
                    String playerOneSpawnWorld = arena.getPlayerOneSpawn().getWorld().getName();

                    int playerTwoSpawnX = arena.getPlayerTwoSpawn().getBlockX();
                    int playerTwoSpawnY = arena.getPlayerTwoSpawn().getBlockY();
                    int playerTwoSpawnZ = arena.getPlayerTwoSpawn().getBlockZ();
                    String playerTwoSpawnWorld = arena.getPlayerTwoSpawn().getWorld().getName();

                    int playerOneBattleX = arena.getPlayerOneBattle().getBlockX();
                    int playerOneBattleY = arena.getPlayerOneBattle().getBlockY();
                    int playerOneBattleZ = arena.getPlayerOneBattle().getBlockZ();
                    String playerOneBattleWorld = arena.getPlayerOneBattle().getWorld().getName();

                    int playerTwoBattleX = arena.getPlayerTwoBattle().getBlockX();
                    int playerTwoBattleY = arena.getPlayerTwoBattle().getBlockY();
                    int playerTwoBattleZ = arena.getPlayerTwoBattle().getBlockZ();
                    String playerTwoBattleWorld = arena.getPlayerTwoBattle().getWorld().getName();

                    pstmt.setString(1, arenaName);

                    pstmt.setInt(2, playerOneSpawnX);
                    pstmt.setInt(3, playerOneSpawnY);
                    pstmt.setInt(4, playerOneSpawnZ);
                    pstmt.setString(5, playerOneSpawnWorld);

                    pstmt.setInt(6, playerTwoSpawnX);
                    pstmt.setInt(7, playerTwoSpawnY);
                    pstmt.setInt(8, playerTwoSpawnZ);
                    pstmt.setString(9, playerTwoSpawnWorld);

                    pstmt.setInt(10, playerOneBattleX);
                    pstmt.setInt(11, playerOneBattleY);
                    pstmt.setInt(12, playerOneBattleZ);
                    pstmt.setString(13, playerOneBattleWorld);

                    pstmt.setInt(14, playerTwoBattleX);
                    pstmt.setInt(15, playerTwoBattleY);
                    pstmt.setInt(16, playerTwoBattleZ);
                    pstmt.setString(17, playerTwoBattleWorld);

                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void deleteTournament(Fwtournament plugin, String dbName, Tournament tournament) {
        String deleteKitSql = "DELETE FROM fw_kit WHERE tournament_name = ?";
        String deletePlayersSql = "DELETE FROM fw_players WHERE tournament_name = ?";
        String deleteBracktsSql = "DELETE FROM fw_brackets WHERE tournament_name = ?";
        String deleteChallongeTournament = "DELETE FROM fw_challonge_tournament WHERE id = (SELECT id_challonge FROM fw_tournament WHERE name = ? LIMIT 1)";
        String deleteTournament = "DELETE FROM fw_tournament WHERE name = ?";

        try (Connection conn = this.connect(plugin, dbName)) {
            PreparedStatement pstmt = conn.prepareStatement(deleteKitSql);
            pstmt.setString(1, tournament.getName());
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement(deletePlayersSql);
            pstmt.setString(1, tournament.getName());
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement(deleteBracktsSql);
            pstmt.setString(1, tournament.getName());
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement(deleteChallongeTournament);
            pstmt.setString(1, tournament.getName());
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement(deleteTournament);
            pstmt.setString(1, tournament.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
