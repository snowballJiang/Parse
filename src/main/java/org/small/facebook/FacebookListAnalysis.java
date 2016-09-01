/**
 * @author wangwei
 * @version 0.9 2016-01-26
 */

package org.small.facebook;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FacebookListAnalysis {

    Document doc = null;

    public FacebookListAnalysis(String htmlPath) {

        File input = new File(htmlPath);

        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public List<String> getUrlList() {

        Elements ele = doc.getElementsByClass("userContentWrapper");

        List<String> urlList = new ArrayList<String>();
        for (Element anEle : ele)
            try {
                urlList.add("\""+anEle.getElementsByClass("clearfix").first().
                        getElementsByTag("abbr").first().parent().
                        select("a[href*=www.facebook.com]").first().attr("href")+"\"");
            } catch (NullPointerException e) {
                System.out.println("Cannot find youtube list");
            }
        return urlList;
    }*/
    public List<String> getUrlList() {

        Elements ele = doc.getElementsByClass("userContentWrapper");
        String url;

        List<String> urlList = new ArrayList<String>();
        for (Element anEle : ele)
            try {
                url = "\"https://www.facebook.com"+anEle.select("a[class=_5pcq]").first().attr("href")+"\"";
                if (!url.contains("/events/"))
                    urlList.add(url);
            } catch (NullPointerException e) {
                System.out.println("Cannot find youtube list");
            }
        return urlList;
    }

    public String getJson() {
        String json = "{\"head\":{\"type\":\"URL\"},\"data\":{\"url\":"+getUrlList().toString()+"}}";

        return json;
    }

}
