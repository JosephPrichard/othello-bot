package commands;

import commands.abstracts.CommandContext;
import commands.abstracts.CommandHandler;
import dao.GamesDao;
import dto.Player;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.logging.Logger;

public class ForfeitCommandHandler extends CommandHandler
{
    private final Logger logger = Logger.getLogger("command.forfeit");
    private final GamesDao gamesDao;

    public ForfeitCommandHandler(GamesDao gamesDao) {
        super("Forfeits the user's current game");
        this.gamesDao = gamesDao;
    }

    @Override
    public void doCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.getEvent();

        Player player = new Player(event.getAuthor());

//        gamesDao.removeGame(player);

        event.getChannel().sendMessage(player + " has forfeited.").queue();
    }
}
