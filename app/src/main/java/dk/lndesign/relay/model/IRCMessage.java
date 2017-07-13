package dk.lndesign.relay.model;

import android.util.Log;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class IRCMessage {

    private static final String LOG_TAG = IRCMessage.class.getSimpleName();

    // Pattern for grouping raw IRC command into meaningful groups.
    private String ircCommandPattern = "^(?:[:](([A-Za-z0-9_]{4,25})!([A-Za-z0-9_]{4,25})@([a-zA-Z0-9_]{4,25}.tmi.twitch.tv)) )?(\\S+)(?: (?!:)(.+?))?(?: [:](.+))?$";
//    private String ircCommandPattern = "^(?:[:](([A-Za-z0-9_]{4,25})!([A-Za-z0-9_]{4,25})@([a-zA-Z0-9_]{4,25}.[\\S]+)) )?(JOIN|MODE|NAMES|PART|PRIVMSG)(?: (?!:)(.+?))?(?: [:](.+))?$";

    // IRC command groups.
    private String raw;
    private String prefix;
    private String nick;
    private String username;
    private String hostname;
    private String command;
    private String channel;
    private String message;

    public IRCMessage(String ircCommand) {
        raw = ircCommand;

        Pattern pattern = Pattern.compile(ircCommandPattern);
        Matcher matcher = pattern.matcher(ircCommand);

        if (matcher.matches()) {
            prefix = matcher.group(1);
            nick = matcher.group(2);
            username = matcher.group(3);
            hostname = matcher.group(4);
            command = matcher.group(5);
            channel = matcher.group(6);
            message = matcher.group(7);
        } else {
            Log.e(LOG_TAG, "Raw IRC command did not match the exptected pattern");
        }
    }

    public String getRaw() {
        return raw;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNick() {
        return nick;
    }

    public String getUsername() {
        return username;
    }

    public String getHostname() {
        return hostname;
    }

    public String getCommand() {
        return command;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return String.format(Locale.getDefault(),
                "IRCMessage { nick: %s, command: %s, message: '%s' }",
                getNick(), getCommand(), getMessage());
    }
}
