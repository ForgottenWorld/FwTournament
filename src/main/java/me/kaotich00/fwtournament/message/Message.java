package me.kaotich00.fwtournament.message;

import me.kaotich00.fwtournament.utils.ChatFormatter;
import me.kaotich00.fwtournament.utils.ColorUtil;
import org.bukkit.command.CommandSender;

import static me.kaotich00.fwtournament.utils.ChatFormatter.parseColorMessage;

public enum Message {

    PREFIX(parseColorMessage("[", ColorUtil.colorSecondary) +
            parseColorMessage("FwTournament", ColorUtil.colorSub1) +
            parseColorMessage("]", ColorUtil.colorSecondary), false),

    /* Tournament */
    TOURNAMENT_NEW_SUCCESS(ChatFormatter.formatSuccessMessage("Successfully created tournament \n You can now add, remove players or mofify tournament kit with \n /torneo add <participant_list> \n /torneo remove <participant_list> \n /torneo kit"), true),
    TOURNAMENT_NEW_ERROR(ChatFormatter.formatErrorMessage("A tournament is already created. Skipping."), true),

    TOURNAMENT_ALREADY_GENERATED(ChatFormatter.formatErrorMessage("The tournament is already generated!"), true),
    TOURNAMENT_ADD_PLAYER_VALIDATING(parseColorMessage("Validating Minecraft username for name {}...", ColorUtil.colorSub2), true),
    TOURNAMENT_ADD_PLAYER_DOES_NOT_EXIST(ChatFormatter.formatErrorMessage("The username {} does not exists"), true),
    TOURNAMENT_ADD_PLAYER_SUCCESS(ChatFormatter.formatSuccessMessage("Successfully added {} to participants"), true),
    TOURNAMENT_ADD_PLAYER_ALREADY_PARTICIPANT(ChatFormatter.formatErrorMessage("The player {} is already a participant"), true),

    TOURNAMENT_REMOVE_PLAYER_SUCCESS(ChatFormatter.formatSuccessMessage("Successfully removed {} from participants"), true),
    TOURNAMENT_REMOVE_PLAYER_NOT_PARTICIPANT(ChatFormatter.formatErrorMessage("The player {} is not a participant"), true),

    TOURNAMENT_DELETE_SUCCESS(ChatFormatter.formatSuccessMessage("Tournament deleted"), true),
    TOURNAMENT_DELETE_ERROR(ChatFormatter.formatErrorMessage("No tournament is available at the moment, can't delete."), true),

    TOURNAMENT_GENERATING(parseColorMessage("Generating challonge tournament...", ColorUtil.colorSub2), true),
    TOURNAMENT_GENERATING_ERROR(ChatFormatter.formatErrorMessage("Error while generating tournament. Maybe it is already existent."), true),
    TOURNAMENT_GENERATING_SUCCESS(ChatFormatter.formatSuccessMessage("Successfully generated tournament at link: {}"), true),

    TOURNAMENT_ADDING_PARTICIPANTS(parseColorMessage("Adding participants to Challonge tournament...", ColorUtil.colorSub2), true),
    TOURNAMENT_ADDING_PARTICIPANTS_SUCCESS(ChatFormatter.formatSuccessMessage("Successfully added participants to the tournament"), true),

    TOURNAMENT_RANDOMIZING(parseColorMessage("Randomizing brackets...", ColorUtil.colorSub2), true),
    TOURNAMENT_RANDOMIZE_SUCCESS(ChatFormatter.formatSuccessMessage("Brackets randomized"), true),

    TOURNAMENT_ALREADY_STARTED(ChatFormatter.formatErrorMessage("Tournament already started!"), true),
    TOURNAMENT_STARTING(parseColorMessage("Starting tournament...", ColorUtil.colorSub2), true),
    TOURNAMENT_STARTED(ChatFormatter.formatSuccessMessage("Tournament started!"), true),
    TOURNAMENT_MUST_GENERATE(ChatFormatter.formatErrorMessage("The tournament must be generated before starting it, with the command /torneo generate"), true),
    TOURNAMENT_NOT_FOUND(ChatFormatter.formatErrorMessage("Tournament not found"), true),

    NOT_PART_OF_TOURNAMENT(parseColorMessage("You are not part of the tournament. Setting gamemode to spectator.", ColorUtil.colorSub2), true),
    NO_MATCH_OPEN(parseColorMessage("You are currently part of a tournament, but no match is open at the moment. Gamemode set to spectator mode.", ColorUtil.colorSub2), true),
    WAITING_FOR_OPPONENT(parseColorMessage("Your match will begin as soon as your opponent comes online. Be patient. Gamemode set to spectator mode.", ColorUtil.colorSub2), true),
    NO_FREE_ARENAS(parseColorMessage("You and your opponent are ready to play. Unfortunately there is not a free arena at the moment. The match will began as soon as one arena become free. Gamemode set to spectator mode.", ColorUtil.colorSub2), true),

    /* Arena  */
    ARENA_ALREADY_EXISTS(ChatFormatter.formatErrorMessage("An arena with that name already exists"), true),
    ARENA_CREATION_STARTED(parseColorMessage("Arena creation started", ColorUtil.colorSub2), true),
    ARENA_CREATION_STEP(parseColorMessage("Right click on {}", ColorUtil.colorSub2), true),
    ARENA_CREATION_STEP_COMPLETED(ChatFormatter.formatSuccessMessage("Position selected as {}. Pos X: {}, Pos Y: {}"), true),
    ARENA_CREATION_COMPLETED(ChatFormatter.formatSuccessMessage("Arena creation completed"), true),
    ARENA_NOT_FOUND(ChatFormatter.formatErrorMessage("Arena not found"), true),
    ARENA_DELETED(ChatFormatter.formatSuccessMessage("Arena deleted"), true),
    ARENA_EMPTY(ChatFormatter.formatErrorMessage("No arena to be found"), true);

    private final String message;
    private final boolean showPrefix;

    Message(String message, boolean showPrefix) {
        this.message = ChatFormatter.rewritePlaceholders(message);
        this.showPrefix = showPrefix;
    }

    public void send(CommandSender sender, Object... objects) {
        sender.sendMessage(asString(objects));
    }

    public String asString(Object... objects) {
        return format(objects);
    }

    private String format(Object... objects) {
        String string = this.message;
        if(this.showPrefix) {
            string = PREFIX.message + " " + this.message;
        }
        for (int i = 0; i < objects.length; i++) {
            Object o = objects[i];
            string = string.replace("{" + i + "}", String.valueOf(o));
        }
        return string;
    }

}
