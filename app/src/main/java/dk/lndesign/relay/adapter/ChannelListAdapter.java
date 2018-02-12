package dk.lndesign.relay.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dk.lndesign.relay.ChatActivity;
import dk.lndesign.relay.R;
import dk.lndesign.relay.model.Stream;

/***************************************************************************************************
 * CONSTRUCTION ZONE: CONVERTING TO KOTLIN                                                         *
 **************************************************************************************************/
public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ViewHolder> {

    private List<Stream> mItems = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mStreamPreview;
        public View mStreamLiveStatus;
        public TextView mStreamViewers;
        public ImageView mChannelLogo;
        public TextView mChannelName;
        public TextView mGame;

        public ViewHolder(View itemView) {
            super(itemView);

            mStreamPreview = itemView.findViewById(R.id.stream_preview);
            mStreamLiveStatus = itemView.findViewById(R.id.stream_live_status);
            mStreamViewers = itemView.findViewById(R.id.stream_viewers);
            mChannelLogo = itemView.findViewById(R.id.channel_logo);
            mChannelName = itemView.findViewById(R.id.channel_name);
            mGame = itemView.findViewById(R.id.game);
        }
    }

    public ChannelListAdapter() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.recycler_item_channel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
        Stream stream = mItems.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = ChatActivity.newIntent(
                        context,
                        mItems.get(holder.getAdapterPosition()).getChannel().getName());
                context.startActivity(chatIntent);
            }
        });

        Glide.with(context)
                .load(stream.getPreview().getMedium())
                .fitCenter()
                .into(holder.mStreamPreview);
        if ("live".equals(stream.getStreamType())) {
            // Show live status and viewer count when stream is live.
            holder.mStreamLiveStatus.setVisibility(View.VISIBLE);
            holder.mStreamViewers.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(stream.getChannel().getLogo())
                    .fitCenter()
                    .into(holder.mChannelLogo);
            holder.mStreamViewers.setText(getFormattedViewerCount(stream.getViewers()));
        } else {
            // Hide live status and viewer count when stream is offline/hosting.
            holder.mStreamLiveStatus.setVisibility(View.GONE);
            holder.mStreamViewers.setVisibility(View.GONE);
        }
        holder.mChannelName.setText(stream.getChannel().getDisplayName());
        holder.mGame.setText(stream.getGame());
    }

    public void updateItems(@NonNull List<Stream> streams) {
        mItems.clear();
        mItems.addAll(streams);

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Format viewer count by shorting large numbers.
     * @param viewers Total viewer count
     * @return Formatted viewer count.
     */
    private String getFormattedViewerCount(int viewers) {
        if (viewers > 1000) {
            return String.format(Locale.getDefault(),
                    "%.1fk", (viewers * 0.001));
        } else {
            return String.valueOf(viewers);
        }
    }
}
