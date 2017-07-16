package dk.lndesign.relay.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * @author Lars Nielsen <lars@lndesign.dk>
 */
public class Stream implements Parcelable {

    @SerializedName("_id")
    private String id;
    private String game;
    private Integer viewers;
    private Channel channel;
    @SerializedName("stream_type")
    private String streamType;

    private Stream(Parcel in) {
        id = in.readString();
        game = in.readString();
        viewers = in.readInt();
        channel = in.readParcelable(Channel.class.getClassLoader());
        streamType = in.readString();
    }

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

    public String getStreamType() {
        return streamType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getGame());
        dest.writeInt(getViewers());
        dest.writeParcelable(getChannel(), flags);
        dest.writeString(getStreamType());
    }

    public static final Creator<Stream> CREATOR = new Creator<Stream>() {
        public Stream createFromParcel(Parcel in) {
            return new Stream(in);
        }

        public Stream[] newArray(int size) {
            return new Stream[size];
        }
    };

    public String toString() {
        return String.format(Locale.getDefault(),
                "Stream { id: %s, game: '%s', viewers: %d, channel: %s }",
                getId(), getGame(), getViewers(), getChannel());
    }

    public static class Channel implements Parcelable {

        @SerializedName("_id")
        private String id;
        @SerializedName("display_name")
        private String displayName;
        private String name;
        private String logo;
        @SerializedName("profile_banner")
        private String profileBanner;

        public Channel(Parcel in) {
            id = in.readString();
            displayName = in.readString();
            name = in.readString();
            logo = in.readString();
            profileBanner = in.readString();
        }

        public String getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getName() {
            return name;
        }

        public String getLogo() {
            return logo;
        }

        public String getProfileBanner() {
            return profileBanner;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(getId());
            dest.writeString(getDisplayName());
            dest.writeString(getName());
            dest.writeString(getLogo());
            dest.writeString(getProfileBanner());
        }

        public static final Creator<Channel> CREATOR = new Creator<Channel>() {
            public Channel createFromParcel(Parcel in) {
                return new Channel(in);
            }

            public Channel[] newArray(int size) {
                return new Channel[size];
            }
        };

        public String toString() {
            return String.format(Locale.getDefault(),
                    "Channel { displayName: %s, name: %s }",
                    getDisplayName(), getName());
        }
    }

}
