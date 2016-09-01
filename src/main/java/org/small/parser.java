package org.small;


import org.small.facebook.FacebookListAnalysis;
import org.small.facebook.FacebookPostAnalysis;
import org.small.twitter.TwitterListAnalysis;
import org.small.twitter.TwitterPostAnalysis;
import org.small.youtube.YoutubeListAnalysis;
import org.small.youtube.YoutubePostAnalysis;

import java.io.FileOutputStream;
import java.io.IOException;

public class parser {

    public static void main(String[] args) {
        //util.utf8TradChToSimpCh("");

//        if (args.length != 3) {
//            System.out.println("Arguments count must be 3.");
//            return;
//        }
        String html = "C:\\Users\\angelo\\Desktop\\doubi.html";
        String path = "C:\\Users\\angelo\\Desktop\\j.json";
        String rule = "facebook.post";

        //goto different path by html difference
        String json = null;
        try {
            json = Html2Json(html, rule);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //save results
        byte[] buff = new byte[]{};
        try {
            if (json != null) {
                buff = json.getBytes();
            }
            FileOutputStream out = new FileOutputStream(path);
            out.write(buff, 0, buff.length);
            out.flush();
            out.close(); //不关内存会爆掉？
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String Html2Json(String html, String rule) throws Exception {
        String json = null;

        if ("facebook.list".equals(rule)) {
            json = parseFacebookList(html);
        } else if ("facebook.post".equals(rule)) {
            json = parseFacebookPost(html);
        } else if ("twitter.list".equals(rule)) {
            json = parseTwitterList(html);
        } else if ("twitter.post".equals(rule)) {
            json = parseTwitterPost(html);
        } else if ("youtube.list".equals(rule)) {
            json = parseYoutubeList(html);
        } else if ("youtube.post".equals(rule)) {
            json = parseYoutubePost(html);
        } else {
            throw new Exception();
        }

        System.out.println("Parser exit.");

        return json;
    }

    private static String parseYoutubeList(String html) {
        YoutubeListAnalysis p = new YoutubeListAnalysis(html);
        return p.getJson();
    }

    private static String parseTwitterList(String html) {
        TwitterListAnalysis p = new TwitterListAnalysis(html);
        return p.getJson();
    }

    private static String parseFacebookList(String html) {
        FacebookListAnalysis p = new FacebookListAnalysis(html);
        return p.getJson();
    }

    public static String parseFacebookPost(String url) {
        FacebookPostAnalysis p = new FacebookPostAnalysis(url);
        return p.getJson();
    }

    public static String parseYoutubePost(String url) {
        YoutubePostAnalysis p = new YoutubePostAnalysis(url);
        return p.getJson();
    }

    private static String parseTwitterPost(String url) {
        TwitterPostAnalysis p = new TwitterPostAnalysis(url);
        return p.getJson();
    }

}
