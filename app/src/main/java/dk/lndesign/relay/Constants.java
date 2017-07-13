package dk.lndesign.relay;

/**
 * @author Lars Nielsen <lars@lndesign.dk>
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
        // https://api.twitch.tv/kraken/oauth2/authorize?client_id=siug9ave1zgzsdt58jdbswfnxwm1ht&redirect_uri=http://localhost&response_type=token&scope=chat_login
//        String accessToken = "0usgugnn73hfsvy4f9shmfpz9e99vd";
        // https://api.twitch.tv/kraken/oauth2/authorize?client_id=siug9ave1zgzsdt58jdbswfnxwm1ht&redirect_uri=http://localhost&response_type=token&scope=user_read+chat_login
        String ACCESS_TOKEN = "<YOUR_TWITCH_ACCESS_TOKEN>";

        String NICK = "<YOUR_TWITCH_USERNAME>";
        String CHANNEL = "#brotatoe";

        interface Irc {
            String SERVER = "irc.chat.twitch.tv";
            int PORT = 6667;
        }
    }
}
