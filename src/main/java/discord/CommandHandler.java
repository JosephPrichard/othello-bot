/*
 * Copyright (c) Joseph Prichard 2024.
 */

package discord;

import domain.BoardRenderer;
import models.Challenge;
import models.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import services.BotState;
import services.GameService;
import utils.EventUtils;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static models.Player.Bot.MAX_BOT_LEVEL;
import static utils.LogUtils.LOGGER;
import static utils.StringUtils.leftPad;
import static utils.StringUtils.rightPad;

public class CommandHandler {

    public static void handleAccept(BotState state, SlashCommandInteraction event) {
        var gameService = state.getGameService();
        var challengeScheduler = state.getChallengeScheduler();

        var opponent = Objects.requireNonNull(EventUtils.getPlayerParam(event, "challenger"));
        var player = new Player(event.getUser());

        if (!challengeScheduler.acceptChallenge(new Challenge(player, opponent))) {
            event.reply("No challenge to accept.").queue();
            return;
        }

        try {
            var game = gameService.createGame(player, opponent);
            var image = BoardRenderer.drawBoard(game.getBoard());

            var view = GameView.createGameStartView(game, image);
            EventUtils.replyView(event, view);
        } catch (GameService.AlreadyPlayingException ex) {
            event.reply("One or more players are already in a game.").queue();
        }

        LOGGER.info("Player " + player + " accepted challenge from " + opponent);
    }

    public static void handleAnalyze(BotState state, SlashCommandInteraction event) {
        var gameService = state.getGameService();
        var agentDispatcher = state.getAgentDispatcher();

        var level = EventUtils.getLongParam(event, "level");
        if (level == null) {
            level = 3L;
        }

        // check if level is within range
        if (Player.Bot.isInvalidLevel(level)) {
            event.reply("Invalid level, should be between 1 and " + MAX_BOT_LEVEL).queue();
            return;
        }

        var player = new Player(event.getUser());

        var game = gameService.getGame(player);
        if (game == null) {
            event.reply("You're not currently in a game.").queue();
            return;
        }

        // send starting message, then add queue an agent request, send back the results in a message when it's done
        var depth = Player.Bot.getDepthFromId(level);
        final var finalLevel = level;
        event.reply("Analyzing... Wait a second...")
            .queue(hook -> {
                LOGGER.info("Starting board state analysis");

                try {
                    var future = agentDispatcher.findMoves(game.getBoard(), depth);
                    var rankedMoves = future.get();

                    var image = BoardRenderer.drawBoardAnalysis(game.getBoard(), rankedMoves);
                    var view = GameView.createAnalysisView(game, image, finalLevel, player);

                    view.editUsingHook(hook);
                    LOGGER.info("Finished board state analysis");
                } catch (ExecutionException | InterruptedException e) {
                    LOGGER.warning("Error occurred while responding to an analyze command " + e);
                }
            });
    }

    public static void handleForfeit(BotState state, SlashCommandInteraction event) {
        var gameService = state.getGameService();
        var statsService = state.getStatsService();

        var player = new Player(event.getUser());

        var game = gameService.getGame(player);
        if (game == null) {
            event.reply("You're not currently in a game.").queue();
            return;
        }

        gameService.deleteGame(game);
        var result = game.createForfeitResult(player);

        var statsResult = statsService.writeStats(result);

        var image = BoardRenderer.drawBoard(game.getBoard());
        var view = GameView.createForfeitView(result, statsResult, image);
        EventUtils.replyView(event, view);

        LOGGER.info("Player: " + player + " has forfeited");
    }

    public static void handleLeaderboard(BotState state, SlashCommandInteraction event) {
        var statsList = state.getStatsService().readTopStats();

        var embed = new EmbedBuilder();

        var desc = new StringBuilder();
        desc.append("```");
        var count = 1;
        for (var stats : statsList) {
            desc.append(rightPad(count + ")", 4))
                .append(leftPad(stats.getPlayer().getName(), 32))
                .append(leftPad(String.format("%.2f", stats.elo), 12))
                .append("\n");
            count++;
        }
        desc.append("```");

        embed.setTitle("Leaderboard")
            .setColor(Color.GREEN)
            .setDescription(desc.toString());

        event.replyEmbeds(embed.build()).queue();
        LOGGER.info("Fetched leaderboard");
    }

    public static void handleStats(BotState state, SlashCommandInteraction event) {
        var userOpt = event.getOption("player");
        var user = userOpt != null ? userOpt.getAsUser() : event.getUser();

        var player = new Player(user);

        var stats = state.getStatsService().readStats(player);

        var embed = new EmbedBuilder();
        embed.setColor(Color.GREEN)
            .setTitle(stats.player.getName() + "'s stats")
            .addField("Rating", Float.toString(stats.elo), false)
            .addField("Win Rate", stats.winRate() + "%", false)
            .addField("Won", Integer.toString(stats.won), true)
            .addField("Lost", Integer.toString(stats.lost), true)
            .addField("Drawn", Integer.toString(stats.drawn), true)
            .setThumbnail(user.getAvatarUrl());

        event.replyEmbeds(embed.build()).queue();

        LOGGER.info("Retrieved stats for " + stats.player);
    }

    public static void handleView(BotState state, SlashCommandInteraction event) {
        var gameService = state.getGameService();
        var player = new Player(event.getUser());

        var game = gameService.getGame(player);
        if (game == null) {
            event.reply("You're not currently in a game.").queue();
            return;
        }

        var board = game.getBoard();
        var potentialMoves = board.findPotentialMoves();

        var image = BoardRenderer.drawBoard(board, potentialMoves);
        var view = GameView.createGameView(game, image);
        EventUtils.replyView(event, view);

        LOGGER.info("Player " + player + " view moves in game");
    }
}
