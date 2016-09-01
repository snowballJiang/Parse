/**
 * @author wangwei
 * @version 0.9 2016-01-26
 */

package org.small.youtube;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.small.util.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class YoutubePostAnalysis {

    private Document doc;

    public YoutubePostAnalysis(String htmlPath) {

        File input = new File(htmlPath);

        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert doc != null;
    }


    public String getAccount() {

        try {
            return doc.getElementsByClass("yt-user-info").first().text();
        } catch (Exception e) {
            return "";
        }
    }

    public Integer getSubscription() {
        try {
            return Integer.parseInt(doc.getElementsByClass("yt-subscriber-count").first().text().replace(",", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public String getCrawlDateTime() {
        return util.getCurrentDatetime();
    }

    public String getPublishDateTime() {
        try {
            return doc.select("[itemprop=datePublished]").first().attr("content") + " 00:00:00";
        } catch (Exception e) {
            return "0000-00-00 00:00:00";
        }
        /*String time = doc.getElementById("watch-uploader-info").text();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日发布", Locale.ENGLISH);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(time);
            sdf2.format(date);
            return sdf2.format(date);
        } catch (Exception e) {
            return "0000-00-00 00:00:00";
        }*/
    }

    public String getContent() {
        try {
            String content = doc.select("title").first().text().replace(" - YouTube", "");
            content = content.replaceAll("\"", ""); // 去除引号，避免非法json
            return util.utf8TradChToSimpCh(content);
        } catch (Exception e) {
            return "";
        }
        /*
        Element e = doc.getElementById("eow-description");
        try {
            return e.text().replace(e.child(0).text(), "") + e.select("a[href]").attr("href");
        } catch (Exception e1) {
            return e.text();
        }
        */
    }

    public String getLink() {
        try {
            return doc.select("link[rel=\"canonical\"]").first().attr("href");
        } catch (Exception e) {
            return "";
        }
        /* embbed link*/
        /*
        Element ele = doc.getElementById("watch7-content");

        try {
            return ele.select("link[itemprop=embedURL]").first().attr("href");
        } catch (Exception e) {
            return "";
        }*/
    }

    public Integer getPlayTimes() {
        /*try {
            return doc.getElementById("watch7-views-info").getElementsByClass("watch-view-count").
                    first().text().replace(",", "");
        } catch (Exception e) {
            return "0";
        }*/
        try {
            String times = doc.select("[itemprop=interactionCount]").first().attr("content");
            System.out.println(times);
            return Integer.parseInt(times);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getComment() {

        try {
            return getLastNumber(getNumber(doc.getElementById("watch-discussion").
                    getElementsByClass("all-comments").first().text())).replace(",", "");
        } catch (Exception e) {
            return "0";
        }
    }

    public String getLike() {
        try {
            return doc.getElementsByClass("like-button-renderer-like-button-unclicked").
                    first().text().replace(",", "");
        } catch (Exception e) {
            return "0";
        }
    }


    public String getLastNumber(List<String> src) {

        if (src.size() > 0) {
            return src.get(src.size() - 1);
        } else {
            return "0";
        }
    }

    public List<String> getNumber(String src) {
        src = src.replace(",", "");
        List<String> numList = new ArrayList<String>();
        for (String aNum : src.replaceAll("[^0-9]", ",").split(",")) {
            if (aNum.length() > 0)
                numList.add(aNum);
        }
        return numList;
    }

    public String getJson() {

        if (getLink().length() == 0) {
            return "";
        }

        if (getPublishDateTime().equals("0000-00-00 00:00:00")) {
            return "";
        }

        return "{" +
                "\"Youtube\":{" +
                "\"Account\":\"" + getAccount() + "\"," +
                "\"Subscription\":" + getSubscription() + "," +
                "\"PublishDateTime\":\"" + getPublishDateTime() + "\"," +
                "\"CrawlDateTime\":\"" + getCrawlDateTime() + "\"," +
                "\"Content\":\"" + getContent() + "\"," +
                "\"Link\":\"" + getLink() + "\"," +
                "\"PlayTimes\":" + getPlayTimes() + "," +
                "\"Comment\":" + getComment() + "," +
                "\"Like\":" + getLike() +
                "}}";
    }

}
