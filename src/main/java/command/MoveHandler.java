/*
 * Copyright (c) Joseph Prichard 2024.
 */

package command;

import models.Game;
import models.Player;
import domain.BoardRenderer;
import domain.Tile;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import services.*;
import utils.EventUtils;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static utils.LogUtils.LOGGER;

public class MoveHandler {
    private static GameView buildMoveView(Game game, Tile move) {
        var image = BoardRenderer.drawBoardMoves(game.getBoard());
        return GameView.createGameMoveView(game, move, image);
    }

    private static GameView buildMoveView(Game game) {
        var image = BoardRenderer.drawBoardMoves(game.getBoard());
        return GameView.createGameMoveView(game, image);
    }

    private static GameView onGameOver(BotState state, Game game, Tile move) {
        var result = game.createResult();
        var statsResult = state.getStatsService().writeStats(result);
        var image = BoardRenderer.drawBoard(game.getBoard());
        return GameView.createGameOverView(result, statsResult, move, game, image);
    }

    public static void doBotMove(BotState state, SlashCommandInteraction event, Game game) {
        var currPlayer = game.getCurrentPlayer();
        var depth = Player.Bot.getDepthFromId(currPlayer.id);

        try {
            // queue an agent request which will find the best move, make the move, and send back a response
            var future = state.getAgentDispatcher().findMove(game.getBoard(), depth);
            var bestMove = future.get();

            var newGame = state.getGameService().makeMove(currPlayer, bestMove.tile());

            var view = newGame.isOver() ?
                onGameOver(state, newGame, bestMove.tile()) :
                buildMoveView(newGame, bestMove.tile());

            EventUtils.sendView(event, view);
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warning("Error occurred while waiting for a bot response " + e);
        } catch (GameService.TurnException | GameService.NotPlayingException | GameService.InvalidMoveException e) {
            // this shouldn't happen: the bot should only make legal moves when it is currently it's turn
            // if we get an error like this, the only thing we can do is log it and debug later
            LOGGER.warning("Error occurred after handling a bot move" + e);
        }
    }

    public static void handle(BotState state, SlashCommandInteraction event) {
        var strMove = Objects.requireNonNull(EventUtils.getStringParam(event, "move"));
        var player = new Player(event.getUser());

        var move = Tile.fromNotation(strMove);
        try {
            var game = state.getGameService().makeMove(player, move);

            if (game.isOver()) {
                var view = onGameOver(state, game, move);
                EventUtils.replyView(event, view);
            } else {
                if (game.isAgainstBot()) {
                    var view = buildMoveView(game);
                    EventUtils.replyView(event, view);

                    doBotMove(state, event, game);
                } else {
                    var view = buildMoveView(game, move);
                    EventUtils.replyView(event, view);
                }
            }

            LOGGER.info("Player " + player + " made move on game");
        } catch (GameService.TurnException e) {
            event.reply("It isn't your turn.").queue();
        } catch (GameService.NotPlayingException e) {
            event.reply("You're not currently in a game.").queue();
        } catch (GameService.InvalidMoveException e) {
            event.reply("Can't make a move to " + strMove + ".").queue();
        }
    }
}