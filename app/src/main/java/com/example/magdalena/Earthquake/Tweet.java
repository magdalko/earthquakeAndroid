package com.example.magdalena.json1;

/**
 * Created by Magdalena on 2015-09-02.
 */
public class Tweet {
    String tweetBy;
    String tweet;

    public Tweet(String tweetBy, String tweet) {
        this.tweetBy = tweetBy;
        this.tweet = tweet;
    }

    public String getTweetBy() {
        return tweetBy;
    }

    public void setTweetBy(String tweetBy) {
        this.tweetBy = tweetBy;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }
}
