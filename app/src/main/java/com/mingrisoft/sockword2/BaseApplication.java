package com.mingrisoft.sockword2;

import android.app.Activity;
import android.app.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by MSI on 18/11/07.
 */

public class BaseApplication extends Application {
    //创建一个Map集合，把Activity加到这个Map集合里
    private static Map<String,Activity>destroyMap = new HashMap<>();
    /**
     * 添加销毁对象的队列
     * <p/>
     * 要销毁的Activity
     */
    public static void addDestroyActivity(Activity activity,String activityName) {
        destroyMap.put(activityName, activity);
    }

    public static void destroyAcitivity(String activityName) {
        Set<String> keySet =destroyMap.keySet();
        for (String key:keySet){
            destroyMap.get(key).finish();
        }
    }

}
