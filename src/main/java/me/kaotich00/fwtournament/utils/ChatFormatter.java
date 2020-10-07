package me.kaotich00.fwtournament.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.Collections;
import java.util.Formatter;
import java.util.Locale;

public class ChatFormatter {

    public static String pluginPrefix() {
        return  parseColorMessage("[", ColorUtil.colorSecondary) +
                parseColorMessage("FwTournament", ColorUtil.colorSub1) +
                parseColorMessage("] ", ColorUtil.colorSecondary) +
                ChatColor.RESET;
    }

    public static String chatHeader() {
        return  parseColorMessage("oOo----------------[ ", ColorUtil.colorSecondary) +
                parseColorMessage("FwTournament ", ColorUtil.colorSub1) +
                parseColorMessage(" ]----------------oOo ", ColorUtil.colorSecondary);
    }

    public static String chatFooter() {
        return  ChatColor.AQUA + String.join("", Collections.nCopies(53, "-"));
    }

    public static String formatSuccessMessage(String message) {
        message = parseColorMessage(message, ColorUtil.successColor);
        return message;
    }

    public static String formatErrorMessage(String message) {
        message = parseColorMessage(message, ColorUtil.errorColor);
        return message;
    }

    public static String thousandSeparator(Double value) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.ITALY);
        formatter.format("%,.2f", value);
        return sb.toString();
    }

    public static String helpMessage() {
        String message = chatHeader();
        message = message.concat(
                "\n" + ChatColor.GRAY + ">> " + ChatColor.DARK_AQUA + "/fwt " + ChatColor.AQUA + "check " + ChatColor.GRAY + "[player]" +
                "\n" + ChatColor.GRAY + ">> " + ChatColor.DARK_AQUA + "/fwt " + ChatColor.AQUA + "reload "
        );
        return message;
    }

    public static String parseColorMessage(String message, String hexColor) {
        return ChatColor.of(hexColor) + message;
    }

    public static String rewritePlaceholders(String input) {
        int i = 0;
        while (input.contains("{}")) {
            input = input.replaceFirst("\\{\\}", "{" + i++ + "}");
        }
        return input;
    }

}
