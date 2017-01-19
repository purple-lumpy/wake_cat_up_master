package com.example.yjx.wake_cat_up;

/**
 * Created by yjx on 2017-1-19.
 */

public class StringEncode {

    /**
     * 将字符串转换成0 1 字符流
     */
    public String Str2Bit(String astring)
    {
        String ZERO = "00000000";
        StringBuffer result = new StringBuffer();
        String temp = "";
        byte[] bys = astring.getBytes();

        for (int i = 0; i < bys.length; i++) {
            temp = Integer.toBinaryString(bys[i]);
            if (temp.length() > 8)
                temp = temp.substring(temp.length() - 8);
            else if (temp.length()< 8)
                temp = ZERO.substring(temp.length()) + temp;
            result.append(temp);
        }
        String re=result.toString();
        return re;
    }

    /**
     * 将0 1 字符流转换成字符串
     */
    public String Bit2Str(String astring)
    {
        byte[] gettt= new byte[(astring.length()/8)];

        String temp="";
        for(int j=0;j<(int) (astring.length()/8);j++)
        {
            temp=astring.substring(j*8,j*8 + 8);
            gettt[j]=(byte)Integer.parseInt(temp,2);
        }
        String result = new String(gettt);
        return result;
    }

}
