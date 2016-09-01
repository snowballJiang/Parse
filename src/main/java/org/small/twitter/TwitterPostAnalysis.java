/**
 * @author wangwei
 * @version 0.9 2016-01-26
 */

package org.small.twitter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.small.util.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TwitterPostAnalysis {

    private Document doc;

    public TwitterPostAnalysis(String htmlPath) {
        File input = new File(htmlPath);

        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert doc != null;
    }

    public static String getLastNumber(String src) {

        src = src.replace(",", "");
        List<String> numList = new ArrayList<String>();
        for (String aNum : src.replaceAll("[^0-9]", ",").split(",")) {
            if (aNum.length() > 0)
                numList.add(aNum);
        }
        if (numList.size() > 0) {
            return numList.get(numList.size() - 1);
        } else {
            return "0";
        }
    }

    public String getAccount() {
        Element ele = doc.getElementsByClass("permalink-tweet-container").first().
                getElementsByClass("fullname").first();

        try {
            return ele.html().replace(ele.select(".Icon").outerHtml(),"").trim();
        } catch (Exception e1) {
            return ele.html().trim();
        }
    }
    /* my method
    * try {
                return doc.select("a[class*=ProfileAvatar-container]").first().attr("title");
            } catch (Exception e) {
                return "";
            }*/

    /*public String getPublishDateTime() {
        String time = doc.getElementsByClass("permalink-tweet-container").first().
                getElementsByClass("tweet-timestamp").attr("title");

        SimpleDateFormat sdf = null;
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (time.contains("上午")) {
            sdf = new SimpleDateFormat("上午hh:mm - yyyy年MM月dd日");
        } else {
            sdf = new SimpleDateFormat("下午hh:mm - yyyy年MM月dd日");
        }
        try {
            Date date = sdf.parse(time);
            sdf2.format(date);
            return sdf2.format(date);
        } catch (Exception e) {
            return "0000-00-00 00:00:00";
        }
    }*/

    public String TimeStamp2Date(String timestampString, String formats){
        Long timestamp = Long.parseLong(timestampString)*1000;
        String date = new SimpleDateFormat(formats).format(new Date(timestamp));
        return date;
    }

    public String getPublishDateTime() {
        try {
            return TimeStamp2Date(doc.select("span[class*=_timestamp]").first().attr("data-time"), "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            return "0000-00-00 00:00:00";
        }
    }

    public String getCrawlDateTime() {
        return util.getCurrentDatetime();
    }

    public String getContent() {
        Element element = doc.getElementsByClass("permalink-tweet-container").first().
                getElementsByClass("TweetTextSize").first();

        String content = element.text().replace(element.select("a.u-hidden").text(), "");
        content = content.replaceAll("\"", ""); // 去除引号，避免非法json

        return util.utf8TradChToSimpCh(content);
    }

    public String getLink() {
        try {
            return doc.select("link[rel=\"canonical\"]").first().attr("href");
        } catch (Exception e) {
            return "";
        }
        /*Element ele = doc.getElementById("global-actions").getElementsByClass("dm-nav").first();
        try {
            return ele.select("a[href]").first().attr("href");
        } catch (Exception e) {
            return "";
        }*/
    }

    public List<String> getPicLink() {
        List<String> eList = new ArrayList<String>();

        // page type 1
        try {
            Element ele = doc.select(".js-media-container").first().select(".js-macaw-cards-iframe-container").first();
            if (ele.attr("data-card-name").equals("summary_large_image")) { // It's a image.
                String iframeUrl = ele.attr("data-src");
                iframeUrl = "https://twitter.com" + iframeUrl;
                System.out.println("iframeUrl:" + iframeUrl);
                Document docIframe = Jsoup.connect(iframeUrl).get();
                System.out.println("iframeDoc:" + (docIframe.hasText() ? "yes" : "no"));
                String picUrl = docIframe.select(".tcu-imageWrapper").first().select(".u-block").first().attr("src");
                System.out.println("picUrl:" + picUrl);
                eList.add(picUrl);
            }
        } catch (Exception e) {
        }
        if (eList.size() > 0)
        	return eList;

        // page type 2
        try {
            Elements ele = doc.getElementsByClass("permalink-tweet-container").first().getElementsByClass("AdaptiveMedia-photoContainer");
            for (Element anE : ele) {
                eList.add(anE.attr("data-image-url"));
            }
        } catch (Exception e) {
        }

        return eList;
    }

    public List<String> getVideoLink() {
        List<String> eList = new ArrayList<String>();
        System.out.println("twitter start parse video:");
        
        //https://twitter.com/WSJ/status/704786353473634304 GIF视频
        try {
        	Elements eles = doc.select("div[class=AdaptiveMedia-videoContainer]");
    		String url = eles.select("iframe").first().attr("src");
    		if (url.contains("?embed_source")) {
    			url = url.substring(0, url.indexOf("?embed_source"));
    		}
    		eList.add(url);
        } catch(Exception e0) {}
        if (eList.size() > 0)
        	return eList;
        
        // page type 1
        try {
            Element ele = doc.select(".js-media-container").first().select(".js-macaw-cards-iframe-container").first();
            if (ele.attr("data-card-name").equals("player")) { // It's a video.
                String iframeUrl = ele.attr("data-src");
                iframeUrl = "https://twitter.com" + iframeUrl;
                System.out.println("iframeUrl:" + iframeUrl);
                Document docIframe = Jsoup.connect(iframeUrl).get();
                System.out.println("iframeDoc:" + (docIframe.hasText() ? "yes" : "no"));
                String videoUrl = docIframe.select("#ExternalIframeContainer").first().select("iframe").first().attr("src");
                System.out.println("videoUrl:" + videoUrl);
                eList.add(videoUrl);
            }
        } catch (Exception e) {
        }

        // page type 2
        try {
            Element ele = doc.select(".AdaptiveMedia-videoContainer").first().select(".js-macaw-cards-iframe-container").first();
            if (ele.attr("data-card-name").equals("__entity_video")) { // It's a video.
                String iframeUrl = ele.attr("data-src");
                iframeUrl = "https://twitter.com" + iframeUrl;
                System.out.println("iframeUrl:" + iframeUrl);
                eList.add(iframeUrl); //还不彻底
            }
        } catch (Exception e) {
        }

        // page type 3
        try {
            Elements es = null;
            Element tweet = doc.getElementsByClass("permalink-tweet-container").first();
            es = tweet.getElementsByClass("AdaptiveMedia-videoContainer").first().getElementsByTag("video");
            for (Element anE : es) {
                eList.add("" + anE.attr("src") + "");
            }
        } catch (NullPointerException e) {
        }

        return eList;
    }

    public boolean isForward() {

        Element ele = doc.getElementsByClass("permalink-tweet-container").first();
        return !ele.getElementsByClass("QuoteTweet").isEmpty();

    }

    public String getComment() {
        Elements elements = doc.getElementsByClass("replies-to").first().
                getElementsByClass("TweetTextSize");

        return String.valueOf(elements.size());
    }

    public String getShare() {
        try {
            return doc.getElementsByClass("request-retweeted-popup").first().
                    getElementsByTag("strong").text().replace(",", "");
        } catch (NullPointerException e) {
            return "0";
        }
    }

    public String getLike() {
        try {
            return doc.getElementsByClass("request-favorited-popup").first().
                    getElementsByTag("strong").text().replace(",", "");
        } catch (NullPointerException e) {
            return "0";
        }
    }

    public String getJson() {

        if (getLink().length() == 0) {
        	System.out.println("Null link is not allowed.");
            return "";
        }

        if (getPublishDateTime().equals("0000-00-00 00:00:00")) {
        	System.out.println("Null publish datetime is not allowed.");
            return "";
        }

        List<String> picList = getPicLink();
        List<String> vdoList = getVideoLink();

        return "{" +
                "\"Twitter\":{" +
                "\"Account\":\"" + getAccount() +
                "\",\"PublishDateTime\":\"" + getPublishDateTime() +
                "\",\"CrawlDateTime\":\"" + getCrawlDateTime() +
                "\",\"Content\":\"" + getContent() +
                "\",\"Link\":\"" + getLink() + "\"," +
                "\"PicLink\":[" + ((picList.size() == 0) ? "" : ("\"" + picList.get(0) + "\"")) + "]," +
                "\"VideoLink\":[" + ((vdoList.size() == 0) ? "" : ("\"" + vdoList.get(0) + "\"")) + "]," +
                "\"IsForward\":" + isForward() +
                ",\"Comment\":" + getComment() +
                ",\"Share\":" + getShare() +
                ",\"Like\":" + getLike() +
                "}}";
    }




    /*public Elements getReplyNum(File input) {

        Document doc = null;
        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Elements replys = doc.getElementsByClass("UFIReplySocialSentenceLinkText");
        return replys;

        *//*for (Element reply : replys) {
            System.out.println( getNumbers(reply.text()));
        }*//*

    }*/

}
