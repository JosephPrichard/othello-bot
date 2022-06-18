package commands;

import commands.abstracts.CommandContext;
import commands.abstracts.CommandParam;
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

public class MoveCommandHandler extends CommandHandler
{
    private final Logger logger = Logger.getLogger("command.move");
    private final GamesDao gamesDao;
    private final ReversiBoardRenderer boardRenderer;

    public MoveCommandHandler(GamesDao gamesDao, ReversiBoardRenderer boardRenderer) {
        super(
            "Makes a move on user's current game",
            List.of(
                new CommandParam(
                    "move",
                    "The notation of move to make on the Reversi board (c3, D2, etc.)"
                )
            )
        );
        this.gamesDao = gamesDao;
        this.boardRenderer = boardRenderer;
    }

    @Override
    public void doCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.getEvent();

        String move = ctx.getParam("move");
        Player player = new Player(event.getAuthor());

        Game game = gamesDao.retrieveGame(player);
        if (game == null) {
            event.getChannel().sendMessage("You're not currently in a game!").queue();
            return;
        }

        // check if the player using command is same is player who's turn it is
        if (game.getCurrentPlayer().equals(player)) {
            // fetch potential moves for game
            ReversiBoard board = game.getBoard();
            List<Tile> potentialMoves = board.findPotentialMoves();
            // check if the move being requested is any of the potential moves, if so make the move
            for (Tile potentialMove : potentialMoves) {
                if (potentialMove.equalsNotation(move)) {
                    board.makeMove(potentialMove);

                    // send an embed with image of the board as a response
                    List<Tile> moves = board.findPotentialMoves();
                    BufferedImage image = boardRenderer.drawBoard(board, moves);
                    new GameMessageBuilder()
                        .setGame(game, move.toLowerCase())
                        .setTag(game)
                        .setImage(image)
                        .sendMessage(event.getChannel());

                    logger.info("Player " + player + " made move on game");

                    return;
                }
            }
        }

        event.getChannel().sendMessage("Can't make a move to" + move + ".").queue();
    }
}