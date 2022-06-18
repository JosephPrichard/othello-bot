package commands;

import commands.abstracts.CommandContext;
import commands.abstracts.CommandHandler;
import commands.abstracts.CommandParam;
import messages.HelpListMessageBuilder;
import messages.HelpMessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class HelpCommandHandler extends CommandHandler
{
    private final Logger logger = Logger.getLogger("command.help");
    private final Map<String, CommandHandler> commandHandlers;

    public HelpCommandHandler(Map<String, CommandHandler> commandHandlers) {
        super(
            "Displays help for any command",
            List.of(
                new CommandParam(
                    "command",
                    "The command to display help for (optional)"
                )
            ),
            0
        );
        this.commandHandlers = commandHandlers;
    }

    @Override
    protected void doCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.getEvent();

        String command = ctx.getParam("command");

        // check if a specific command help is specified
        if (command == null) {
            // show help for all commands
            HelpListMessageBuilder messageBuilder = new HelpListMessageBuilder();
            for (Map.Entry<String, CommandHandler> handlerPair : commandHandlers.entrySet()) {
                messageBuilder.addCommand(handlerPair.getKey(), handlerPair.getValue().getParams());
            }
            messageBuilder.sendMessage(event.getChannel());
        } else {
            // shows help for a specific command
            CommandHandler handler = commandHandlers.get("!" + command);

            if (handler == null) {
                event.getChannel().sendMessage("No such command exists!").queue();
                return;
            }

            new HelpMessageBuilder()
                .setCommandKey(command)
                .setDescription(handler.getDescription())
                .setParams(handler.getParams())
                .sendMessage(event.getChannel());
        }

        logger.info("Used help command.");
    }
}
