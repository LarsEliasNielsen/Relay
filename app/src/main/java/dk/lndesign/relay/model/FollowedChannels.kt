package dk.lndesign.relay.model

import com.google.gson.annotations.SerializedName

class FollowedChannels {
    @SerializedName("_total")
    val total: Int? = null
    val streams: List<Stream>? = null
}
