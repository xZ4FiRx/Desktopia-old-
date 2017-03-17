package zafirmcbryde.com.desktopia.Controller.Fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import zafirmcbryde.com.desktopia.Controller.Util.RedditParser;
import zafirmcbryde.com.desktopia.Model.DesktopItems;
import zafirmcbryde.com.desktopia.R;


public class DesktopGalleryFragment extends Fragment
{
    private RecyclerView mDesktopRecyclerView;
    private List<DesktopItems> mList = new ArrayList<>();
    private int count;
    private String after, placeHolder;
    private Parcelable recyclerViewState;


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
        new FetchItemsTask().execute(after);
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

        mDesktopRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                recyclerViewState = mDesktopRecyclerView.getLayoutManager().onSaveInstanceState();
                mDesktopRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                PhotoAdapter adapter = (PhotoAdapter) recyclerView.getAdapter();
                int lastPostion = adapter.getLastBoundPosition();
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int loadBufferPosition = 1;
                if (lastPostion >= adapter.getItemCount() - layoutManager.getSpanCount() - loadBufferPosition)
                {
                    new FetchItemsTask().execute(after);
                }
            }
        });

        mDesktopRecyclerView.addOnItemTouchListener(new DesktopGalleryFragment.
                RecyclerTouchListener(

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

        return v;
    }

    private void setupAdapter()
    {
        if (isAdded())
        {
            mDesktopRecyclerView.setAdapter(new PhotoAdapter(mList));
        }
    }

    interface ClickListener
    {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
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

        @Override
        protected List<DesktopItems> doInBackground(String... params)
        {
            return new RedditParser().fetchItems(params[0]);
        }

        @Override
        protected void onPostExecute(List<DesktopItems> items)
        {
            getAfter(DesktopItems.getAfter()); //Getting the after value. Very important.

            if (count > 1)
            {
                mList.addAll(items);
                mDesktopRecyclerView.getAdapter().notifyDataSetChanged();

            } else
            {
                mList = items;
                recyclerViewState = mDesktopRecyclerView.getLayoutManager().onSaveInstanceState();//Save state
                mDesktopRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);//Restore state
                setupAdapter();
            }
            count++;
        }
    }
}