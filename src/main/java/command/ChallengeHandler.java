/*
 * Copyright (c) Joseph Prichard 2024.
 */

package command;

import domain.BoardRenderer;
import lombok.AllArgsConstructor;
import models.Challenge;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import services.BotState;
import services.GameService;
import models.Player;
import utils.EventUtils;

import java.util.Objects;

import static utils.LogUtils.LOGGER;

@AllArgsConstructor
public class ChallengeHandler {

    public static void handle(BotState state, SlashCommandInteraction event) {
        switch (Objects.requireNonNull(event.getSubcommandName())) {
            case "bot" -> handleBot(state, event);
            case "user" -> handleUser(state, event);
            default -> throw new IllegalStateException("Invalid subcommand for the challenge command");
        }
    }

    public static void handleBot(BotState state, SlashCommandInteraction event) {
        var gameService = state.getGameService();

        var level = EventUtils.getLongParam(event, "level");
        if (level == null) {
            level = 3L;
        }

        if (Player.Bot.isInvalidLevel(level)) {
            event.reply("Invalid level. Type !help analyze for valid levels.").queue();
            return;
        }

        var player = new Player(event.getUser());

        try {
            var game = gameService.createBotGame(player, level);
            var image = BoardRenderer.drawBoardMoves(game.getBoard());

            var view = GameView.createGameStartView(game, image);
            EventUtils.replyView(event, view);
        } catch (GameService.AlreadyPlayingException ex) {
            event.reply("You're already in a game").queue();
        }

        LOGGER.info("Player " + player + " challenged the bot");
    }

    public static void handleUser(BotState state, SlashCommandInteraction event) {
        var challengeScheduler = state.getChallengeScheduler();

        var opponent = Objects.requireNonNull(EventUtils.getPlayerParam(event, "opponent"));

        var player = new Player(event.getUser());

        Runnable onExpiry = () -> event.getChannel().sendMessage(player.toAtString() + " Challenge timed out!").queue();
        challengeScheduler.createChallenge(new Challenge(opponent, player), onExpiry);

        var message = opponent.toAtString() +
            ", " +
            player.getName() +
            " has challenged you to a game of Othello. " +
            "Type `/accept` " +
            player.toAtString() +
            ", " +
            "or ignore to decline.";
        event.reply(message).queue();

        LOGGER.info("Player " + player + " challenged opponent " + opponent);
    }
}
