package me.kaotich00.fwtournament.storage.sqlite;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.challonge.objects.ChallongeTournament;
import me.kaotich00.fwtournament.kit.Kit;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.SerializationUtil;
import org.bukkit.inventory.ItemStack;
import sun.java2d.pipe.SpanShapeRenderer;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

        String[] tablesSql = new String[3];

        tablesSql[0] = "CREATE TABLE IF NOT EXISTS fw_challonge_tournament ("+
                "id  INTEGER NOT NULL," +
                "name  varchar(100) NOT NULL," +
                "challonge_link  varchar(250) NOT NULL," +
                "PRIMARY KEY (id)" +
                ")";

        tablesSql[1] = "CREATE TABLE IF NOT EXISTS fw_tournament ("+
                "name  varchar(50) NOT NULL," +
                "description  varchar(500), " +
                "id_challonge INTEGER," +
                "PRIMARY KEY (name ASC)," +
                "CONSTRAINT fk_fw_tournament_x_fw_challonge_tournament FOREIGN KEY (id_challonge) REFERENCES fw_challonge_tournament (id)" +
                ")";

        tablesSql[2] = "CREATE TABLE IF NOT EXISTS fw_kit ("+
                "itemstack varchar(500)," +
                "tournament_name  varchar(50)," +
                "CONSTRAINT fk_fw_kit_x_fw_tournament FOREIGN KEY (tournament_name) REFERENCES fw_tournament (name)" +
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
                SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
                simpleTournamentService.newTournament(tournamentName);
                Tournament tournament = simpleTournamentService.getTournament(tournamentName).get();

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
                // Creating new tournament
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

            String insertTournamentSql = "INSERT OR IGNORE INTO fw_tournament(name,id_challonge) VALUES(?,?)";
            try (Connection conn = this.connect(plugin, dbName);
                 PreparedStatement pstmt = conn.prepareStatement(insertTournamentSql)) {
                pstmt.setString(1, tournament.getName());
                pstmt.setInt(2, tournament.getChallongeTournament().getId().intValue());
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
        }
    }

}
