package dk.lndesign.relay.model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import java.util.Locale

class Stream(@SerializedName("_id") val id: String,
             val game: String,
             val viewers: Int?,
             val preview: Preview,
             val channel: Channel,
             @SerializedName("stream_type") val streamType: String): Parcelable {

    private constructor(parcel: Parcel): this(
        id = parcel.readString(),
        game = parcel.readString(),
        viewers = parcel.readInt(),
        preview = parcel.readParcelable(Preview::class.java.classLoader),
        channel = parcel.readParcelable(Channel::class.java.classLoader),
        streamType = parcel.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(game)
        dest.writeInt(viewers!!)
        dest.writeParcelable(preview, flags)
        dest.writeParcelable(channel, flags)
        dest.writeString(streamType)
    }

    override fun toString(): String {
        return "Stream { id: $id, game: '$game', viewers: $viewers, channel: $channel }"
    }

    class Preview(val small: String,
                  val medium: String,
                  val large: String) : Parcelable {

        private constructor(parcel: Parcel): this(
                small = parcel.readString(),
                medium = parcel.readString(),
                large = parcel.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(small)
            dest.writeString(medium)
            dest.writeString(large)
        }

        override fun toString(): String {
            return "Preview { small: $small, medium: $medium, large: $large }"
        }

        companion object {
            @JvmField val CREATOR = object : Parcelable.Creator<Preview> {
                override fun createFromParcel(parcel: Parcel): Preview {
                    return Preview(parcel)
                }

                override fun newArray(size: Int): Array<Preview?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    class Channel(@SerializedName("_id")
                  val id: String,
                  @SerializedName("display_name") val displayName: String,
                  val name: String,
                  val logo: String,
                  @SerializedName("profile_banner") val profileBanner: String) : Parcelable {

        private constructor(parcel: Parcel): this(
                id = parcel.readString(),
                displayName = parcel.readString(),
                name = parcel.readString(),
                logo = parcel.readString(),
                profileBanner = parcel.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(id)
            dest.writeString(displayName)
            dest.writeString(name)
            dest.writeString(logo)
            dest.writeString(profileBanner)
        }

        override fun toString(): String {
            return String.format(Locale.getDefault(),
                    "Channel { displayName: %s, name: %s }",
                    displayName, name)
        }

        companion object {
            @JvmField val CREATOR = object : Parcelable.Creator<Channel> {
                override fun createFromParcel(parcel: Parcel): Channel {
                    return Channel(parcel)
                }

                override fun newArray(size: Int): Array<Channel?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    class Wrapper(val stream: Stream) : Parcelable {

        private constructor(parcel: Parcel): this(
            stream = parcel.readParcelable(Wrapper::class.java.classLoader)
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeParcelable(stream, flags)
        }

        companion object {
            @JvmField val CREATOR = object : Parcelable.Creator<Wrapper> {
                override fun createFromParcel(parcel: Parcel): Wrapper {
                    return Wrapper(parcel)
                }

                override fun newArray(size: Int): Array<Wrapper?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        @JvmField val CREATOR = object : Parcelable.Creator<Stream> {
            override fun createFromParcel(parcel: Parcel): Stream {
                return Stream(parcel)
            }

            override fun newArray(size: Int): Array<Stream?> {
                return arrayOfNulls(size)
            }
        }
    }

}
