package dk.lndesign.relay;

/**
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class Constants {

    public interface Key {
        String SELECTED_STREAM = "dk.lndesign.relay.key.selected_stream";
    }

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
        // https://api.twitch.tv/kraken/oauth2/authorize?client_id=siug9ave1zgzsdt58jdbswfnxwm1ht&redirect_uri=http://localhost&response_type=token&scope=user_read+chat_login
        String ACCESS_TOKEN = "q7bif1v0ro2ovk0lhr3dxg1l4vlgtq";

        String NICK = "larselias";
        String CHANNEL = "#cohhcarnage";

        interface Irc {
            String SERVER = "irc.chat.twitch.tv";
            int PORT = 6667;
        }
    }
}
