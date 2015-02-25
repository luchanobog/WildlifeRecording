package com.example.luchano.wildliferecording;

import android.net.Uri;

/**
 * Created by Luchano on 13/02/2015.
 */
public class Log {

    private String _name, _number, _location, _comments;
    private Uri _imageURI;
    private int _id;

    public Log(int id, String name,String number, String location, String comments, Uri imageURI)
    {
        _id = id;
        _name=name;
        _number=number;
        _location=location;
        _comments=comments;
        _imageURI=imageURI;
    }

    public int getId() {
        return _id;
    }

    public String get_name() {
        return _name;
    }

    public String get_number() {
        return _number;
    }

    public String get_location() {
        return _location;
    }

    public String get_comments() {
        return _comments;
    }

    public Uri get_imageURI() {
        return _imageURI;
    }
}
