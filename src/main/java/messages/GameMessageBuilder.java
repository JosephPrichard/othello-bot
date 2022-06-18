package messages;

import dto.Player;
import dto.Game;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GameMessageBuilder
{
    private static final int GREEN = new Color(82, 172, 85).getRGB();

    private final EmbedBuilder embedBuilder;
    private BufferedImage image;
    private String tag;

    public GameMessageBuilder() {
        embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(GREEN);
    }

    public GameMessageBuilder setGame(Game game) {
        if (game.getBoard().isGameOver()) {
            setGameOver(game);
            return this;
        }
        embedBuilder.setTitle(game.getBlackPlayer().getName() + " vs " + game.getWhitePlayer().getName());
        embedBuilder.addField(game.getScore(), game.getCurrentPlayer().getName() + " to move", false);
        return this;
    }

    public GameMessageBuilder setGame(Game game, String move) {
        if (game.getBoard().isGameOver()) {
            setGameOver(game);
            return this;
        }
        embedBuilder.setTitle("Your game with " + game.getOtherPlayer().getName());
        embedBuilder.addField(game.getScore(), "Your opponent has moved: " + move, false);
        return this;
    }

    public GameMessageBuilder setTag(Game game) {
        tag = "<@" + game.getCurrentPlayer() + ">";
        return this;
    }

    public GameMessageBuilder setGameOver(Game game) {
        embedBuilder.setTitle(game.getBlackPlayer().getName() + " vs " + game.getWhitePlayer().getName());
        Player winner = game.getWinner();
        String winnerText = winner != null ? winner.getName() + " won" : "Tie";
        embedBuilder.addField(game.getScore(), winnerText, false);
        return this;
    }

    public GameMessageBuilder setImage(BufferedImage image) {
        this.image = image;
        return this;
    }

    public void sendMessage(MessageChannel channel) {
        try {
            InputStream is = ImageUtils.toPngIS(image);
            embedBuilder.setImage("attachment://board.png");

            if (tag != null) {
                channel.sendMessage(tag)
                    .setEmbeds(embedBuilder.build())
                    .addFile(is, "board.png")
                    .queue();
            } else {
                channel.sendMessageEmbeds(embedBuilder.build())
                    .addFile(is, "board.png")
                    .queue();
            }
        } catch(IOException ex) {
            channel.sendMessage("Unexpected error: couldn't create board image").queue();
        }
    }
}
