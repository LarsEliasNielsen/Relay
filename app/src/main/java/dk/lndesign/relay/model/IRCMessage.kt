package dk.lndesign.relay.model

import timber.log.Timber
import java.util.regex.Pattern

class IRCMessage(val raw: String) {

    // Pattern for grouping raw IRC command into meaningful groups.
    private val ircCommandPattern = "^(?:[:](([A-Za-z0-9_]{4,25})!([A-Za-z0-9_]{4,25})@([a-zA-Z0-9_]{4,25}.tmi.twitch.tv)) )?(\\S+)(?: (?!:)(.+?))?(?: [:](.+))?$"
    // private val ircCommandPattern = "^(?:[:](([A-Za-z0-9_]{4,25})!([A-Za-z0-9_]{4,25})@([a-zA-Z0-9_]{4,25}.[\\S]+)) )?(JOIN|MODE|NAMES|PART|PRIVMSG)(?: (?!:)(.+?))?(?: [:](.+))?$"

    // IRC command groups.
    var prefix: String? = null
    var nick: String? = null
    var username: String? = null
    var hostname: String? = null
    var command: String? = null
    var channel: String? = null
    var message: String? = null

    init {
        val pattern = Pattern.compile(ircCommandPattern)
        val matcher = pattern.matcher(raw)

        if (matcher.matches()) {
            prefix = matcher.group(1)
            nick = matcher.group(2)
            username = matcher.group(3)
            hostname = matcher.group(4)
            command = matcher.group(5)
            channel = matcher.group(6)
            message = matcher.group(7)
        } else {
            Timber.e("Raw IRC command did not match the expected pattern")
        }
    }

    override fun toString(): String {
        return "IRCMessage { nick: $nick, command: $command, message: '$message' }"
    }
}
