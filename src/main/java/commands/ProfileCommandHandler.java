package commands;

import commands.abstracts.CommandContext;
import commands.abstracts.CommandHandler;
import dao.ProfilesDao;
import dto.Player;
import dto.Profile;
import messages.ProfileMessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.logging.Logger;

public class ProfileCommandHandler extends CommandHandler
{
    private final Logger logger = Logger.getLogger("command.profile");
    private final ProfilesDao profilesDao;

    public ProfileCommandHandler(ProfilesDao profilesDao) {
        super("Retrieves the stats profile for a player");
        this.profilesDao = profilesDao;
    }

    @Override
    protected void doCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.getEvent();

        Player player = new Player(event.getAuthor());

        Profile profile = profilesDao.retrieveProfile(player);

        new ProfileMessageBuilder()
            .setProfile(profile)
            .setAuthor(event.getAuthor())
            .sendMessage(event.getChannel());

        logger.info("Retrieved profile for " + profile.getPlayer());
    }
}
