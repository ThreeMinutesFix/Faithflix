package com.primeplay.faithflix.models;

public class TrendingList {
    private int ID;
    private int Type;
    private int contentType;
    private String Thumbnail;
    private String custom_tag;
    private String custom_tag_background_color;
    private String custom_tag_text_color;

    public TrendingList(int ID, int type, int contentType, String thumbnail, String custom_tag, String custom_tag_background_color, String custom_tag_text_color) {
        this.ID = ID;
        Type = type;
        this.contentType = contentType;
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

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
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
