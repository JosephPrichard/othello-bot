import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Main
{
    public static void main(String[] args) throws LoginException {
        if (args.length < 1) {
            System.out.println("You have to provide a token as first argument!");
            System.exit(1);
        }

        System.out.println("Token: " + args[0]);

        JDABuilder.createLight(args[0], GatewayIntent.GUILD_MESSAGES)
            .addEventListeners(new ReversiBot())
            .setActivity(Activity.playing("Reversi"))
            .build();
    }
}
