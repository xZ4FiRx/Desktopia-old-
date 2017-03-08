package zafirmcbryde.com.desktopia.Controller;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RedditParser
{
    private static final String TAG = "RedditParser";

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

    public void fetchItems()
    {
        try
        {
            String url = Uri.parse("https://www.reddit.com/r/battlestations/hot.json?limit=101")
                    .buildUpon().build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
        } catch (IOException ioe)
        {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
    }
}
