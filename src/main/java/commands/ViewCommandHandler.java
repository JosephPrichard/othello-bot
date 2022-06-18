package commands;

import commands.abstracts.CommandContext;
import commands.abstracts.CommandHandler;
import dto.Game;
import dao.GamesDao;
import dto.Player;
import messages.GameMessageBuilder;
import renderers.ReversiBoardRenderer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import reversi.board.ReversiBoard;
import reversi.board.Tile;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.logging.Logger;

public class ViewCommandHandler extends CommandHandler
{
    private final Logger logger = Logger.getLogger("command.view");
    private final GamesDao gamesDao;
    private final ReversiBoardRenderer boardRenderer;

    public ViewCommandHandler(GamesDao gamesDao, ReversiBoardRenderer boardRenderer) {
        super("Displays the game state including all the moves that can be made this turn");
        this.gamesDao = gamesDao;
        this.boardRenderer = boardRenderer;
    }

    @Override
    public void doCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.getEvent();

        Player player = new Player(event.getAuthor());

        Game game = gamesDao.retrieveGame(player);
        if (game == null) {
            event.getChannel().sendMessage("You're not currently in a game!").queue();
            return;
        }

        ReversiBoard board = game.getBoard();
        List<Tile> potentialMoves = board.findPotentialMoves();

        BufferedImage image = boardRenderer.drawBoard(board, potentialMoves);
        new GameMessageBuilder()
            .setGame(game)
            .setImage(image)
            .sendMessage(event.getChannel());

        logger.info("Player " + player + " view moves in game");
    }
}
