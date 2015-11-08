package com.freegeek.android.sheet.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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


}
