package zafirmcbryde.com.desktopia.Controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumbnailDownloader<T> extends HandlerThread
{
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private Handler mRequestHandler;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();//A ConcurrentHashMap is a thread-safe version of HashMap
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public interface ThumbnailDownloadListener<T>
    {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener)
    {
        mThumbnailDownloadListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler)
    {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared()
    {
        mRequestHandler = new Handler()
        {
            /*check the message type, retrieve the obj value (which will
            be of type T and serves as the identifier for the request),
            and then pass it to handleRequest(…)*/
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == MESSAGE_DOWNLOAD)
                {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    public void queueThumbnail(T target, String url)
    {
        Log.i(TAG, "Got a URL: " + url);
        if (url == null)
        {
            mRequestMap.remove(target);
        } else
        {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }

    public void clearQueue()
    {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }


    /*Check for the existence of a URL. Then you pass the URL to a new instance RedditParser.*/
    private void handleRequest(final T target)
    {
        try
        {
            final String url = mRequestMap.get(target);
            if (url == null)
            {
                return;
            }
            byte[] bitmapBytes = new RedditParser().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            mResponseHandler.post(new Runnable()
            {
                public void run()
                {
                    if (mRequestMap.get(target) != url)
                    {
                        return;
                    }
                    mRequestMap.remove(target);
                }
            });
        } catch (IOException ioe)
        {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }
}
