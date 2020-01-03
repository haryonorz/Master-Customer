package com.example.mastercustomer.utility;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GlobalFunc {

    @SuppressLint("SimpleDateFormat")
    public static String getDateNow(){
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }
}
