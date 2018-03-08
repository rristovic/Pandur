package com.pandurbg.android.model;

/**
 * Created by Quantox 1 on 3/8/2018.
 */

public class PostCategory {
    private int _id;
    private String name;
    private String slug;

    public PostCategory() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
