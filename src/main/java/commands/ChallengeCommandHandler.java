package commands;

import commands.abstracts.CommandContext;
import commands.abstracts.CommandParam;
import commands.abstracts.CommandHandler;
import dao.GamesDao;
import dto.Player;
import messages.ChallengeMessageBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.DiscordUtils;

import java.util.List;
import java.util.logging.Logger;

public class ChallengeCommandHandler extends CommandHandler
{
    private final Logger logger = Logger.getLogger("command.challenge");
    private final GamesDao gamesDao;

    public ChallengeCommandHandler(GamesDao gamesDao) {
        super(
            "Challenges an another discord player to a Reversi game",
            List.of(
                new CommandParam(
                    "opponent",
                    "The @tag of the opposing player you want to challenge to a game"
                )
            )
        );
        this.gamesDao = gamesDao;
    }

    @Override
    public void doCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.getEvent();

        User opponentUser = DiscordUtils.getUser(event.getJDA(), ctx.getParam("opponent"));
        if (opponentUser == null) {
            event.getChannel().sendMessage("Can't find a discord user with that id. Try using @ directly.").queue();
            return;
        }

        Player opponent = new Player(opponentUser);
        Player player = new Player(event.getAuthor());

        if (gamesDao.isPlaying(player)) {
            event.getChannel().sendMessage("You already have a game in progress.").queue();
        } else if (gamesDao.isPlaying(opponent)) {
            event.getChannel().sendMessage("Your opponent has a game in progress.").queue();
        } else {
            gamesDao.createGame(player, opponent);

            new ChallengeMessageBuilder()
                .setChallenged(opponent)
                .setChallenger(player)
                .sendMessage(event.getChannel());
        }

        logger.info("Player " + player + " challenged opponent " + opponent);
    }
}
