package me.kaotich00.fwtournament.tournament.setup;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.command.services.SimpleTournamentService;
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
    private final int SHOW_PARTICIPANTS = 3;
    private final int MODIFY_KIT = 4;

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
            abandonedEvent.getContext().getForWhom().sendRawMessage(ChatFormatter.formatSuccessMessage("Setup completed!"));
        } else {
            abandonedEvent.getContext().getForWhom().sendRawMessage(ChatFormatter.formatErrorMessage("Setup canceled!"));
        }
    }

    private class SetupEnterInit extends FixedSetPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            String promptMessage = ChatFormatter.formatSuccessMessage(ChatColor.GRAY + "Welcome to tournament setup");
            promptMessage = promptMessage.concat( "\n" + "Here is the list of actions you can do:" );
            promptMessage = promptMessage.concat( "\n" + "(1) Add a participant" );
            promptMessage = promptMessage.concat( "\n" + "(2) Remove a participant" );
            promptMessage = promptMessage.concat( "\n" + "(3) Show participant list" );
            promptMessage = promptMessage.concat( "\n" + "(4) Modify tournament Kit" );
            return promptMessage;
        }

        @Override
        protected boolean isInputValid(ConversationContext context, String input) {
            return Integer.parseInt(input) < 5 && Integer.parseInt(input) > 0;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {
            switch(Integer.parseInt(input)) {
                case ADD_PARTICIPANT:
                    return new AddParticipantPrompt();
                case REMOVE_PARTICIPANT:
                    return new RemoveParticipantPrompt();
                case SHOW_PARTICIPANTS:

                    break;
                case MODIFY_KIT:

                    break;
            }
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, String invalidInput) {
            return "The input must be a number between 1 and 4";
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
            return Prompt.END_OF_CONVERSATION;
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
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, String invalidInput) {
            return "The player you specified doesn't exist";
        }

    }

}
