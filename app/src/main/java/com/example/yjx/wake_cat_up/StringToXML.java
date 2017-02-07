package com.example.yjx.wake_cat_up;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by yjx on 2017-2-7.
 */

public class StringToXML {

    /*
     *将context字符串的内容，写到xmlFileName指定的文件中
     */
    public static void writeintoxml(String xmlFileName, String context){
        File xmlFile = new File(xmlFileName);
        if(!xmlFile.exists()) {
            try {
                xmlFile.createNewFile();
            }
            catch (IOException e)
            {
                Log.e("IOException", "exception in createNewFile() method");
            }
        }
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(xmlFileName, true);
            bw = new BufferedWriter(fw); // 将缓冲对文件的输出

            bw.write(context); // 写入文件
            bw.flush(); // 刷新该流的缓冲
            bw.close();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                bw.close();
                fw.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
            }
        }
    }

}
