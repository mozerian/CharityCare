package com.example.charitycare.data;

import android.content.Context;
import android.content.SharedPreferences;

public class UserType {
    public static String getUserType(Context context){
        SharedPreferences pref = context.getSharedPreferences("UserType", Context.MODE_PRIVATE);
        String userType = "";
        if(pref.contains("USERTYPE")){
            userType = pref.getString("USERTYPE", "");
        }
        return  userType;
    }
}
