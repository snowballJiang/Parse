package org.small.util;

import com.spreada.utils.chinese.ZHConverter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class util {
    public static String getCurrentDatetime(String format) {
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCurrentDatetime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static String utf8TradChToSimpCh(String simpCh) {
        /*String str1="简体[大人，我觉得此事有蹊跷] 繁體轉簡體，元芳，你怎麼看？";
        String simpleStr=ZHConverter.convert(str1, ZHConverter.SIMPLIFIED);
        System.out.println(simpleStr);
        String str2="简体转繁体,大人，我觉得此事有蹊跷";
        String tradiStr=ZHConverter.convert(str2, ZHConverter.TRADITIONAL);
        System.out.println(tradiStr);
*/
        if (simpCh.length() > 0)
            return ZHConverter.convert(simpCh, ZHConverter.SIMPLIFIED);
        else
            return "";
    }
}
