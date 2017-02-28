package zafirmcbryde.com.desktopia.Model;

public class SubredditModel
{
    private String subreddit;
    private Integer score;
    private String author;
    private String subredditNamePrefixed;
    private String url;
    private String title;

    public SubredditModel(String subreddit, Integer score, String author, String subredditNamePrefixed, String url, String title)
    {
        this.subreddit = subreddit;
        this.score = score;
        this.author = author;
        this.subredditNamePrefixed = subredditNamePrefixed;
        this.url = url;
        this.title = title;
    }


    public String getSubreddit()
    {
        return subreddit;
    }

    public void setSubreddit(String subreddit)
    {
        this.subreddit = subreddit;
    }

    public Integer getScore()
    {
        return score;
    }

    public void setScore(Integer score)
    {
        this.score = score;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getSubredditNamePrefixed()
    {
        return subredditNamePrefixed;
    }

    public void setSubredditNamePrefixed(String subredditNamePrefixed)
    {
        this.subredditNamePrefixed = subredditNamePrefixed;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}

