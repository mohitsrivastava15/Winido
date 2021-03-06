package com.winido.app.helper;

/**
 * Created by mohit on 12/6/17.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


/**
 * This class helps to store and retrive class object in the shared preferences
 */

public class ComplexPreferences {
    private static ComplexPreferences       complexPreferences;
    private final Context                   context;
    private final SharedPreferences         preferences;
    private final SharedPreferences.Editor  editor;
    private static Gson                     GSON            = new GsonBuilder().setDateFormat("EEE MMM dd HH:mm:ss Z yyyy").create();
    Type                                    typeOfObject    = new TypeToken<Object>(){}
            .getType();

    private ComplexPreferences(Context context, String namePreferences, int mode) {
        this.context = context;
        if (namePreferences == null || namePreferences.equals("")) {
            namePreferences = "abhan";
        }
        preferences = context.getSharedPreferences(namePreferences, mode);
        editor = preferences.edit();
    }

    public static ComplexPreferences getComplexPreferences(Context context,
                                                           String namePreferences, int mode) {
        if (complexPreferences == null) {
            complexPreferences = new ComplexPreferences(context,
                    namePreferences, mode);
        }
        return complexPreferences;
    }

    public void putObject(String key, Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Object is null");
        }
        if (key.equals("") || key == null) {
            throw new IllegalArgumentException("Key is empty or null");
        }
        editor.putString(key, GSON.toJson(object));
    }

    public void commit() {
        editor.commit();
    }

    public <T> T getObject(String key, Class<T> a) {
        String gson = preferences.getString(key, null);
        if (gson == null) {
            return null;
        }
        else {
            try {
                return GSON.fromJson(gson, a);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Object stored with key "
                        + key + " is instance of other class");
            }
        }
    }
}
