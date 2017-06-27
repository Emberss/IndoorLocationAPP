package com.project.indoorlocalization.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljm on 2017/6/27.
 */
public class Data {
    public static List<Bitmap> imgs = new ArrayList<>();
    public static String picture_save_path = "/tempImages";


    public static void recycleBitmap(List<Bitmap> list) {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) != null && ! list.get(i).isRecycled()) {
                list.get(i).recycle();
            }
        }
        list.clear();
    }

    public static String getPictureSavePath() {
        return Environment.getExternalStorageDirectory() + picture_save_path;
    }
}
