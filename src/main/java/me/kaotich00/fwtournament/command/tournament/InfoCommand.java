package me.kaotich00.fwtournament.command.tournament;

import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.command.api.AdminCommand;
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

            sender.sendMessage(ChatFormatter.parseColorMessage("Tournament name: ", ColorUtil.colorPrimary) +
                                ChatFormatter.parseColorMessage(tournament.getName(), ColorUtil.colorSecondary));

            String status = "created";
            if(tournament.isGenerated()) {
                status = "generated";
            }
            if(tournament.isStarted()) {
                status = "started";
            }

            if(tournament.isGenerated()) {
                sender.sendMessage(ChatFormatter.parseColorMessage("Challonge link: ", ColorUtil.colorPrimary) +
                        ChatFormatter.parseColorMessage(tournament.getChallongeTournament().getChallongeLink(), ColorUtil.colorSecondary));
            }

            sender.sendMessage(ChatFormatter.parseColorMessage("Status: ", ColorUtil.colorPrimary) +
                    ChatFormatter.parseColorMessage(status, ColorUtil.colorSecondary));

            sender.sendMessage(ChatFormatter.parseColorMessage("Matches list:", ColorUtil.colorPrimary));
            for(Bracket bracket: tournament.getBracketsList()) {
                sender.sendMessage( ChatFormatter.parseColorMessage(bracket.getFirstPlayerName(), ColorUtil.colorSub1) +
                                    ChatFormatter.parseColorMessage(" vs ", ColorUtil.colorSecondary) +
                                    ChatFormatter.parseColorMessage(bracket.getSecondPlayerName(), ColorUtil.colorSub1));
            }


        } else {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Error: no tournament with the given name exists"));
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
