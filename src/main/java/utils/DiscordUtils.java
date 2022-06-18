package utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;

public class DiscordUtils
{
    public static long toLongId(String id) {
        String strippedId = id.replaceAll("[\\D]", "");
        return Long.parseLong(strippedId);
    }

    @Nullable
    public static User getUser(JDA jda, String id) {
        try {
            long longId = toLongId(id);
            return jda.retrieveUserById(longId).complete();
        } catch(NumberFormatException ex) {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(Long.parseLong("168526026425499648"));
    }
}
