package com.example.charitycare;

import android.content.Context;
import android.content.SharedPreferences;

class UserType {
    static String getUserType(Context context){
        SharedPreferences pref = context.getSharedPreferences("UserType", Context.MODE_PRIVATE);
        String userType = "";
        if(pref.contains("USERTYPE")){
            userType = pref.getString("USERTYPE", "");
        }
        return  userType;
    }
}
