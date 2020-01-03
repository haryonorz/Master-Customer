package com.example.mastercustomer.utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.mastercustomer.view.home.HomeAct;
import com.example.mastercustomer.view.sign_in.SignInAct;
import com.example.mastercustomer.view.splash_screen.SplashScreenAct;

import java.util.HashMap;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefEditor;
    private Context context;
    private int PRIVATE_MODE = 0;

    private String PREF_NAME = "LOGIN";
    private static String LOGIN = "IS_LOGIN";
    public static String USERNAME = "USERNAME";
    public static String PASSWORD = "PASSWORD";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        prefEditor = sharedPreferences.edit();
    }

    public void createSession(String username, String password){
        prefEditor.putBoolean(LOGIN, true);
        prefEditor.putString(USERNAME, username);
        prefEditor.putString(PASSWORD, password);
        prefEditor.apply();
    }

    public Boolean isLogin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(){
        if (this.isLogin()){
            Intent intent = new Intent(context, HomeAct.class);
            context.startActivity(intent);
            ((SplashScreenAct) context).finish();
        } else {
            Intent intent = new Intent(context, SignInAct.class);
            context.startActivity(intent);
            ((SplashScreenAct) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(USERNAME, sharedPreferences.getString(USERNAME, null));
        user.put(PASSWORD, sharedPreferences.getString(PASSWORD, null));

        return user;
    }

    public void signOut(){
        prefEditor.clear();
        prefEditor.commit();
    }
}
