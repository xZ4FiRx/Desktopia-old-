package zafirmcbryde.com.desktopia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import zafirmcbryde.com.desktopia.Controller.Retro.DesktopPosts;
import zafirmcbryde.com.desktopia.Controller.Retro.RecyclerViewAdapter;
import zafirmcbryde.com.desktopia.Controller.Retro.RedditPostParser;
import zafirmcbryde.com.desktopia.Model.SubredditModel;

public class MainActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    SubredditModel mSubredditModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSubredditModel.getTitle();
        mSubredditModel.getUrl();

        new DesktopPosts().execute(this);
    }

    public void updateUserInterface()
    {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(RedditPostParser.getInstance().redditPosts);
        recyclerView.setAdapter(adapter);
    }
}
