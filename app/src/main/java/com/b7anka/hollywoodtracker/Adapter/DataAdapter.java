package com.b7anka.hollywoodtracker.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.b7anka.hollywoodtracker.Helpers.Constants;
import com.b7anka.hollywoodtracker.Helpers.ImageManager;
import com.b7anka.hollywoodtracker.Interfaces.SetOnMovieClickListener;
import com.b7anka.hollywoodtracker.Model.Show;
import com.b7anka.hollywoodtracker.R;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ShowsDataViewHolder>
{
    private Context context;
    private List<Show> allShows;
    private SetOnMovieClickListener setOnMovieClickListener;

    public DataAdapter(Context context, List<Show> allShows, SetOnMovieClickListener setOnMovieClickListener)
    {
        this.context = context;
        this.allShows = allShows;
        this.setOnMovieClickListener = setOnMovieClickListener;
    }

    public void setAllShows(List<Show> allShows)
    {
        this.allShows = allShows;
    }

    @NonNull
    @Override
    public ShowsDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.data_item,parent,false);
        return new ShowsDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowsDataViewHolder holder, int position)
    {
        if(allShows.get(position).getTitle() != null)
        {
            holder.titleTextView.setText(allShows.get(position).getTitle());
        }
        else
        {
            holder.titleTextView.setText(Constants.EMPTY);
        }

        if(allShows.get(position).getWatchedTime() != null)
        {
            holder.watchedTimeTextView.setText(allShows.get(position).getWatchedTime());
        }
        else
        {
            holder.titleTextView.setText(Constants.EMPTY);
        }

        if(allShows.get(position).getType().equals(Constants.TV_SHOW))
        {
            holder.seasonTextView.setText(String.format("%s %s", context.getString(R.string.data_season), allShows.get(position).getSeason()));
            holder.episodeTextView.setText(String.format("%s %s", context.getString(R.string.data_episode), allShows.get(position).getEpisode()));
        }
        else
        {
            holder.seasonTextView.setText(Constants.EMPTY);
            holder.episodeTextView.setText(Constants.EMPTY);
        }

        if(allShows.get(position).isCompleted())
        {
            holder.completedTextView.setText(String.format("%s %s", context.getString(R.string.data_completed), context.getString(R.string.yes)));
        }
        else
        {
            holder.completedTextView.setText(String.format("%s %s", context.getString(R.string.data_completed), context.getString(R.string.no)));
        }

        Bitmap bitmap = ImageManager.loadImageFromStorage(allShows.get(position).getThumbnail());
        holder.thumbnailImageView.setImageBitmap(bitmap);
    }


    @Override
    public int getItemCount()
    {
        return allShows.size();
    }

    public class ShowsDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView titleTextView;
        TextView watchedTimeTextView;
        TextView seasonTextView;
        TextView episodeTextView;
        TextView completedTextView;
        ImageView thumbnailImageView;
        CardView dataCardView;

        public ShowsDataViewHolder(View view)
        {
            super(view);
            titleTextView = view.findViewById(R.id.tvTitle);
            watchedTimeTextView = view.findViewById(R.id.tvTimeWatchedOrPage);
            seasonTextView = view.findViewById(R.id.tvSeason);
            episodeTextView = view.findViewById(R.id.tvEpisode);
            completedTextView = view.findViewById(R.id.tvCompleted);
            thumbnailImageView = view.findViewById(R.id.ivThumbnail);
            dataCardView = view.findViewById(R.id.cvData);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            setOnMovieClickListener.setOnShowClick(this.getLayoutPosition());
        }
    }
}
