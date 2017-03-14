package zafirmcbryde.com.desktopia.Controller.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import zafirmcbryde.com.desktopia.Controller.Util.EndlessRecyclerViewScrollListener;
import zafirmcbryde.com.desktopia.Controller.Util.RedditParser;
import zafirmcbryde.com.desktopia.Model.DesktopItems;
import zafirmcbryde.com.desktopia.R;

public class DesktopGalleryFragment extends Fragment
{
    private RecyclerView mDesktopRecyclerView;
    private List<DesktopItems> mList = new ArrayList<>();
    private DesktopItems mItems = new DesktopItems();
    private ProgressDialog mProgressDialog;
    private boolean loading = true;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int lastFetchedPage = 1;
    private int lastBoundPosition;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_desktop_gallery, container, false);
        mDesktopRecyclerView = (RecyclerView) v
                .findViewById(R.id.fragment_desktop_gallery_recycler_view);

        final GridLayoutManager mLayoutManager;
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mDesktopRecyclerView.setLayoutManager(mLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager)
        {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view)
            {
                if (page > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {

                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            PhotoAdapter adapter = (PhotoAdapter) view.getAdapter();
                            int lastPosition = adapter.getLastBoundPosition();
                            GridLayoutManager layoutManager = (GridLayoutManager) view.getLayoutManager();
                            int loadBufferPosition = 1;
                            if (lastPosition >= adapter.getItemCount() - layoutManager.getSpanCount() - loadBufferPosition)
                            {
                                new FetchItemsTask().execute(lastPosition + 1);
                            }
                        }
                    }
                }
            }
        };



        mDesktopRecyclerView.addOnItemTouchListener(new DesktopGalleryFragment.
                RecyclerTouchListener(

            getContext(),mDesktopRecyclerView, new

            ClickListener()
            {
                @Override
                public void onClick (View view,int position)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", (Serializable) mList);
                    bundle.putInt("position", position);

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ImageDialogFragment newFragment = ImageDialogFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "slideshow");
                }

                @Override
                public void onLongClick (View view,int position)
                {
                    //Later
                }
            }));
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
        private int lastBoundPosition;

        public int getLastBoundPosition()
        {
            return lastBoundPosition;
        }

        PhotoAdapter(List<DesktopItems> galleryItems)
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
            photoHolder.bindGalleryItem(desktopItems);
            lastBoundPosition = position;
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
        private DesktopItems mDesktopItems;

        PhotoHolder(View itemView)
        {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.fragment_desktop_gallery_image);
        }

        void bindGalleryItem(DesktopItems desktopItems)
        {
            mDesktopItems = desktopItems;
            Glide.with(getActivity())
                    .load(desktopItems.getUrl())
                    .into(mImageView);
        }
    }

    interface ClickListener
    {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener
    {

        private GestureDetector gestureDetector;
        private DesktopGalleryFragment.ClickListener clickListener;

        RecyclerTouchListener(Context context, final RecyclerView recyclerView,
                              final DesktopGalleryFragment.ClickListener clickListener)
        {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new
                    GestureDetector.SimpleOnGestureListener()
                    {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e)
                        {
                            return true;
                        }

                        @Override
                        public void onLongPress(MotionEvent e)
                        {
                            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                            if (child != null && clickListener != null)
                            {
                                clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                            }
                        }
                    });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
        {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e))
            {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e)
        {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
        {

        }
    }

    private class FetchItemsTask extends AsyncTask<Integer, Void, List<DesktopItems>>
    {
        @Override
        protected List<DesktopItems> doInBackground(Integer... params)
        {

            return new RedditParser().fetchItems(params[0]);
        }

        @Override
        protected void onPostExecute(List<DesktopItems> items)
        {
            if (lastFetchedPage > 1)
            {
                mList.addAll(items);
                mDesktopRecyclerView.getAdapter().notifyDataSetChanged();
            } else
            {
                mList = items;
                setupAdapter();
            }
            lastFetchedPage++;

            mList = items;
            setupAdapter();
        }
    }
}