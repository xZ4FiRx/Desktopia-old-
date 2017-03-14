package zafirmcbryde.com.desktopia.Model;

import android.net.Uri;

import java.io.Serializable;

public class DesktopItems implements Serializable
{
    private String author;
    private String title;
    private String url;
    private String permalink;
    private String thumbnail;
    private String after;
    private String before;

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    private String domain;


    public String getAfter()
    {
        return after;
    }

    public void setAfter(String after)
    {
        this.after = after;
    }

    public String getBefore()
    {
        return before;
    }

    public void setBefore(String before)
    {
        this.before = before;
    }

    public String getThumbnail()
    {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    public String getPermalink()
    {
        return permalink;
    }

    public void setPermalink(String permalink)
    {
        this.permalink = permalink;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Uri getPhotoPageUri()
    {
        return Uri.parse("https://www.reddit.com")
                .buildUpon()
                .appendPath(permalink)
                .build();
    }

}
