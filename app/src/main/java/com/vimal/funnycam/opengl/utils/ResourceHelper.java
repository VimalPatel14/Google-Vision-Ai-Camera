/**
 * Created by Vimal on July-2021.
 */
package com.vimal.funnycam.opengl.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceHelper {

    public static String readFile(Context context, int resourceId){
        StringBuilder builder = new StringBuilder();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine())!=null){
                builder.append(nextLine);
                builder.append('\n');
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return builder.toString();
    }
}
