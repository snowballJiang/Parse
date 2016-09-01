/**
 * @author wangwei
 * @version 0.9 2016-01-26
 */

package org.small.twitter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TwitterListAnalysis {

    Document doc = null;

    public TwitterListAnalysis(String htmlPath) {

        File input = new File(htmlPath);

        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getUrlList() {

        List<String> urlList = new ArrayList<String>();
        try {
            Elements eleList = doc.select("div[class=stream-item-header]");
            for (Element ele : eleList) {
                urlList.add("\"https://twitter.com" + ele.select("small").first().select("a").first().attr("href") + "\"");
            }
        } catch (Exception e) {
        }
        return urlList;
    }

    public String getJson() {
        String json = "{\"head\":{\"type\":\"URL\"},\"data\":{\"url\":"+getUrlList().toString()+"}}";

        return json;
    }

}
