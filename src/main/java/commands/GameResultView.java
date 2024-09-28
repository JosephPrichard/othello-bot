/*
 * Copyright (c) Joseph Prichard 2024.
 */

package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import domain.Tile;
import models.Game;
import models.GameResult;
import models.Player;
import models.StatsResult;

import java.awt.image.BufferedImage;

public class GameResultView {

    private static String getStatsMessage(GameResult gameRes, StatsResult statsRes) {
        return gameRes.winner().name() +
            "'s new rating is " + statsRes.winnerElo() +
            " (" + statsRes.formatWinnerEloDiff() + ") \n" +
            gameRes.loser().name() +
            "'s new rating is " + statsRes.loserElo() +
            " (" + statsRes.formatLoserEloDiff() + ") \n";
    }

    private static String getForfeitMessage(Player winner) {
        return winner.name() + " won by forfeit \n";
    }

    private static String getScoreMessage(int whiteScore, int blackScore) {
        return "Score: " + blackScore + " - " + whiteScore + "\n";
    }

    private static String getMoveMessage(Player winner, String move) {
        return winner.name() + " won with " + move + "\n";
    }

    private static String getTag(GameResult result) {
        var message = "";
        if (!result.winner().isBot()) {
            message = "<@" + result.winner() + "> ";
        } else if (!result.loser().isBot()) {
            message = "<@" + result.loser() + "> ";
        }
        return message;
    }

    public static GameView createGameOverView(GameResult result, StatsResult statsResult, Tile move, Game game, BufferedImage image) {
        var embed = new EmbedBuilder();

        var desc = getMoveMessage(result.winner(), move.toString()) +
            getScoreMessage(game.whiteScore(), game.blackScore()) + "\n" +
            getStatsMessage(result, statsResult);
        embed.setDescription(desc);

        return new GameView(embed)
            .setTitle("Game has ended")
            .setMessage(getTag(result))
            .setImage(image);
    }

    public static GameView createForfeitView(GameResult result, StatsResult statsResult, BufferedImage image) {
        var embed = new EmbedBuilder();

        var desc = getForfeitMessage(result.winner()) + "\n" +
            getStatsMessage(result, statsResult);
        embed.setDescription(desc);

        return new GameView(embed)
            .setTitle("Game has ended")
            .setMessage(getTag(result))
            .setImage(image);
    }

    public static GameView createSimulationView(Game game, Tile move, BufferedImage image) {
        var result = game.createResult();

        var embed = new EmbedBuilder();

        var desc = getMoveMessage(result.winner(), move.toString()) +
            getScoreMessage(game.whiteScore(), game.blackScore());
        embed.setDescription(desc);

        return new GameView(embed)
            .setTitle("Simulation has ended")
            .setMessage(getTag(result))
            .setImage(image);
    }
}