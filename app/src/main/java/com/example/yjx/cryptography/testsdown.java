package com.example.yjx.cryptography;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by yjx on 2016-8-23.
 */
public class testsdown extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EncrypImpl encryp = new EncrypImpl();
        String str ="abc";
//        byte[] de = encryp.encryption(str);
//        String str1 = encryp.decryption(de);
    }

}
