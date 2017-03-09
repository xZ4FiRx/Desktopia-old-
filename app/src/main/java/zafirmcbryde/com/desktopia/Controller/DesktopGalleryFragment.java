package zafirmcbryde.com.desktopia.Controller;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import zafirmcbryde.com.desktopia.Model.DesktopItems;
import zafirmcbryde.com.desktopia.R;

public class DesktopGalleryFragment extends Fragment
{
    private RecyclerView mDesktopRecyclerView;
    private static final String TAG = "DesktopGalleryFragment";
    private List<DesktopItems> mList = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public static DesktopGalleryFragment newInstance()
    {
        return new DesktopGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_desktop_gallery, container, false);
        mDesktopRecyclerView = (RecyclerView) v
                .findViewById(R.id.fragment_desktop_gallery_recycler_view);
        mDesktopRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return v;
    }

    private void setupAdapter()
    {
        if (isAdded())
        {
            mDesktopRecyclerView.setAdapter(new PhotoAdapter(mList));
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>
    {
        private List<DesktopItems> mGalleryItems;

        public PhotoAdapter(List<DesktopItems> galleryItems)
        {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position)
        {
            DesktopItems desktopItems = mGalleryItems.get(position);
            mThumbnailDownloader.queueThumbnail(photoHolder, desktopItems.getUrl());
        }

        @Override
        public int getItemCount()
        {
            return mGalleryItems.size();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder
    {
        private ImageView mImageView;

        public PhotoHolder(View itemView)
        {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.fragment_desktop_gallery_image);
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<DesktopItems>>
    {
        @Override
        protected List<DesktopItems> doInBackground(Void... params)
        {

            return new RedditParser().fetchItems();
        }

        @Override
        protected void onPostExecute(List<DesktopItems> items)
        {
            mList = items;
            setupAdapter();
        }
    }

}

