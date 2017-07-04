package com.project.indoorlocalization.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;

import com.project.indoorlocalization.indoormapview.Position;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ljm on 2017/5/9.
 */
public class Utils {
    private static Toast toast;

    public static void setToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(msg);
            toast.show();
        }
    }

    public static Map<String, Pair> readShopData(Context context) {

        String tmp = readTxtFile(context, "map.txt");
        //Log.v("#####", tmp);
        String[] data = tmp.split("#");
        Map<String, Pair> dataMap = new HashMap<>();
        for (int i = 1; i < data.length; ++i) {
            //Log.v("#####1", data[i]+"");
            String[] s = data[i].split(",");//Log.v("#####1", data[i]+"");
            dataMap.put(s[1], new Pair(s[2], null));
        }

        tmp = readTxtFile(context, "location.txt");
        //Log.v("#####", tmp);
        data = tmp.split("#");
        for (int i = 1; i < data.length; ++i) {
            String[] s = data[i].split(",");
            if (dataMap.get(s[0]) == null) continue;
            //String first = (String) dataMap.get(s[0]);
            double x = Double.parseDouble(s[1].substring(1));
            double y = Double.parseDouble(s[2].substring(0, s[2].length()-1));
            //Log.v("#####2", s[0]+"");
            dataMap.put(s[0], new Pair(dataMap.get(s[0]).first, new Position(y, Data.mMaxX - x)));
        }

        return dataMap;
    }
    private static String readTxtFile(Context context, String name) {
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            // context.getAssets().open("map.txt");
            bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(name)));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line+"#");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    //保存图片
    public static void saveBitmap(Bitmap bitmap, String path, String name) {
        if (bitmap == null) return;

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(path+"/"+name);
        try {
            file.createNewFile();

            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String parseSensorInfo(float[] acc_data, float[] mag_data, float[] ori_data,
                                         float[] gyr_data) {
        if (acc_data == null || mag_data == null || ori_data == null || gyr_data == null) {
            return "";
        }

        JSONObject acc_object = new JSONObject();
        JSONObject mag_object = new JSONObject();
        JSONObject ori_object = new JSONObject();
        JSONObject gyr_object = new JSONObject();

        JSONObject tmp = new JSONObject();
        try {
            acc_object.put("x", acc_data[0]);
            acc_object.put("y", acc_data[1]);
            acc_object.put("z", acc_data[2]);

            mag_object.put("x", mag_data[0]);
            mag_object.put("y", mag_data[1]);
            mag_object.put("z", mag_data[2]);

            ori_object.put("x", ori_data[0]);
            ori_object.put("y", ori_data[1]);
            ori_object.put("z", ori_data[2]);

            gyr_object.put("x", gyr_data[0]);
            gyr_object.put("y", gyr_data[1]);
            gyr_object.put("z", gyr_data[2]);

            tmp.put("acc_data", acc_object);
            tmp.put("mag_data", mag_object);
            tmp.put("ori_data", ori_object);
            tmp.put("gyr_data", gyr_object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tmp.toString();
    }

    //视频录制需要的权限(相机，录音，外部存储)
    private static String[] VIDEO_PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static List<String> NO_VIDEO_PERMISSION = new ArrayList<String>();
    private static final int REQUEST_CAMERA = 0;
    public static void checkCameraPermission(Activity context) {
        NO_VIDEO_PERMISSION.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < VIDEO_PERMISSION.length; i++) {
                if (ActivityCompat.checkSelfPermission(context, VIDEO_PERMISSION[i]) != PackageManager.PERMISSION_GRANTED) {
                    NO_VIDEO_PERMISSION.add(VIDEO_PERMISSION[i]);
                }
            }
            if (NO_VIDEO_PERMISSION.size() == 0) {
                //Intent intent = new Intent(this, RecordVideoActivity.class);
                //startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(context, NO_VIDEO_PERMISSION.toArray(new String[NO_VIDEO_PERMISSION.size()]), REQUEST_CAMERA);
            }
        } else {
            //Intent intent = new Intent(this, RecordVideoActivity.class);
            //startActivity(intent);
        }
    }

    //拍照上传需要的权限(相机，录音，外部存储)
    private static String[] VIDEO_PERMISSION2 = {
            Manifest.permission.CAMERA
    };
    private static List<String> NO_VIDEO_PERMISSION2 = new ArrayList<String>();
    private static final int REQUEST_CAMERA2 = 0;
    public static void checkTakePicturePermission(Activity context) {
        NO_VIDEO_PERMISSION2.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < VIDEO_PERMISSION2.length; i++) {
                if (ActivityCompat.checkSelfPermission(context, VIDEO_PERMISSION2[i]) != PackageManager.PERMISSION_GRANTED) {
                    NO_VIDEO_PERMISSION2.add(VIDEO_PERMISSION2[i]);
                }
            }
            if (NO_VIDEO_PERMISSION2.size() == 0) {
                //Intent intent = new Intent(this, RecordVideoActivity.class);
                //startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(context, NO_VIDEO_PERMISSION2.toArray(new String[NO_VIDEO_PERMISSION2.size()]), REQUEST_CAMERA2);
            }
        } else {
            //Intent intent = new Intent(this, RecordVideoActivity.class);
            //startActivity(intent);
        }
    }



    public static float getVectorLen(float[] v) {
        return (float)Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
    }

    public static float[][] matrixProduct(float m1[][], float m2[][]) {
        float m0[][] = new float[m1.length][m1.length];
        for (int i = 0; i < m1.length; ++i) {
            for (int j = 0; j < m1.length; ++j) {
                m0[i][j] = 0;
                for (int k = 0; k < m1.length; ++k) {
                    m0[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return m0;
    }
    //矩阵求逆，
    public static float GetMatrixNorm(float array[][], int n) {
        if (n == 1) {
            return array[0][0];
        }
        float ans = 0;
        float temp[][] = new float[array.length][array.length];
        int i, j, k;
        for (i = 0; i < n; i++) {
            for (j = 0; j < n - 1; j++) {
                for (k = 0; k < n - 1; k++) {
                    int y = (k >= i) ? k + 1 : k;
                    temp[j][k] = array[j + 1][y];

                }
            }
            float t = GetMatrixNorm(temp, n - 1);
            if (i % 2 == 0) {
                ans += array[0][i] * t;
            }
            else {
                ans -= array[0][i] * t;
            }
        }
        return ans;
    }
    public static void  GetMatrix(float array[][], int n, float ans[][]) {
        if (n == 1) {
            ans[0][0] = 1;
            return;
        }
        int i, j, k, t;
        float temp[][] = new float[array.length][array.length];
        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                for (k = 0; k < n - 1; k++) {
                    for (t = 0; t < n - 1; t++) {
                        int x = k >= i ? k + 1 : k;
                        int y = t >= j ? t + 1 : t;
                        temp[k][t] = array[x][y];
                    }
                }
                ans[j][i] = GetMatrixNorm(temp, n - 1);
                if ((i + j) % 2 == 1) {
                    ans[j][i] = -ans[j][i];
                }
            }
        }
    }
    public static boolean GetInverseMatrix(float src[][], int n, float des[][]) {
        float flag = GetMatrixNorm(src, n);
        float t[][] = new float[src.length][src.length];
        if (flag == 0) {
            return false;
        }
        GetMatrix(src, n, t);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                des[i][j] = t[i][j] / flag;
            }
        }
        return true;
    }

    public static float[] getProjectVector(float[] g0) {
        float[] v0 = new float[]{1,0,0};
        float g0_v0 = g0[0];
        float g0_len = getVectorLen(g0);
        g0_v0 = g0_v0 / g0_len;
        float[] g0_norm = new float[]{g0[0]/g0_len, g0[1]/g0_len, g0[2]/g0_len};

        float[] tmp = new float[]{g0_v0*g0_norm[0], g0_v0*g0_norm[1], g0_v0*g0_norm[2]};
        return new float[]{1- tmp[0], 0 - tmp[1], 0 - tmp[2]};
    }

    public static float[] getQuaternion(float[] v, float t) {
        float w = getVectorLen(v);
        float[] n = new float[]{v[0]/w, v[1]/w, v[2]/w};
        float tmp = (float) Math.sin((w*t)/2);

        return new float[]{n[0]*tmp, n[1]*tmp, n[2]*tmp, (float) Math.cos((w*t)/2)};
    }

    public static float[][] updateM(float[] q, float[][] M) {
        float x = q[0], y = q[1], z = q[2], w = q[3];

        float[][] MR = new float[][]{
                {1- 2*(y*y + z*z),  2*(x*y - z*w),      2*(x*z + y*w)},
                {2*(x*y + z*w),     1 - 2*(x*x + z*z),  2*(y*z - x*w)},
                {2*(x*z - y*w),     2*(y*z + x*w),      1-2*(x*x + y*y) }
        };
        return matrixProduct(MR, M);
    }

    public static float getAngle(float[][] M, float[] vh0, float[] g){

        float[][] inverse_M = new float[3][3];
        if (!GetInverseMatrix(M, 3, inverse_M)) {
            return 0;
        } else {
            float[] vh = new float[]{
                    inverse_M[0][0]*vh0[0]+inverse_M[0][1]*vh0[1]+inverse_M[0][2]*vh0[2],
                    inverse_M[1][0]*vh0[0]+inverse_M[1][1]*vh0[1]+inverse_M[1][2]*vh0[2],
                    inverse_M[2][0]*vh0[0]+inverse_M[2][1]*vh0[1]+inverse_M[2][2]*vh0[2]
            };
            float[] vh1 = getProjectVector(g);
            float vh1_len = getVectorLen(vh1);
            float vh_len = getVectorLen(vh);

            float n1 = vh[0]*vh1[0] + vh[1]*vh1[1] + vh[2]*vh1[2];
            float n2 = vh1_len * vh_len;

            return (float) Math.toDegrees(Math.acos(n1/n2));
        }
    }

}
