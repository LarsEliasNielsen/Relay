package dk.lndesign.relay.irc;

/**
 * List of IRC return codes.
 * {@see http://www.networksorcery.com/enp/protocol/irc.htm}
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class IRCReturnCode {

    public static final String RPL_MYINFO = "004";
    public static final String RPL_NAMREPLY = "353";
    public static final String RPL_ENDOFNAMES = "366";
    public static final String RPL_MOTDSTART = "375";
    public static final String RPL_ENDOFMOTD = "376";
    public static final String RPL_MOTD = "372";

    public static final String ERR_NOSUCHNICK = "401";
    public static final String ERR_NOSUCHSERVER = "402";
    public static final String ERR_NOSUCHCHANNEL = "403";
    public static final String ERR_NICKNAMEINUSE = "433";
}
