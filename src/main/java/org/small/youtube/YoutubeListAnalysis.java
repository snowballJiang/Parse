/**
 * @author wangwei
 * @version 0.9 2016-01-26
 */

package org.small.youtube;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YoutubeListAnalysis {

    Document doc = null;

    public YoutubeListAnalysis(String htmlPath) {

        File input = new File(htmlPath);

        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getUrlList() {

        Elements ele = doc.getElementById("browse-items-primary").
                getElementsByClass("yt-lockup-content");

        List<String> urlList = new ArrayList<String>();
        for (Element anEle : ele) {
            urlList.add("\"https://www.youtube.com" + anEle.select("a[href]").first().attr("href")+"\"");
        }
        return urlList;
    }

    public String getJson() {
        String json = "{\"head\":{\"type\":\"URL\"},\"data\":{\"url\":" + getUrlList().toString()+"}}";

        return json;
    }

}
