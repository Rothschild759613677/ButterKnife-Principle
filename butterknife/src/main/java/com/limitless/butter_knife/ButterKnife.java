package com.limitless.butter_knife;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * Created by Nick on 2018/10/24
 *
 * @author Nick
 */
public class ButterKnife {


    public static void bind(@NonNull Activity activity) {
        String className = activity.getClass().getName() + "_ViewBinder";

        try {
            Class<?> aClass = Class.forName(className);
            ViewBinder instance = (ViewBinder) aClass.newInstance();
            instance.bind(activity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
