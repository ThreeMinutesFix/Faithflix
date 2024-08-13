package com.primeplay.faithflix.models;

public class MovieList {
    private int ID;
    private int Type;
    private String Title;
    private String Year;
    private String Thumbnail;
    private String custom_tag;
    private String custom_tag_background_color;
    private String custom_tag_text_color;

    public MovieList(int ID, int type, String title, String year, String thumbnail, String custom_tag, String custom_tag_background_color, String custom_tag_text_color) {
        this.ID = ID;
        Type = type;
        Title = title;
        Year = year;
        Thumbnail = thumbnail;
        this.custom_tag = custom_tag;
        this.custom_tag_background_color = custom_tag_background_color;
        this.custom_tag_text_color = custom_tag_text_color;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getCustom_tag() {
        return custom_tag;
    }

    public void setCustom_tag(String custom_tag) {
        this.custom_tag = custom_tag;
    }

    public String getCustom_tag_background_color() {
        return custom_tag_background_color;
    }

    public void setCustom_tag_background_color(String custom_tag_background_color) {
        this.custom_tag_background_color = custom_tag_background_color;
    }

    public String getCustom_tag_text_color() {
        return custom_tag_text_color;
    }

    public void setCustom_tag_text_color(String custom_tag_text_color) {
        this.custom_tag_text_color = custom_tag_text_color;
    }
}
