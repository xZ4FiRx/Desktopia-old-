package zafirmcbryde.com.desktopia.Controller.Retro;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import zafirmcbryde.com.desktopia.Model.SubredditModel;

public class RedditPostParser
{
    private static RedditPostParser parser;

    public ArrayList<SubredditModel> redditPosts;

    public RedditPostParser()
    {
        redditPosts = new ArrayList<SubredditModel>();
    }

    public static RedditPostParser getInstance()
    {
        if(parser == null)
        {
            parser = new RedditPostParser();
        }
        return parser;
    }


    //This turn the inputstream into a JSONObject
    public JSONObject parseInputStream(InputStream inputStream)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject object = null;

        String currentLine;

        try
        {
            while ((currentLine = reader.readLine()) != null)
            {
                stringBuilder.append(currentLine);
            }

            JSONTokener jsonTokener = new JSONTokener(stringBuilder.toString());

            object = new JSONObject(jsonTokener);
        }
        catch (IOException error)
        {
            Log.e("Reddit Parser", "IOException" + error);
        }
        catch (JSONException error)
        {
            Log.e("Reddit Parser", "JSONException" + error);
        }

        return object;
    }

    //Retrieving exact JSON data from the subreddit.
    public void readRedditFeed(JSONObject jsonObject)
    {
        redditPosts.clear();

        try
        {
            JSONArray postData = jsonObject.getJSONObject("data").getJSONArray("children");

            for(int i = 0; i  < postData.length(); i++)
            {
                JSONObject post = postData.getJSONObject(i).getJSONObject("data");

                String title = post.getString("title");
                String url = post.getString("url");
                String subReddit = post.getString("subreddit");
                int score = post.getInt("score");
                String author = post.getString("author");
                String subPrefix = post.getString("subreddit");

                SubredditModel model = new SubredditModel(subReddit,score,author,subPrefix,url,title);

                redditPosts.add(model);
            }
        }
        catch (JSONException error)
        {
            Log.e("Reddit Parser", "JSONException" + error);
        }
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException
    {
        //creating a URL object from a string
        URL url = new URL(urlSpec);
        //creates a connection object pointed at the URL(urlSpec)
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally
        {
            connection.disconnect();
        }
    }
}
