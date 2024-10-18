/*
 * Copyright (c) Joseph Prichard 2024.
 */

package command;

import domain.Tile;
import lombok.Getter;
import models.Game;
import models.Player;
import models.Stats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Getter
public class GameView {

    private final EmbedBuilder embed;
    private BufferedImage image;
    private String message = "";

    public GameView(EmbedBuilder embed) {
        this.embed = embed;
        this.embed.setColor(Color.GREEN);
    }

    public GameView setImage(BufferedImage image) {
        this.image = image;
        return this;
    }

    public GameView setMessage(String message) {
        this.message = message;
        return this;
    }

    public GameView setTitle(String title) {
        embed.setTitle(title);
        return this;
    }

    public void editUsingHook(InteractionHook hook) {
        try {
            var embed = getEmbed();
            var message = getMessage();

            var os = new ByteArrayOutputStream();
            ImageIO.write(getImage(), "png", os);
            var is = new ByteArrayInputStream(os.toByteArray());

            embed.setImage("attachment://image.png");

            hook.editOriginalEmbeds(embed.build())
                .retainFilesById(new long[]{}) // retain none of the ids: aka get rid of all the files
                .addFile(is, "image.png")
                .queue();
            if (!message.isEmpty()) {
                hook.editOriginal(message).queue();
            }
        } catch (IOException ex) {
            hook.editOriginal("Unexpected error: couldn't create image").queue();
        }
    }

    private static String getScoreText(Game game) {
        return "Black: " + game.getBlackScore() + " points \n" + "White: " + game.getWhiteScore() + " points \n";
    }

    private static String getTag(Player player) {
        return player.isBot() ? "" : player.toAtString();
    }

    public static GameView createGameStartView(Game game, BufferedImage image) {
        var desc = "Black: " + game.getBlackPlayer().getName() + "\n " +
            "White: " + game.getWhitePlayer().getName() + "\n " +
            "Use `/view` to view the game and use `/move` to make a move.";
        var embed = new EmbedBuilder()
            .setTitle("Game started!")
            .setDescription(desc);

        return new GameView(embed).setImage(image);
    }

    public static GameView createSimulationStartView(Game game, BufferedImage image) {
        var desc = "Black: " + game.getBlackPlayer().getName() + "\n " +
            "White: " + game.getWhitePlayer().getName();
        var embed = new EmbedBuilder()
            .setTitle("Simulation started!")
            .setDescription(desc);

        return new GameView(embed).setImage(image);
    }

    public static GameView createGameMoveView(Game game, Tile move, BufferedImage image) {
        var desc = getScoreText(game) + "Your opponent has moved: " + move;

        var embed = new EmbedBuilder()
            .setTitle("Your game with " + game.getOtherPlayer().getName())
            .setDescription(desc)
            .setFooter(game.isBlackMove() ? "Black to move" : "White to move");

        return new GameView(embed)
            .setMessage(getTag(game.getCurrentPlayer()))
            .setImage(image);
    }

    public static GameView createSimulationView(Game game, Tile move, BufferedImage image) {
        var desc = getScoreText(game) + game.getOtherPlayer().getName() + " has moved: " + move;

        var embed = new EmbedBuilder()
            .setTitle(game.getBlackPlayer().getName() + " vs " + game.getWhitePlayer().getName())
            .setDescription(desc)
            .setFooter(game.isBlackMove() ? "Black to move" : "White to move");

        return new GameView(embed).setImage(image);
    }

    public static GameView createGameMoveView(Game game, BufferedImage image) {
        return createGameView(game, image, getTag(game.getCurrentPlayer()));
    }

    public static GameView createGameView(Game game, BufferedImage image) {
        return createGameView(game, image, "");
    }

    public static GameView createGameView(Game game, BufferedImage image, String message) {
        var desc = getScoreText(game) + game.getCurrentPlayer().getName() + " to move";

        var embed = new EmbedBuilder()
            .setTitle(game.getBlackPlayer().getName() + " vs " + game.getWhitePlayer().getName())
            .setDescription(desc)
            .setFooter(game.isBlackMove() ? "Black to move" : "White to move");

        return new GameView(embed)
            .setMessage(message)
            .setImage(image);
    }

    public static GameView createAnalysisView(Game game, BufferedImage image, long level, Player player) {
        var desc = getScoreText(game);

        var embed = new EmbedBuilder()
            .setTitle("Game Analysis using bot level " + level)
            .setDescription(desc)
            .setFooter("Positive heuristics are better for black, and negative heuristics are better for white");

        return new GameView(embed)
            .setMessage(getTag(player))
            .setImage(image);
    }

    private static String getStatsMessage(Game.Result gameRes, Stats.Result statsRes) {
        return gameRes.winner().getName() +
            "'s new rating is " + statsRes.winnerElo() +
            " (" + statsRes.formatWinnerEloDiff() + ") \n" +
            gameRes.loser().getName() +
            "'s new rating is " + statsRes.loserElo() +
            " (" + statsRes.formatLoserEloDiff() + ") \n";
    }

    private static String getForfeitMessage(Player winner) {
        return winner.getName()+ " won by forfeit \n";
    }

    private static String getScoreMessage(int whiteScore, int blackScore) {
        return "Score: " + blackScore + " - " + whiteScore + "\n";
    }

    private static String getMoveMessage(Player winner, String move) {
        return winner.getName() + " won with " + move + "\n";
    }

    private static String getTag(Game.Result result) {
        var message = "";
        if (!result.winner().isBot()) {
            message = result.winner().toAtString();
        } else if (!result.loser().isBot()) {
            message = result.loser().toAtString();
        }
        return message;
    }

    public static GameView createGameOverView(Game.Result result, Stats.Result statsResult, Tile move, Game game, BufferedImage image) {
        var embed = new EmbedBuilder();

        var desc = getMoveMessage(result.winner(), move.toString()) +
            getScoreMessage(game.getWhiteScore(), game.getBlackScore()) + "\n" +
            getStatsMessage(result, statsResult);
        embed.setDescription(desc);

        return new GameView(embed)
            .setTitle("Game has ended")
            .setMessage(getTag(result))
            .setImage(image);
    }

    public static GameView createForfeitView(Game.Result result, Stats.Result statsResult, BufferedImage image) {
        var embed = new EmbedBuilder();

        var desc = getForfeitMessage(result.winner()) + "\n" +
            getStatsMessage(result, statsResult);
        embed.setDescription(desc);

        return new GameView(embed)
            .setTitle("Game has ended")
            .setMessage(getTag(result))
            .setImage(image);
    }

    public static GameView createResultSimulationView(Game game, Tile move, BufferedImage image) {
        var result = game.createResult();

        var embed = new EmbedBuilder();

        var desc = getMoveMessage(result.winner(), move.toString()) +
            getScoreMessage(game.getWhiteScore(), game.getBlackScore());
        embed.setDescription(desc);

        return new GameView(embed)
            .setTitle("Simulation has ended")
            .setMessage(getTag(result))
            .setImage(image);
    }
}
