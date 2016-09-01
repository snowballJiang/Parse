/**
 * @author wangwei
 * @version 0.9 2016-01-26
 */

package org.small.facebook;

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

public class FacebookPostAnalysis extends FacebookAnalysis {

    private Document doc;

    public FacebookPostAnalysis(String htmlPath) {

        File input = new File(htmlPath);

        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert doc != null;
    }

    /*public String getAccount() {

        return doc.getElementById("globalContainer").
                getElementById("fbPhotoPageAuthorName").text();

    }*/

    // Facebook的帖子页面中都有一段被<!-- -->注释掉的有用内容，这个函数就是取出这段有用内容
    public String getPostCommentContent() {
        String divContent = "";

        try {
            divContent = doc.select("script~div").select("div[class=hidden_elem]").first().child(0).html();
        } catch (Exception e) {
        	try {
        		divContent = doc.select("code[class=hidden_elem]").first().html();
        	} catch (Exception e2) {
        	}
        }
        if (divContent.indexOf("<!--") == 0)
            divContent = divContent.substring(5, divContent.length() - 3);

        return divContent;
    }

    // 去除Facebook图片链接中的错误标号，原因未知
    public String formatFacebookPicLink(String link) {
        link = link.replace("\\-", "-");
        link = link.replace("-\\", "-");
        return link;
    }

    public String getAccount() {
        try { // tested on page https://www.facebook.com/cnnmoney/posts/10153387015633067
              // and https://www.facebook.com/voiceofamerica/posts/10153517063903074
              // and https://www.facebook.com/cctvnewschina/posts/1136995093007976
            String divContent = getPostCommentContent();
            Document docSlice = Jsoup.parse(divContent);
            return docSlice.select("h5").first().select("a").first().text();
        } catch (Exception e0) {
            try { // is this right?
                return doc.getElementsByClass("userContentWrapper").first().
                        getElementsByClass("fwb").first().text();
            } catch (Exception e) {
                try { // this is correct!
                    return doc.select("#fbPhotoPageAuthorName").first().select("a").first().text();
                } catch (Exception e2) {
                    try {  //貌似可以取消了 correct in some page but wrong in https://www.facebook.com/cnnmoney/posts/10153387015633067
                        String divContent = doc.select("code[class=hidden_elem]").first().html();
                        if (divContent.indexOf("<!--") == 0) {
                            divContent = divContent.substring(5, divContent.length() - 3);
                        }
                        Document docSlice = Jsoup.parse(divContent);
                        return docSlice.select("[class=profileLink]").first().text();
                    } catch (Exception e3) {
                        try {
                            String title = doc.select("title").first().text();
                            return title.substring(0, title.indexOf(" - "));
                        } catch (Exception e4) {
                            return "";
                        }
                    }
                }
            }
        }
    }

    /*public String getPublishDateTime() {
        String time = doc.getElementsByClass("timestampContent").first().parent().attr("title");

        SimpleDateFormat sdf = null;
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (time.contains("上午")) {
            sdf = new SimpleDateFormat("yyyy年MM月dd日上午 hh:mm");
        }else {
            sdf = new SimpleDateFormat("yyyy年MM月dd日下午 hh:mm");
        }
        try {
            Date date = sdf.parse(time);
            sdf2.format(date);
            return sdf2.format(date);
        } catch (Exception e) {
            return "0000-00-00 00:00:00";
        }

    }*/

    // "yyyy-MM-dd HH:mm:ss"
    public String TimeStamp2Date(String timestampString, String formats){
        Long timestamp = Long.parseLong(timestampString)*1000;
        String date = new SimpleDateFormat(formats).format(new Date(timestamp));
        return date;
    }

    /*public String getPublishDateTime() {
        String time = doc.getElementsByClass("timestampContent").first().parent().attr("title");

        SimpleDateFormat sdf = null;
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (time.contains("上午")) {
            sdf = new SimpleDateFormat("yyyy年MM月dd日上午 hh:mm");
        }else {
            sdf = new SimpleDateFormat("yyyy年MM月dd日下午 hh:mm");
        }
        try {
            Date date = sdf.parse(time);
            sdf2.format(date);
            return sdf2.format(date);
        } catch (Exception e) {
            return "0000-00-00 00:00:00";
        }

    }*/

    public String getCrawlDateTime() {
        return util.getCurrentDatetime();
    }

    public String getPublishDateTime() {
    	String res = "";
    	
    	//可能是javascript充分执行之后的
    	//https://www.facebook.com/chinaorgcn/photos/a.757450174281140.1073741825.371171589575669/1328369793855839/?type=3
    	try {
    		res = TimeStamp2Date(doc.select("div[class*=userContentWrapper]").first().select("[data-utime]").first().attr("data-utime"), "yyyy-MM-dd HH:mm:ss");
    	} catch (Exception e0) {}
    	if (res.length() > 0)
    		return res;
    	
        try { //tested
            String strUtime = doc.select(".livetimestamp").first().attr("data-utime");
            return TimeStamp2Date(strUtime, "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            try { // tested
                String divContent = doc.select("code[class=hidden_elem]").first().html();
                if (divContent.indexOf("<!--") == 0)
                    divContent = divContent.substring(5, divContent.length() - 3);
                Document docSlice = Jsoup.parse(divContent);
                String strUtime = docSlice.select("a[class=_5pcq]").first().select("abbr").first().attr("data-utime");
                return TimeStamp2Date(strUtime, "yyyy-MM-dd HH:mm:ss");
            } catch (Exception e2) {
                try { // tested on https://www.facebook.com/abcnews.au/posts/10154782044594988，有3张图片喔
                    String divContent;
                    divContent = doc.select("script~div").select("div[class=hidden_elem]").first().child(0).html();
                    //String divContent = doc.select("code[id=u_0_l]").first().html(); //https://www.facebook.com/abcnews.au/posts/10154776982009988
                    //String divContent = doc.select("code[id=u_0_o]").first().html(); //https://www.facebook.com/abcnews.au/posts/10154782044594988
                    if (divContent.indexOf("<!--") == 0)
                        divContent = divContent.substring(5, divContent.length() - 3);
                    Document docSlice = Jsoup.parse(divContent);
                    String strUtime = docSlice.select("a[class=_5pcq]").first().select("abbr").first().attr("data-utime");
                    return TimeStamp2Date(strUtime, "yyyy-MM-dd HH:mm:ss");
                } catch (Exception e3) {
                    try { //tested on page https://www.facebook.com/OFA/photos/a.203787266478791.1073741831.203330919857759/460809677443214/
                        return TimeStamp2Date(doc.select("a[class=_39g5]").first().select("abbr").first().attr("data-utime"), "yyyy-MM-dd HH:mm:ss");
                    } catch (Exception e4) {
                        return "0000-00-00 00:00:00";
                    }
                }
            }
        }
    }

    public String getContent() {
        String content = "";
        Boolean success = false;
        String divContent;
        Document docSlice;

        // 第一类页面 https://www.facebook.com/abcnews.au/posts/10154776982009988
        try {
            divContent = getPostCommentContent();
            docSlice = Jsoup.parse(divContent);
            Element ele = docSlice.select("div[class*=userContent]").last(); // 不使用first是因为这个选择器会把userContentWrapper页选中
            content = ele.text();
            if (ele.text().length() == 0) { //tested on https://www.facebook.com/cctvnewschina/posts/1138605569513595
                content = ele.nextElementSibling().text();
            }
            success = true;
        } catch (Exception e){ }

        // 去除引号和"\-"，避免非法json
        content = content.replace("\"", "");
        content = content.replace("\\", "");

        // 完成
        if (success)
            return util.utf8TradChToSimpCh(content);

        // 其他
        try {
            content = doc.getElementsByClass("userContentWrapper").first().
                    getElementsByClass("userContent").first().select("p").text();
            content = content.replaceAll("\"", ""); // 去除引号，避免非法json
            return util.utf8TradChToSimpCh(content);
        } catch (Exception e) {
            try {
                divContent = doc.select("code[class=hidden_elem]").first().html();
                if (divContent.indexOf("<!--") == 0)
                    divContent = divContent.substring(5, divContent.length() - 3);
                docSlice = Jsoup.parse(divContent);
                content = docSlice.select("div[class*=_5pco]").first().select("p").first().text();
                content = content.replaceAll("\"", ""); // 去除引号，避免非法json
                return util.utf8TradChToSimpCh(content);
            } catch (Exception e2) {
                try {
                    divContent = doc.select("code[class=hidden_elem]").first().html();
                    if (divContent.indexOf("<!--") == 0)
                        divContent = divContent.substring(5, divContent.length() - 3);
                    docSlice = Jsoup.parse(divContent);
                    content = docSlice.select("div[class*=_5pbx]").first().select("p").first().text();
                    content = content.replaceAll("\"", ""); // 去除引号，避免非法json
                    return util.utf8TradChToSimpCh(content);
                } catch (Exception e3) {
                    try { //tested on https://www.facebook.com/abcnews.au/posts/10154776982009988
                        divContent = doc.select("code[id=u_0_l]").first().html();
                        if (divContent.indexOf("<!--") == 0)
                            divContent = divContent.substring(5, divContent.length() - 3);
                        docSlice = Jsoup.parse(divContent);
                        content = docSlice.select("div[class*=_5pbx]").first().text();
                        content = content.replaceAll("\"", ""); // 去除引号，避免非法json
                        return util.utf8TradChToSimpCh(content);
                    } catch (Exception e4) {
                        try {
                            content = doc.select("#fbPhotoPageCaption").text();
                            content = content.replaceAll("\"", ""); // 去除引号，避免非法json
                            return util.utf8TradChToSimpCh(content);
                        } catch (Exception e5) {
                            return "";
                        }
                    }
                }
            }
        }

    }

    public String getLink() {
        String link;

        try { // 视频页
            link = doc.getElementById("fbxPhotoContentContainer").
                    getElementsByClass("fbPhotosPhotoUfi").first().
                    getElementsByClass("UFIAddComment").first().
                    getElementsByClass("UFICommentAttachmentButtons").first().
                    select("a[href]").attr("href");
            System.out.println("link4 "+link);
            return link;
        } catch (Exception e) {
            //e.printStackTrace();
        }

        try {
            link = doc.select("link[rel=canonical]").first().attr("href");
            return link;
        } catch (Exception e) {
            try {
                link = doc.select("link[rel=alternate]").first().attr("href");
                return link;
            } catch (Exception e2) {
                try {
                    link = doc.getElementsByClass("userContentWrapper").first().
                            getElementsByClass("userContent").first().select("a[role=button]").attr("href");
                    return link;
                } catch (Exception e3) {
                    return "";
                }
            }
        }
    }

    //在regex中"\\"表示一个"\"，在java中一个"\"也要用"\\"表示。这样，前一个"\\"代表regex中的"\"，后一个"\\"代表java中的"\"。所以要想使用replaceAll方法将字符串中的反斜杠("\")替换成空字符串("")，则需要这样写：str.replaceAll("\\\\","");
    public List<String> getPicLink() {
        List<String> eList = new ArrayList<String>();
        String link;
        Boolean success = false;
        String divContent;
        Document docSlice;

        // 第一类页面 https://www.facebook.com/abcnews.au/posts/10154776982009988
        //https://www.facebook.com/abcnews.au/posts/10154772886199988
        // https://www.facebook.com/abcnews.au/photos/a.10150844703979988.524484.72924719987/10154791878964988/?type=3&theater
        //此方法不会误判视频页：https://www.facebook.com/CCTVAmerica/posts/941203472641592
        try {
            divContent = getPostCommentContent();
            if (divContent.length() > 0)
            	docSlice = Jsoup.parse(divContent);
            else
            	docSlice = doc;
            Elements es = new Elements();
            es.clear();
            es = docSlice.select("img[class*=scaledImageFitWidth]"); //使用"div>img"是为了过滤个别误操作，比如https://www.facebook.com/abcnews.au/posts/10154779184669988
            if (es.size() == 0)
            	es = docSlice.select("img[class*=scaledImageFitHeight]");
            //es.remove(0); // 第一个img都不是用户贴图
            for (Element x : es) {
                link = x.attr("src");
                //if (link.indexOf("static-") > 0) //过滤掉，比如https://www.facebook.com/abcnews.au/posts/10154779184669988 此帖子有符合选择器条件的，但它却是一个视频帖子
                   // continue;
                link = formatFacebookPicLink(link);
                eList.add("\"" + link + "\"");
            }
        } catch (Exception e) { }
        if (eList.size() > 0)
            return eList;
        
        return eList;
        /*
        //貌似过时了
        // 第二类页面 
        try {
            link = doc.select("img[class*=fbPhotoImage]").attr("src");
            if (link.length() > 0) {
                link = formatFacebookPicLink(link);
                eList.add("\"" + link + "\"");
            }
        } catch (Exception e) { }
        if (eList.size() > 0)
            return eList;

        // 其他
        try {
            Element ele = doc.getElementsByClass("userContentWrapper").first();

            for (Element anEle : ele.getElementsByClass("mtm").select("a[href*=www.facebook.com]")) {
                link = anEle.attr("href");
                if (link.length() > 0) {
                    link = link.replace("\\-", "-");
                    link = link.replace("-\\", "-"); //
                    eList.add("\"" + link + "\"");System.out.println("c");
                }
            }

            for (Element e : ele.getElementsByClass("userContent").first().select("a[href*=www.facebook.com]")) {
                link = e.attr("href");
                if (link.length() > 0) {
                    link = link.replace("\\-", "-");
                    link = link.replace("-\\", "-"); //
                    eList.add("\"" + link + "\"");System.out.println("d");
                }
            }

            return removeDuplicate(eList);
        } catch (Exception e) {
            try {
                try {
                    divContent = doc.select("code[class=hidden_elem]").first().html();System.out.println("e");
                } catch (Exception ee) {
                    try {
                        divContent = doc.select("code[id=u_0_l]").first().html(); // tested on 
                        System.out.println("f");
                    } catch (Exception ee2) {
                        return eList;
                    }
                }
                if (divContent.indexOf("<!--") == 0)
                    divContent = divContent.substring(5, divContent.length() - 3);
                docSlice = Jsoup.parse(divContent);
                link = docSlice.select("img[class*=scaledImageFitWidth]").first().attr("src");
                if (link.length() > 0) {
                    link = link.replace("\\-", "-"); //别问我为啥要这么做，facebook的前端最诡异了
                    eList.add("\"" + link + "\"");System.out.println("g");
                }
                return eList;
            } catch (Exception e2) {
                try {
                    link = doc.select("img[class*=fbPhotoImage]").attr("src");
                    if (link.length() > 0) {
                        link = link.replace("\\-", "-");
                        eList.add("\"" + link + "\"");System.out.println("h");
                    }
                    return eList;
                } catch (Exception e3) {
                    return eList;
                }
            }
        }*/
    }

    public List<String> getVideoLink() {

        List<String> eList = new ArrayList<String>();
        Document docSlice;
        String divContent;

        try {//tested on https://www.facebook.com/CCTVAmerica/posts/941203472641592,
                 //通过contains就不会误判https://www.facebook.com/abcnews.au/photos/a.10150844703979988.524484.72924719987/10154791878964988/?type=3
            String url;
            divContent = getPostCommentContent();
            if (divContent.length() > 0)
            	docSlice = Jsoup.parse(divContent);
            else
            	docSlice = doc;
            	
            url = docSlice.select("a[rel=theater]").first().attr("href");
            if (url.contains("/videos/") && !url.endsWith("/videos/"))
            	eList.add("\"https://www.facebook.com" + url + "\"");
        } catch (Exception e) {}
        if (eList.size() > 0)
        	return removeDuplicate(eList);
        
        try {
            Element ele = doc.getElementsByClass("userContentWrapper").first();
            Elements eles = ele.getElementsByClass("mtm").first().
                    getElementsByClass("lfloat").select("a[href*=www.facebook.com]");

            for (Element anEle : eles) {
                System.out.println(anEle.attr("href"));
                if ( !anEle.attr("href").endsWith("/videos/"))
                	eList.add("\"" + anEle.attr("href") + "\"");
            }
        } catch (Exception e2) {}
        if (eList.size() > 0)
        	return removeDuplicate(eList);
        
        try {
            divContent = getPostCommentContent();
            if (divContent.length() > 0)
            	docSlice = Jsoup.parse(divContent);
            else
            	docSlice = doc;
            String videoUrl = docSlice.select("a[class=_2za_]").first().attr("href");
            if (videoUrl.indexOf("/") == 0)
                videoUrl = "https://www.facebook.com" + videoUrl;
            videoUrl = "\"" + videoUrl + "\"";
            if (!videoUrl.endsWith("/videos/"))
            	eList.add(videoUrl);
        } catch (Exception e3) {}
        if (eList.size() > 0)
        	return removeDuplicate(eList);
        
        try {
            if (getLink().indexOf("/videos/") > 0 && !getLink().endsWith("/videos/"))
                eList.add("\"" + getLink() + "\"");
        } catch (Exception e4) {}

        return removeDuplicate(eList);
    }

    public boolean isForward() {
        try { // tested on https://www.facebook.com/cctvnewschina/posts/1137297949644357
              // and https://www.facebook.com/cctvnewschina/posts/1137745316266287
              // and https://www.facebook.com/cctvnewschina/posts/1138021726238646
            return doc.html().indexOf("\"isshare\":true,") > 0;
        } catch (Exception e0) {
            try {
                Elements eles = doc.getElementById("globalContainer").
                        select("div:not(#fbPhotoPageFeedback)").select("a[class=profileLink]");

                return eles.size() > 1;
            } catch (Exception e) {
                try {
                    Elements ele = doc.getElementsByClass("userContent").first().//UFICommentContentBlock
                            select("a[class=profileLink]");
                    int count = 0;
                    for (Element anEle : ele) {
                        count++;
                    }
                    return count > 1;
                } catch (Exception e2) {
                    try {
                        String divContent = doc.select("code[class=hidden_elem]").first().html();
                        if (divContent.indexOf("<!--") == 0) {
                            divContent = divContent.substring(5, divContent.length() - 3);
                        }
                        Document docSlice = Jsoup.parse(divContent);
                        //需要改成class*=fcg吗
                        int s = docSlice.select("h5[class=_5pbw]").first().select("span[class=fcg]").first().select("span[class=fcg]").first().select("span[class=fwb]").size();
                        return s > 0;
                    } catch (Exception e3) {
                        return false;
                    }
                }
            }
        }
    }

    public String getLike() {
        try {
            return getLastNumber(doc.getElementsByClass("UFILikeSentenceText").first().text());
        } catch (NullPointerException e) {
            try {
                String likeStr = doc.html().toString();
                likeStr = likeStr.substring(likeStr.indexOf("\"likecount\":"));
                likeStr = likeStr.substring("\"likecount\":".length(), likeStr.indexOf(","));
                return likeStr;
            } catch (Exception e2) {
                return "0";
            }
        }
    }

    public String getComment() {

        try {
            Element ele = doc.getElementsByClass("UFIList").first();
            String[] s = ele.getElementsByClass("UFIPagerCount").last().text().split("/");
            return s[1].replace(",", "").replace(" ", "");
        } catch (Exception e) {
            try {
                String shareStr = doc.html().toString();
                shareStr = shareStr.substring(shareStr.indexOf("\"commentcount\":"));
                shareStr = shareStr.substring("\"commentcount\":".length(), shareStr.indexOf(","));
                return shareStr;
            } catch (Exception e2) {
                return "0";
            }
        }
    }

    // 本身就是分享的帖子，看不到分享数
    public String getShare() {
        try {
            return getFirstNumber(doc.getElementsByClass("UFIShareLink").first().text());
        } catch (NullPointerException e) {
            try {
                String shareStr = doc.html().toString();
                shareStr = shareStr.substring(shareStr.indexOf("\"sharecount\":"));
                shareStr = shareStr.substring("\"sharecount\":".length(), shareStr.indexOf(","));
                return shareStr;
            } catch (Exception e2) {
                return "0";
            }
        }
    }

}
