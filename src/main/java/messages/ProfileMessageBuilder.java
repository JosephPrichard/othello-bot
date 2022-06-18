package messages;

import dto.Profile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class ProfileMessageBuilder
{
    private static final int GREEN = new Color(82, 172, 85).getRGB();

    private final EmbedBuilder embedBuilder;

    public ProfileMessageBuilder() {
        embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(GREEN);
    }

    public ProfileMessageBuilder setProfile(Profile profile) {
        embedBuilder.setTitle(profile.getPlayer().getName() + "'s stats")
            .addField("Rating", Float.toString(profile.getElo()), false)
            .addField("Lost", Integer.toString(profile.getLost()), true)
            .addField("Won", Integer.toString(profile.getWon()), true)
            .addField("Drawn", Integer.toString(profile.getDrawn()), true);
        return this;
    }

    public ProfileMessageBuilder setAuthor(User author) {
        embedBuilder.setThumbnail(author.getAvatarUrl());
        return this;
    }

    public void sendMessage(MessageChannel channel) {
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
