package dk.lndesign.relay.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class FollowedChannels {

    @SerializedName("_total")
    private Integer total;
    private List<Stream> streams;

    public Integer getTotal() {
        return total;
    }

    public List<Stream> getStreams() {
        return streams;
    }
}
