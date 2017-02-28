package zafirmcbryde.com.desktopia.Controller.Retro;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import zafirmcbryde.com.desktopia.MainActivity;

public class DesktopPosts extends AsyncTask<MainActivity, Integer, JSONObject>
{
    private MainActivity desktop;

    @Override
    protected JSONObject doInBackground(MainActivity... desktops)
    {
        JSONObject object = null;

        desktop = desktops[0];

        //The try and catch are for the error that will be thrown when the URL is tried.
        try
        {
            URL redditposts = new URL("https://www.reddit.com/r/battlestations/new/.json");

            //Open URL connection
            HttpURLConnection connection = (HttpURLConnection) redditposts.openConnection();
            connection.connect();

            //This will equal 200 if connection to the URL was successful
            int status = connection.getResponseCode();

            //Check to see if the status code is equal to the connection code.
            if(status == connection.HTTP_OK)
            {
                //Parsing data from URL
                object = RedditPostParser.getInstance().parseInputStream(connection.getInputStream());
            }
        }
        catch (MalformedURLException error)
        {
            Log.e("DesktopPosts(doInBackground)","MailformedURL:" + error);
        }
        catch (IOException error)
        {
            Log.e("DesktopPosts(doInBackground)","IOException:" + error);
        }

        return object;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject)
    {
        RedditPostParser.getInstance().readRedditFeed(jsonObject);
        desktop.updateUserInterface();
    }
}

