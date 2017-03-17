package zafirmcbryde.com.desktopia.Controller.Util;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import zafirmcbryde.com.desktopia.Model.DesktopItems;

public class RedditParser
{
    private static final String TAG = "RedditParser";
    private DesktopItems mItems;


    public byte[] getUrlBytes(String urlSpec) throws IOException
    {
        //Created a URL object from a string.
        URL url = new URL(urlSpec);
        /*Create a connection object pointed at the URL. openConnection()
        returns a URLConnection, but because you are connecting to an http URL, you can cast it to
        HttpURLConnection This gives you HTTP-specific interfaces for working with request methods,
        response codes, streaming methods, and more. HttpURLConnection represents a connection, but
        it will not actually connect to your endpoint until you (or getOutputStream() for POST
        calls). Until then, you cannot get a valid response code.*/
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            /*HttpURLConnection represents a connection, but it will not actually connect to your
            endpoint until you call getInputStream() (or getOutputStream() for POST calls). Until
            then, you cannot get a valid response code.*/
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            /* read() runs repeatedly until the connection runs out of data. The InputStream will
            yield bytes as they are available. When done, close it and spit out your
            ByteArrayOutputStream’s byte array.*/
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

    public String getUrlString(String urlSpec) throws IOException
    {
        return new String(getUrlBytes(urlSpec));
    }

    public List<DesktopItems> fetchItems(String after)
    {
        List<DesktopItems> items = new ArrayList<>();
        int count = 0;
        int counter = count + 25;

        try
        {
            String url = Uri.parse("https://www.reddit.com/r/battlestations/hot.json")
                    .buildUpon()
                    .appendQueryParameter("count", String.valueOf(counter))
                    .appendQueryParameter("after", after)
                    .build()
                    .toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (JSONException je)
        {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe)
        {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return items;
    }

    private void parseItems(List<DesktopItems> items, JSONObject jsonBody)
            throws IOException, JSONException
    {
        DesktopItems di1 = new DesktopItems();
        JSONObject dataPost = jsonBody.getJSONObject("data");
        di1.setAfter(dataPost.getString("after"));
        items.add(di1);
        JSONArray childPost = dataPost.getJSONArray("children");
        for (int i = 0; i < childPost.length(); i++)
        {
            JSONObject dataPost2 = childPost.getJSONObject(i).getJSONObject("data");
            DesktopItems di2 = new DesktopItems();
            di2.setThumbnail(dataPost2.getString("thumbnail"));
            di2.setAuthor(dataPost2.getString("author"));
            di2.setPermalink(dataPost2.getString("permalink"));
            di2.setTitle(dataPost2.getString("title"));
            di2.setDomain(dataPost2.getString("domain"));

            JSONObject previewPost = dataPost2.getJSONObject("preview");
            JSONArray imagesPost = previewPost.getJSONArray("images");
            for (int y = 0; y < imagesPost.length(); y++)
            {
                if (!Objects.equals(di2.getDomain(), "self.battlestations"))
                {
                    JSONObject sourcePost = imagesPost.getJSONObject(y).getJSONObject("source");
                    di2.setUrl(sourcePost.getString("url"));
                    items.add(di2);
                }
            }
        }
    }
}
