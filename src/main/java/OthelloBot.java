/*
 * Copyright (c) Joseph Prichard 2023.
 */

import discord.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import services.BotState;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Level;

import static discord.SimulateHandler.MAX_DELAY;
import static discord.SimulateHandler.MIN_DELAY;
import static models.Player.Bot.MAX_BOT_LEVEL;
import static utils.LogUtils.LOGGER;

public class OthelloBot extends ListenerAdapter {

    private BotState state;

    public void init(JDA jda) {
        state = new BotState(jda);
    }

    public static List<SlashCommandData> getCommandData() {
        return List.of(
            Commands.slash("challenge", "Challenges the bot or another user to an Othello game")
                .addSubcommands(new SubcommandData("user", "Challenges another user to a game")
                    .addOption(OptionType.USER, "opponent", "The opponent to challenge", true))
                .addSubcommands(new SubcommandData("bot", "Challenges the bot to a game")
                    .addOption(OptionType.INTEGER, "level", "Level of the bot between 1 and " + MAX_BOT_LEVEL, false)),
            Commands.slash("accept", "Accepts a challenge from another discord user")
                .addOptions(new OptionData(OptionType.USER, "challenger", "User who made the challenge", true)),
            Commands.slash("forfeit", "Forfeits the user's current game"),
            Commands.slash("move", "Makes a move on user's current game")
                .addOptions(new OptionData(OptionType.STRING, "move", "Move to make on the board", true, true)),
            Commands.slash("view", "Displays the game state including all the moves that can be made this turn"),
            Commands.slash("analyze", "Runs an analysis of the board")
                .addOptions(new OptionData(OptionType.INTEGER, "level", "Level of the bot between 1 and " + MAX_BOT_LEVEL, false)),
            Commands.slash("stats", "Retrieves the stats profile for a player")
                .addOptions(new OptionData(OptionType.USER, "player", "Player to get stats profile for", false)),
            Commands.slash("leaderboard", "Retrieves the highest rated players by ELO"),
            Commands.slash("simulate", "Simulates a game between two bots")
                .addOptions(new OptionData(OptionType.STRING, "black-level",
                    "Level of the bot to play black between 1 and " + MAX_BOT_LEVEL, false))
                .addOptions(new OptionData(OptionType.STRING, "white-level",
                    "Level of the bot to play white between 1 and " + MAX_BOT_LEVEL, false))
                .addOptions(new OptionData(OptionType.INTEGER, "delay",
                    "Delay between moves in seconds between " + MIN_DELAY + " and " + MAX_DELAY + " ms", false))
        );
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        switch (event.getName()) {
            case "move" -> AutoCompleteHandler.handleMove(state, event);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) {
            return;
        }

        BiConsumer<BotState, SlashCommandInteractionEvent> handler = switch (event.getName()) {
            case "challenge" -> ChallengeHandler::handle;
            case "accept" -> CommandHandler::handleAccept;
            case "forfeit" -> CommandHandler::handleForfeit;
            case "move" -> MoveHandler::handle;
            case "view" -> CommandHandler::handleView;
            case "analyze" -> CommandHandler::handleAnalyze;
            case "stats" -> CommandHandler::handleStats;
            case "leaderboard" -> CommandHandler::handleLeaderboard;
            case "simulate" -> SimulateHandler::handle;
            default -> null; // Return null for unknown commands
        };

        if (handler != null) {
            state.getTaskExecutor().submit(() -> {
                try {
                    handler.accept(state, event);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Fatal error during command", ex);
                    event.reply("An unexpected error has occurred.").queue();
                }
            });
        }
    }
}
