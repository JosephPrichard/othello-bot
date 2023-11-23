/*
 * Copyright (c) Joseph Prichard 2023.
 */

package discord;

import discord.commands.*;
import discord.commands.Command;
import services.DataSource;
import services.stats.StatsDao;
import services.challenge.ChallengeService;
import services.game.GameService;
import services.agent.AgentService;
import services.stats.StatsService;
import discord.renderers.OthelloBoardRenderer;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OthelloBot extends ListenerAdapter
{
    private final Map<String, Command> commandMap = new ConcurrentHashMap<>();
    private final List<Command> commandList = new ArrayList<>();

    public OthelloBot() {
        DataSource ds = new DataSource();

        StatsDao statsDao = new StatsDao(ds);

        AgentService agentService = new AgentService();
        StatsService statsService = new StatsService(statsDao);
        GameService gameService = new GameService(statsService);
        ChallengeService challengeService = new ChallengeService();

        OthelloBoardRenderer boardRenderer = new OthelloBoardRenderer();

        // add all bot commands to the handler map for handling events
        addCommand(new ChallengeCommand(challengeService));
        addCommand(new ChallengeBotCommand(gameService, boardRenderer));
        addCommand(new AcceptCommand(gameService, challengeService, boardRenderer));
        addCommand(new ForfeitCommand(gameService, statsService, boardRenderer));
        addCommand(new MoveCommand(gameService, statsService, agentService, boardRenderer));
        addCommand(new ViewCommand(gameService, boardRenderer));
        addCommand(new AnalyzeCommand(gameService, agentService));
        addCommand(new StatsCommand(statsService));
        addCommand(new LeaderBoardCommand(statsService));
    }

    public void addCommand(Command command) {
        commandMap.put("!" + command.getKey(), command);
        commandList.add(command);
    }

    public void onHelpForCommand(MessageChannel channel, String key) {
        key = "!" + key;
        Command command = commandMap.get(key);
        if (command != null) {
            StringBuilder text = new StringBuilder(command.getDesc() + "\n" + key);
            for (String param : command.getParams()) {
                text.append(" `").append(param).append("`");
            }
            channel.sendMessage(text.toString()).queue();
        } else {
            channel.sendMessage("No such command: " + key).queue();
        }
    }

    public void onHelp(MessageChannel channel) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Commands: ");
        for (Command command : commandList) {
            stringBuilder.append("`!")
                .append(command.getKey())
                .append("` ");
        }
        channel.sendMessage(stringBuilder.toString()).queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String message = event.getMessage().getContentRaw();
        // extracts the command key from the string
        String[] tokens = message.split("\\s+");
        String key = tokens[0];

        // special case for help command
        if (key.equals("!help")) {
            if (tokens.length >= 2) {
                onHelpForCommand(event.getChannel(), tokens[1]);
            } else {
                onHelp(event.getChannel());
            }

            return;
        }

        // fetch command handler from bot.commands map, execute if command exists
        Command command = commandMap.get(key);
        if (command != null) {
            command.onMessageEvent(event);
        }
    }
}
