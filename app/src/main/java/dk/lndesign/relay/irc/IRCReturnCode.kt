package dk.lndesign.relay.irc

/**
 * List of IRC return codes.
 * {@see http://www.networksorcery.com/enp/protocol/irc.htm}
 */
object IRCReturnCode {

    val RPL_MYINFO = "004"
    val RPL_NAMREPLY = "353"
    val RPL_ENDOFNAMES = "366"
    val RPL_MOTDSTART = "375"
    val RPL_ENDOFMOTD = "376"
    val RPL_MOTD = "372"

    val ERR_NOSUCHNICK = "401"
    val ERR_NOSUCHSERVER = "402"
    val ERR_NOSUCHCHANNEL = "403"
    val ERR_NICKNAMEINUSE = "433"
}
