package zafirmcbryde.com.desktopia.Controller.Retro;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


import zafirmcbryde.com.desktopia.Model.SubredditModel;
import zafirmcbryde.com.desktopia.R;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder>
{
    private ArrayList<SubredditModel> redditPosts;
    private ThumbnailDownloader<RecyclerViewHolder> mThumbnailDownloader;

    public RecyclerViewAdapter(ArrayList<SubredditModel> post)
    {
        this.redditPosts = post;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.gallery_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position)
    {
        SubredditModel model = redditPosts.get(position);
        mThumbnailDownloader.queueThumbnail(holder, model.getUrl());
    }


    @Override
    public int getItemCount()
    {
        return redditPosts.size();
    }
}

class RecyclerViewHolder extends RecyclerView.ViewHolder
{
    public TextView titleText;
    public ImageView imageView;

    public RecyclerViewHolder(View itemView)
    {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
    }
}