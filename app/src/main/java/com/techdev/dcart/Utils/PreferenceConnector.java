package com.techdev.dcart.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceConnector {

    public static final String PREF_NAME = "MY_PREF";
    public static final int MODE = Context.MODE_PRIVATE;

    public static String login_id = "login_id";
    public static String user_id = "user_id";


    /**
     * @param context
     * @param key
     * @param value
     */
    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();

    }

    /**
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }


    /**
     * @param context
     * @return
     */
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    /**
     * @param context
     * @return
     */
    public static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }
}
