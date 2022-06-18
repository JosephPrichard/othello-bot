package commands.abstracts;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandHandler
{
    private final String description;
    private final List<CommandParam> params;
    private final int minCount;

    public CommandHandler(String description) {
        this.description = description;
        this.params = new ArrayList<>();
        this.minCount = 0;
    }

    public CommandHandler(String description, List<CommandParam> params) {
        this.description = description;
        this.params = params;
        this.minCount = params.size();
    }

    public CommandHandler(String description, List<CommandParam> params, int minCount) {
        this.description = description;
        this.params = params;
        this.minCount = minCount;
    }

    public String getDescription() {
        return description;
    }

    public List<CommandParam> getParams() {
        return params;
    }

    abstract protected void doCommand(CommandContext ctx);

    private void sendErrorResponse(MessageChannel channel) {
        channel.sendMessage("Error processing command. Type !help command for details.").queue();
    }

    protected boolean isContextValid(CommandContext ctx) {
        int i = 0;
        for (CommandParam param : params) {
            if (ctx.getParam(param.getName()) == null && i < minCount) {
                return false;
            }
            i++;
        }
        return true;
    }

    public final void onMessageEvent(MessageReceivedEvent event) {
        CommandContext ctx = new CommandContext();

        String[] chunks = event.getMessage().getContentRaw().split("\\s+");

        // parse the chunks from the commands into the ctx variables
        ctx.setEvent(event);
        ctx.setKey(chunks[0]);

        // for each chunk, check the params list to get the name of the param and add it to params ctx
        for (int i = 1; i < chunks.length && i < params.size() + 1; i++) {
            CommandParam param = params.get(i - 1);
            if (param != null) {
                ctx.addParam(param.getName(), chunks[i]);
            } else {
                sendErrorResponse(ctx.getEvent().getChannel());
                return;
            }
        }

        if (isContextValid(ctx)) {
            doCommand(ctx);
        } else {
            sendErrorResponse(ctx.getEvent().getChannel());
        }
    }
}
