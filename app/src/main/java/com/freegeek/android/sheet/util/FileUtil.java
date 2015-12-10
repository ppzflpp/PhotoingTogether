package com.freegeek.android.sheet.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;

import android.provider.MediaStore.Images;

/**
 * Created by rtugeek@gmail.com on 2015/11/8.
 */
public class FileUtil {
    public static String USER_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + APP.STRING.APP_EN_NAME + File.separator;
    public static String IMAGE_PATH = USER_FILE_PATH + "image" +File.separator;
    public static String IMAGE_PROJECT_PATH = IMAGE_PATH + "project" + File.separator;
    public static String IMAGE_TEMP_PATH = IMAGE_PATH + "temp" + File.separator;
    public static String IMAGE_BG_PATH = IMAGE_PATH + "bg" + File.separator;
    public static String IMAGE_USER_AVATAR_FILE = IMAGE_PATH + "avatar.png";
    public static String IMAGE_MAIN_BG = IMAGE_PATH + "main_bg.png";
    public static String RING_PATH = USER_FILE_PATH + "ringtone" + File.separator;

    /**
     * 判读文件目录是否存在，不存在则创建
     * @param file
     * @return
     */
    public static boolean makePath(File file){
        if(file.isDirectory()){
            return file.mkdirs();
        }else{
            return new File(file.getParent()).mkdirs();
        }
    }

    /**
     * 获取临时文件
     * @param name +format eg:.avatar.png
     * @return
     */
    public static File getTempFile(String name){
        File file = new File(FileUtil.IMAGE_TEMP_PATH +name);
        makePath(file);
        return file;
    }



    /**
     * 使用文件通道的方式复制文件
     *
     * @param s
     *            源文件
     * @param t
     *            复制到的新文件
     */

    public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;

        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**获取文件格式
     * @param file
     * @return
     */
    public static String getFileFormat(File file){
        String fileName=file.getName();
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    /**判断uri文件是否存在
     * @param context
     * @param uri
     * @return
     */
    public static boolean uriFileExists(Context context,Uri uri){
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        if(cursor == null) return false;
        cursor.moveToFirst();
        String path = "";
        if (cursor != null) {
            path= cursor.getString(1);
            cursor.close();
        }
        return new File(path).exists();
    }

    public static String getImagePath(int index){
        String path = IMAGE_BG_PATH + index +".png";
        makePath(new File(path));
        return path;
    }
    public static String getProjectImagePath(String index){
        return IMAGE_PROJECT_PATH + index +".png";
    }

    public static boolean fileExists(String path){
        return new File(path).exists();
    }


    //递归删除文件及文件夹
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    public static File getImageFile(String name){
        File file = new File(IMAGE_PATH + name + ".png");
        makePath(file);
        return file;
    }

    /**
     * 保存Bitmap
     * @param bitmap
     * @param fileName 文件名
     */
    public static void saveBitmap(Bitmap bitmap,String fileName){
        File myCaptureFile = getImageFile(fileName);
        makePath(myCaptureFile);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(
                    new FileOutputStream(myCaptureFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 通过Uri返回File文件
     * 注意：通过相机的是类似content://media/external/images/media/97596
     * 通过相册选择的：file:///storage/sdcard0/DCIM/Camera/IMG_20150423_161955.jpg
     * 通过查询获取实际的地址
     * @param uri
     * @return
     */
    public static File getFileByUri(Context context,Uri uri) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns._ID, Images.ImageColumns.DATA }, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();
            if(path == null) return null;
            return new File(path);
        } else {
            Logger.i( "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }
}
