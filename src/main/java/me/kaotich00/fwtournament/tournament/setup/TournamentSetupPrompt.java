package me.kaotich00.fwtournament.tournament.setup;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.kit.gui.KitGUI;
import me.kaotich00.fwtournament.services.SimpleMojangApiService;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import me.kaotich00.fwtournament.utils.ColorUtil;
import me.kaotich00.fwtournament.utils.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.shanerx.mojang.Mojang;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
            String promptMessage = ChatFormatter.parseColorMessage("\n" + "---------------------------------------------------", ColorUtil.colorPrimary);
            promptMessage = promptMessage.concat( "\n" + ChatFormatter.parseColorMessage("Welcome to tournament setup, Here is the list of actions you can do, type one of the following:", ColorUtil.colorPrimary));
            promptMessage = promptMessage.concat( "\n" + ChatFormatter.parseColorMessage("---------------------------------------------------", ColorUtil.colorPrimary));
            promptMessage = promptMessage.concat( "\n" + ChatFormatter.parseColorMessage("(1) ", ColorUtil.colorSecondary) + ChatFormatter.parseColorMessage("Add a participant", ColorUtil.colorSub1));
            promptMessage = promptMessage.concat( "\n" + ChatFormatter.parseColorMessage("(2) ", ColorUtil.colorSecondary) + ChatFormatter.parseColorMessage("Remove a participant", ColorUtil.colorSub1));
            promptMessage = promptMessage.concat( "\n" + ChatFormatter.parseColorMessage("(3) ", ColorUtil.colorSecondary) + ChatFormatter.parseColorMessage("Modify tournament kit", ColorUtil.colorSub1));
            promptMessage = promptMessage.concat( "\n" + ChatFormatter.parseColorMessage("(4) ", ColorUtil.colorSecondary) + ChatFormatter.parseColorMessage("Exit setup", ColorUtil.colorSub2));
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
            return true;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {
            Player sender = (Player) context.getForWhom();
            CompletableFuture.runAsync(() -> {
                sender.sendMessage(ChatFormatter.formatSuccessMessage("Validanting minecraft username..."));

                UUID playerUUID = SimpleMojangApiService.getInstance().getPlayerUUID(input);
                if(playerUUID!= null) {
                    context.setSessionData("player_uuid", playerUUID);
                }

                SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
                if(simpleTournamentService.addPlayerToTournament(playerUUID, input)) {
                    sender.sendMessage(ChatFormatter.formatSuccessMessage("Successfully added " + input + " to participants"));
                } else {
                    sender.sendMessage(ChatFormatter.formatErrorMessage("The player " + input + " is already a participant"));
                }
            });
            return new SetupEnterInit();
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, String invalidInput) {
            return "The player you specified doesn't exist or the API call limit has been reached";
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
            CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> {
                Mojang api = new Mojang().connect();
                String playerUUID = null;
                try {
                    playerUUID = api.getUUIDOfUsername(input);
                    context.setSessionData("player_uuid", playerUUID);
                } catch (Exception e) {
                    return false;
                }
                if (playerUUID == null) {
                    return false;
                }
                return true;
            });
            Boolean result = false;
            try {
                result = completableFuture.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String input) {
            Player sender = (Player) context.getForWhom();

            SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
            if(simpleTournamentService.removePlayerFromTournament(UUID.fromString(UUIDUtils.parseUUID(context.getSessionData("player_uuid").toString())))) {
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
