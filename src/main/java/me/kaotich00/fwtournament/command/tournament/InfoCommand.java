package me.kaotich00.fwtournament.command.tournament;

import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.message.Message;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import me.kaotich00.fwtournament.utils.ColorUtil;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class InfoCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
        Optional<Tournament> optTournament = simpleTournamentService.getTournament();

        if(optTournament.isPresent()) {
            Tournament tournament = optTournament.get();
            sender.sendMessage(ChatFormatter.chatHeader());

            sender.sendMessage(ChatFormatter.parseColorMessage("Tournament name: ", ColorUtil.colorSub2) +
                                ChatFormatter.parseColorMessage(tournament.getName(), ColorUtil.successColor));

            String status = "created";
            if(tournament.isGenerated()) {
                status = "generated";
            }
            if(tournament.isStarted()) {
                status = "started";
            }

            if(tournament.isGenerated()) {
                sender.sendMessage(ChatFormatter.parseColorMessage("Challonge link: ", ColorUtil.colorSub2) +
                        ChatFormatter.parseColorMessage(tournament.getChallongeTournament().getChallongeLink(), ColorUtil.successColor));
            }

            sender.sendMessage(ChatFormatter.parseColorMessage("Status: ", ColorUtil.colorSub2) +
                    ChatFormatter.parseColorMessage(status, ColorUtil.successColor));

            sender.sendMessage(ChatFormatter.parseColorMessage("Matches list:", ColorUtil.colorSub2));
            for(Bracket bracket: tournament.getBracketsList()) {
                String firstPlayer = ChatFormatter.parseColorMessage(bracket.getFirstPlayerName(), ColorUtil.colorSub1);
                String vs = ChatFormatter.parseColorMessage(" vs ", ColorUtil.colorSecondary);
                String secondPlayer = ChatFormatter.parseColorMessage(bracket.getSecondPlayerName(), ColorUtil.colorSub1);
                String result = ChatFormatter.parseColorMessage("(0-0)", ColorUtil.colorSecondary);
                if(bracket.getWinner() != null) {
                    if(bracket.getWinner().equals(bracket.getFirstPlayerUUID())) {
                        firstPlayer = ChatFormatter.parseColorMessage(bracket.getFirstPlayerName(), ColorUtil.successColor);
                        secondPlayer = ChatFormatter.parseColorMessage(bracket.getSecondPlayerName(), ColorUtil.colorPrimary);
                        result = ChatFormatter.parseColorMessage("(1-0)", ColorUtil.colorSecondary);
                    } else if(bracket.getWinner().equals(bracket.getSecondPlayerUUID())){
                        firstPlayer = ChatFormatter.parseColorMessage(bracket.getFirstPlayerName(), ColorUtil.colorPrimary);
                        secondPlayer = ChatFormatter.parseColorMessage(bracket.getSecondPlayerName(), ColorUtil.successColor);
                        result = ChatFormatter.parseColorMessage("(0-1)", ColorUtil.colorSecondary);
                    }
                }
                sender.sendMessage( firstPlayer + " " + vs + " " + secondPlayer + " " + result);
            }


        } else {
            Message.TOURNAMENT_NOT_FOUND.send(sender);
        }
    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/torneo info";
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public Integer getRequiredArgs() {
        return 1;
    }

}
