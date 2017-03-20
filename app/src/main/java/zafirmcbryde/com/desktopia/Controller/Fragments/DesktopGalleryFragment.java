package zafirmcbryde.com.desktopia.Controller.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import zafirmcbryde.com.desktopia.Controller.Util.RedditParser;
import zafirmcbryde.com.desktopia.Model.DesktopItems;
import zafirmcbryde.com.desktopia.R;


public class DesktopGalleryFragment extends Fragment
{
    private RecyclerView mDesktopRecyclerView;
    private String TAG = "DesktopGalleryFragment";
    private List<DesktopItems> mList = new ArrayList<>();
    private int count;
    private String after, nullString = null;
    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";
    private SwipeRefreshLayout swipeContainer;


    public static DesktopGalleryFragment newInstance()
    {
        return new DesktopGalleryFragment();
    }

    public String getAfter(String s)
    {
        return this.after = s;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        ConnectivityManager cm = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected)
        {
            Toast.makeText(getContext(), "NO NETWORK CONNECTIVITY FOUND.", Toast.LENGTH_LONG).show();
        }

        new Timer().schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Looper.prepare();
                new FetchItemsTask().execute(nullString);
            }
        }, 1500);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_desktop_gallery, container, false);
        mDesktopRecyclerView = (RecyclerView) v
                .findViewById(R.id.fragment_desktop_gallery_recycler_view);

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        final GridLayoutManager mLayoutManager;
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mDesktopRecyclerView.setLayoutManager(mLayoutManager);

        mDesktopRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                }
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
                {
                }
                if (newState == RecyclerView.SCROLL_STATE_SETTLING)
                {
                    PhotoAdapter adapter = (PhotoAdapter) recyclerView.getAdapter();
                    int lastPostion = adapter.getLastBoundPosition();
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    int loadBufferPosition = 1;
                    if (lastPostion >= adapter.getItemCount() - layoutManager.getSpanCount() - loadBufferPosition)
                    {

                        Log.i(TAG, "NEW PAGE WAS CALLED! the after key is = " + after.toString());
                        new FetchItemsTask().execute(after);
                    }
                }
            }
        });

        mDesktopRecyclerView.addOnItemTouchListener(new DesktopGalleryFragment.RecyclerTouchListener(

                getContext(), mDesktopRecyclerView, new

                ClickListener()
                {
                    @Override
                    public void onClick(View view, int position)
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
                    public void onLongClick(View view, int position)
                    {
                        //Later
                    }
                }));


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                setupAdapter();
                mList.clear();
                new FetchItemsTask().execute(nullString);
                swipeContainer.setRefreshing(false);
            }
        });


        return v;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null)
        {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mDesktopRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mDesktopRecyclerView.getLayoutManager().onSaveInstanceState());
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

        public void clear()
        {
            mGalleryItems.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<DesktopItems> list)
        {
            mGalleryItems.addAll(list);
            notifyDataSetChanged();
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
                    .placeholder(R.mipmap.ic_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .override(200, 200)
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

    private class FetchItemsTask extends AsyncTask<String, Void, List<DesktopItems>>
    {

        private ProgressDialog progDailog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progDailog.setMessage("Loading images...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
        }

        @Override
        protected List<DesktopItems> doInBackground(String... params)
        {
            return new RedditParser().fetchItems(params[0]);
        }

        @Override
        protected void onPostExecute(List<DesktopItems> items)
        {
            getAfter(DesktopItems.getAfter());
            progDailog.dismiss();
            if (count > 1)
            {
                mList.addAll(items);
                mDesktopRecyclerView.getAdapter().notifyDataSetChanged();
            } else
            {
                mList = items;
                setupAdapter();
                mList.clear();
                new FetchItemsTask().execute(nullString);
            }
            count++;
        }
    }
}