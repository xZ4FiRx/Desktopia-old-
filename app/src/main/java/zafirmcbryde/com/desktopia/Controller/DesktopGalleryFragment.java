package zafirmcbryde.com.desktopia.Controller;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import zafirmcbryde.com.desktopia.R;

public class DesktopGalleryFragment extends Fragment
{
    private RecyclerView mDesktopRecyclerView;
    private static final String TAG = "DesktopGalleryFragment";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_desktop_gallery, container, false);
        mDesktopRecyclerView = (RecyclerView) v
                .findViewById(R.id.fragment_desktop_gallery_recycler_view);
        mDesktopRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        return v;
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, Void>
    {
        //
        @Override
        protected Void doInBackground(Void... params)
        {
            new RedditParser().fetchItems();
            return null;
        }
    }

}

