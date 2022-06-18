package dao;

import dto.Player;
import dto.Game;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GamesDao
{
    private final Map<Player, Game> games = new ConcurrentHashMap<>();

    public void createGame(Player blackPlayer, Player whitePlayer) {
        Game game = new Game();
        game.setBlackPlayer(blackPlayer);
        game.setWhitePlayer(whitePlayer);

        games.put(blackPlayer, game);
        games.put(whitePlayer, game);
    }

    @Nullable
    public Game retrieveGame(Player player) {
        return games.get(player);
    }

    public boolean isPlaying(Player player) {
        Game game = retrieveGame(player);
        return game != null && !game.getBoard().isGameOver();
    }
}
