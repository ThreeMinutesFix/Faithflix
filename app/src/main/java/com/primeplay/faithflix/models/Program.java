package com.primeplay.faithflix.models;

public class Program
{
    private String programName;
    private String programTime;
    private String imageUrl;

    public Program(String programName, String programTime, String imageUrl) {
        this.programName = programName;
        this.programTime = programTime;
        this.imageUrl = imageUrl;
    }

    public String getProgramName() { return programName; }
    public String getProgramTime() { return programTime; }
    public String getImageUrl() { return imageUrl; }
}
