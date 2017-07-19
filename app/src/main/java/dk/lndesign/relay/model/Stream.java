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
    private Preview preview;
    private Channel channel;
    @SerializedName("stream_type")
    private String streamType;

    public Stream(Parcel in) {
        id = in.readString();
        game = in.readString();
        viewers = in.readInt();
        preview = in.readParcelable(Preview.class.getClassLoader());
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

    public Preview getPreview() {
        return preview;
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
        dest.writeParcelable(getPreview(), flags);
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

    public static class Preview implements Parcelable {
        private String small;
        private String medium;
        private String large;

        public Preview(Parcel in) {
            small = in.readString();
            medium = in.readString();
            large = in.readString();
        }

        public String getSmall() {
            return small;
        }

        public String getMedium() {
            return medium;
        }

        public String getLarge() {
            return large;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(getSmall());
            dest.writeString(getMedium());
            dest.writeString(getLarge());
        }

        public static final Creator<Preview> CREATOR = new Creator<Preview>() {
            public Preview createFromParcel(Parcel in) {
                return new Preview(in);
            }

            public Preview[] newArray(int size) {
                return new Preview[size];
            }
        };

        public String toString() {
            return String.format(Locale.getDefault(),
                    "Preview { small: %s, medium: %s, large: %s }",
                    getSmall(), getMedium(), getLarge());
        }
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

    public static class Wrapper implements Parcelable {
        public Stream stream;

        public Wrapper(Parcel in) {
            stream = in.readParcelable(Wrapper.class.getClassLoader());
        }

        public Stream getStream() {
            return stream;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(getStream(), flags);
        }

        public static final Creator<Wrapper> CREATOR = new Creator<Wrapper>() {
            public Wrapper createFromParcel(Parcel in) {
                return new Wrapper(in);
            }

            public Wrapper[] newArray(int size) {
                return new Wrapper[size];
            }
        };
    }

}
