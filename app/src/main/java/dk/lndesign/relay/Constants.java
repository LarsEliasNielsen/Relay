package dk.lndesign.relay;

/**
 * @author Lars Nielsen <larn@tv2.dk>
 */
public class Constants {

    public interface Action {
        String START_FOREGROUND_ACTION = "dk.lndesign.relay.action.start_foreground";
        String STOP_FOREGROUND_ACTION = "dk.lndesign.relay.action.stop_foreground";
        String FOREGROUND_MAIN_ACTION = "dk.lndesign.action.action.foreground_main";
    }

    public interface Notification {
        int FOREGROUND_SERVICE_ID = 420;
    }

    public interface Twitch {
        String TWITCH_CLIENT_ID = "siug9ave1zgzsdt58jdbswfnxwm1ht";
        String ACCESS_TOKEN = "<YOUR_TWITCH_ACCESS_TOKEN>";

        String NICK = "<YOUR_TWITCH_USERNAME>";
        String CHANNEL = "#brotatoe";

        interface Irc {
            String SERVER = "irc.chat.twitch.tv";
            int PORT = 6667;
        }
    }
}
