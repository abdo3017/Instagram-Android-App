package com.example.instaapp.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/**
 * Created by Abdo GHazi
 */
public class Preferences_Utils {

    public Preferences_Utils() {
    }

    public static boolean Save_ID(String ID, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.User_ID,ID);
        editor.apply();
        return  true;
    }

    public static boolean Save_Password(String password, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PASSWORD,password);
        editor.apply();
        return  true;
    }

    public static boolean Save_Email(String email, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.Email,email);
        editor.apply();
        return  true;
    }
    public static boolean Save_Status(String status, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.STATUS,status);
        editor.apply();
        return  true;
    }

    public static boolean Save_Name(String Name, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.USERNAME,Name);
        editor.apply();
        return  true;
    }

    public static boolean Save_Gender(String Gender, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.Gender,Gender);
        editor.apply();
        return  true;
    }
    public static boolean Save_About(String About, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.About,About);
        editor.apply();
        return  true;
    }
    public static boolean Save_Image(String Image, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.IMAGEPROFILE,Image);
        editor.apply();
        return  true;
    }

    public static String get_ID(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.User_ID,null);
    }

    public static String get_Password(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.PASSWORD,null);
    }
    public static String get_Status(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.STATUS,null);
    }
    public static String get_Email(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.Email,null);
    }

    public static String get_Username(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.USERNAME,null);
    }
    public static String get_Image(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.IMAGEPROFILE,null);
    }

}
