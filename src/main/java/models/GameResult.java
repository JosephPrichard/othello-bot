/*
 * Copyright (c) Joseph Prichard 2024.
 */

package models;

public record GameResult(Player winner, Player loser, boolean isDraw) {

    public static GameResult Draw(Player playerOne, Player playerTwo) {
        return new GameResult(playerOne, playerTwo, true);
    }

    public static GameResult WinLoss(Player winner, Player loser) {
        return new GameResult(winner, loser, false);
    }
}
