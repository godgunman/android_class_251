package com.example.csie.simpleui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by csie on 2015/3/26.
 */
public class Utils {

    public static Bitmap bytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Uri getOutputUri() {
        File fileDir = Environment.
                getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM);
        if(fileDir.exists() == false) {
            fileDir.mkdirs();
        }

        File file = new File(fileDir, "photo.png");
        Log.d("debug", file.getPath());
        return Uri.fromFile(file);
    }
}
