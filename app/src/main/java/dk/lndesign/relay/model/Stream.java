package dk.lndesign.relay.model;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class Stream {

    @SerializedName("_id")
    private String id;
    private String game;
    private Integer viewers;
    private Channel channel;
    @SerializedName("stream_type")
    private String streamType;

    public String getId() {
        return id;
    }

    public String getGame() {
        return game;
    }

    public Integer getViewers() {
        return viewers;
    }

    public Channel getChannel() {
        return channel;
    }

    public static class Channel {

        @SerializedName("_id")
        private String id;
        @SerializedName("display_name")
        private String displayName;
        private String name;
        private String logo;
        @SerializedName("profile_banner")
        private String profileBanner;

        public String getDisplayName() {
            return displayName;
        }

        public String getName() {
            return name;
        }

        public String getLogo() {
            return logo;
        }

        public String toString() {
            return String.format(Locale.getDefault(),
                    "Channel { displayName: %s, name: %s }",
                    getDisplayName(), getName());
        }
    }

    public String toString() {
        return String.format(Locale.getDefault(),
                "Stream { id: %s, game: '%s', viewers: %d, channel: %s }",
                getId(), getGame(), getViewers(), getChannel());
    }
}
