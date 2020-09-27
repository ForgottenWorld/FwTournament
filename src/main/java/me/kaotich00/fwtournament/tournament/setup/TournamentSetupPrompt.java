package me.kaotich00.fwtournament.tournament.setup;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.kit.gui.KitGUI;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TournamentSetupPrompt implements ConversationAbandonedListener {

    private ConversationFactory conversationFactory;
    private Tournament editedTournament;

    private final int ADD_PARTICIPANT = 1;
    private final int REMOVE_PARTICIPANT = 2;
    private final int MODIFY_KIT = 3;
    private final int EXIT_SETUP = 4;

    public TournamentSetupPrompt(Fwtournament plugin, Tournament tournament) {
        this.editedTournament = tournament;
        this.conversationFactory = new ConversationFactory(plugin)
                .withModality(false)
                .withFirstPrompt(new SetupEnterInit())
                .withEscapeSequence("cancel")
                .withTimeout(60)
                .thatExcludesNonPlayersWithMessage("Only players can run this conversation")
                .addConversationAbandonedListener(this);
    }

    public void startConversationForPlayer(Player player) {
        conversationFactory.buildConversation(player).begin();
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
        if (abandonedEvent.gracefulExit()) {
            abandonedEvent.getContext().getForWhom().sendRawMessage(ChatFormatter.formatSuccessMessage("Done"));
        } else {
            abandonedEvent.getContext().getForWhom().sendRawMessage(ChatFormatter.formatErrorMessage("Canceled"));
        }
    }

    private class SetupEnterInit extends FixedSetPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            String promptMessage = ChatFormatter.formatSuccessMessage("\n" + ChatColor.AQUA + "---------------------------------------------------");
            promptMessage = promptMessage.concat( "\n" + ChatColor.DARK_AQUA + "Welcome to tournament setup, Here is the list of actions you can do, type one of the following:" );
            promptMessage = promptMessage.concat( "\n" + ChatColor.AQUA + "---------------------------------------------------" );
            promptMessage = promptMessage.concat( "\n" + ChatColor.GOLD + "(1) " + ChatColor.GREEN + "Add a participant" );
            promptMessage = promptMessage.concat( "\n" + ChatColor.GOLD + "(2) " + ChatColor.GREEN + "Remove a participant" );
            promptMessage = promptMessage.concat( "\n" + ChatColor.GOLD + "(3) " + ChatColor.GREEN + "Modify tournament Kit" );
            promptMessage = promptMessage.concat( "\n" + ChatColor.GOLD + "(4) " + ChatColor.RED + "Exit setup\n" );
            return promptMessage;
        }

        @Override
        protected boolean isInputValid(ConversationContext context, String input) {
            return Integer.parseInt(input) < 4 && Integer.parseInt(input) > 0;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {
            switch(Integer.parseInt(input)) {
                case ADD_PARTICIPANT:
                    return new AddParticipantPrompt();
                case REMOVE_PARTICIPANT:
                    return new RemoveParticipantPrompt();
                case MODIFY_KIT:
                    KitGUI gui = new KitGUI((Player) context.getForWhom(), editedTournament);
                    gui.openGUI();
                    break;
                case EXIT_SETUP:
                    return Prompt.END_OF_CONVERSATION;
            }
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, String invalidInput) {
            return "The input must be a number between 1 and 3";
        }
    }

    private class AddParticipantPrompt extends FixedSetPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            String promptMessage = ChatFormatter.formatSuccessMessage(ChatColor.GRAY + "Please insert a valid nickname to add to the tournament");
            return promptMessage;
        }

        @Override
        protected boolean isInputValid(ConversationContext context, String input) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(input);
            return offlinePlayer != null;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {
            Player sender = (Player) context.getForWhom();

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(input);
            UUID playerUUID = offlinePlayer.getUniqueId();

            SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
            if(simpleTournamentService.addPlayerToTournament(editedTournament.getName(), playerUUID)) {
                sender.sendMessage(ChatFormatter.formatSuccessMessage("Successfully added " + input + " to participants"));
            } else {
                sender.sendMessage(ChatFormatter.formatErrorMessage("The player " + input + " is already a participant"));
            }
            return new SetupEnterInit();
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, String invalidInput) {
            return "The player you specified doesn't exist";
        }

    }

    private class RemoveParticipantPrompt extends FixedSetPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            String promptMessage = ChatFormatter.formatSuccessMessage(ChatColor.GRAY + "Please insert a valid nickname to remove from the tournament");
            return promptMessage;
        }

        @Override
        protected boolean isInputValid(ConversationContext context, String input) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(input);
            return offlinePlayer != null;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {
            Player sender = (Player) context.getForWhom();

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(input);
            UUID playerUUID = offlinePlayer.getUniqueId();

            SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
            if(simpleTournamentService.removePlayerFromTournament(editedTournament.getName(), playerUUID)) {
                sender.sendMessage(ChatFormatter.formatSuccessMessage("Successfully removed " + input + " from participants"));
            } else {
                sender.sendMessage(ChatFormatter.formatErrorMessage("The player " + input + " is not a participant"));
            }
            return new SetupEnterInit();
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, String invalidInput) {
            return "The player you specified doesn't exist";
        }

    }

}
