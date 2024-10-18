/*
 * Copyright (c) Joseph Prichard 2024.
 */

package command;

import domain.OthelloBoard;
import lombok.AllArgsConstructor;
import models.Player;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import services.BotState;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class AutoCompleteHandler {
    public static void handleMove(BotState state, CommandAutoCompleteInteraction interaction) {
        var gameService = state.getGameService();

        var player = new Player(interaction.getUser());

        var game = gameService.getGame(player);
        if (game != null) {
            var moves = game.findPotentialMoves();

            // don't display duplicate moves
            var duplicate = new boolean[OthelloBoard.getBoardSize()][OthelloBoard.getBoardSize()];

            List<Command.Choice> choices = new ArrayList<>();
            for (var tile : moves) {
                var row = tile.row();
                var col = tile.col();

                if (!duplicate[row][col]) {
                    choices.add(new Command.Choice(tile.toString(), tile.toString()));
                }
                duplicate[row][col] = true;
            }

            interaction.replyChoices(choices).queue();
        } else {
            interaction.replyChoices().queue();
        }
    }
}
