package com.leancloud.im.guide.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author zhangjunpu
 * @date 2016/12/6
 */

public class AssetsUtils {

    public static String getFromAssets(String fileName, Context context) {
        String result = "";
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                result += line;
                result += "\n";
            }
            bufReader.close();
            inputReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
