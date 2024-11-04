package com.example.cddd2_nhom6.model;

public class MovieItem {
    private String name;
    private String posterPath;

    public MovieItem(String name, String posterPath) {
        this.name = name;
        this.posterPath = posterPath;
    }

    public String getName() {
        return name;
    }

    public String getPosterPath() {
        return posterPath;
    }
}

