package messages;

import dto.Player;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ChallengeMessageBuilder
{
    private final StringBuilder messageBuilder;
    private Player challenged;
    private Player challenger;

    public ChallengeMessageBuilder() {
        messageBuilder = new StringBuilder();
    }

    public ChallengeMessageBuilder setChallenged(Player challenged) {
        this.challenged = challenged;
        return this;
    }

    public ChallengeMessageBuilder setChallenger(Player challenger) {
        this.challenger = challenger;
        return this;
    }

    public void sendMessage(MessageChannel channel) {
        messageBuilder.append("<@").append(challenged.getId()).append(">, ")
            .append(challenger.getName()).append(" has challenged you to a game of reversi. ")
            .append("Type !accept ")
            .append("<@").append(challenger.getId()).append(">, ")
            .append("or ignore to decline.");

        channel.sendMessage(messageBuilder.toString()).queue();
    }
}
