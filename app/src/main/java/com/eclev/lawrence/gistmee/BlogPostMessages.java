package com.eclev.lawrence.gistmee;

/**
 * Created by SYSTEM on 9/22/2017.
 */

public class BlogPostMessages{
    private String title, description, content, photoUrl, date, timePosted;

    public BlogPostMessages() {
    }


    public BlogPostMessages(String title, String description, String content,
                            String photoUrl, String date, String time) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.photoUrl = photoUrl;
        this.date = date;
        this.timePosted = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(String time) {
        this.timePosted = time;
    }

}
