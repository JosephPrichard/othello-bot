import commands.*;
import commands.abstracts.CommandHandler;
import dao.GamesDao;
import dao.ProfilesDao;
import renderers.ReversiBoardRenderer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReversiBot extends ListenerAdapter
{
    private final Map<String, CommandHandler> handlerMap = new ConcurrentHashMap<>();

    public ReversiBot() {
        GamesDao gamesDao = new GamesDao();
        ProfilesDao profilesDao = new ProfilesDao();
        ReversiBoardRenderer boardRenderer = new ReversiBoardRenderer(4, 6, 8);

        addCommand("!challenge", new ChallengeCommandHandler(gamesDao));
        addCommand("!move", new MoveCommandHandler(gamesDao, boardRenderer));
        addCommand("!view", new ViewCommandHandler(gamesDao, boardRenderer));
        addCommand("!profile", new ProfileCommandHandler(profilesDao));
        addCommand("!help", new HelpCommandHandler(handlerMap));
    }

    public void addCommand(String key, CommandHandler handler) {
        handlerMap.put(key, handler);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        // extracts the command key from the string
        int i = message.indexOf(' ');
        String key = i != -1 ? message.substring(0, i) : message;

        // fetch command handler from commands map, execute if command exists
        CommandHandler handler = handlerMap.get(key);
        if (handler != null) {
            handler.onMessageEvent(event);
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
    }
}
