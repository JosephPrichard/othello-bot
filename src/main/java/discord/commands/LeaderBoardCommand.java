package discord.commands;

import discord.commands.abstracts.CommandContext;
import discord.commands.abstracts.Command;
import modules.stats.Stats;
import discord.message.builder.LeaderboardEmbedBuilder;
import modules.stats.StatsService;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.logging.Logger;

public class LeaderBoardCommand extends Command
{
    private final Logger logger = Logger.getLogger("command.leaderboard");
    private final StatsService statsService;

    public LeaderBoardCommand(StatsService statsService) {
        super("leaderboard", "Retrieves the highest rated players by ELO");
        this.statsService = statsService;
    }

    @Override
    protected void doCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.getEvent();
        MessageChannel channel = event.getChannel();

        List<Stats> statsList = statsService.getTopStats();

        MessageEmbed embed = new LeaderboardEmbedBuilder()
            .setStats(statsList)
            .build();

        channel.sendMessageEmbeds(embed).queue();

        logger.info("Fetched leaderboard");
    }
}