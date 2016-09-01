/**
 * @author wangwei
 * @version 0.9 2016-01-26
 */

package org.small.facebook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FacebookAnalysis {

    public static String getFirstNumber(String content) {
        content = content.replace(",", "");
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public static String getLastNumber(String src) {

        src = src.replace(",", "");
        List<String> numList = new ArrayList<String>();
        for (String aNum : src.replaceAll("[^0-9]", ",").split(",")) {
            if (aNum.length() > 0)
                numList.add(aNum);
        }
        return numList.get(numList.size() - 1);
    }

    public static List<String> removeDuplicate(List<String> list) {
    	HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
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

        return "{" +
                "\"Facebook\":{" +
                "\"Account\":\"" + getAccount() +
                "\",\"PublishDateTime\":\"" + getPublishDateTime() +
                "\",\"CrawlDateTime\":\"" + getCrawlDateTime() +
                "\",\"Content\":\"" + getContent() +
                "\",\"Link\":\"" + getLink() +
                "\",\"PicLink\":" + getPicLink() +
                ",\"VideoLink\":" + getVideoLink() +
                ",\"IsForward\":" + isForward() +
                ",\"Like\":" + getLike() +
                ",\"Comment\":" + getComment() +
                ",\"Share\":" + getShare() +
                "}}";
    }

    public abstract String getAccount();

    public abstract String getPublishDateTime();

    public abstract String getCrawlDateTime();

    public abstract String getContent();

    public abstract String getLink();

    public abstract List<String> getPicLink();

    public abstract List<String> getVideoLink();

    public abstract boolean isForward();

    public abstract String getLike();

    public abstract String getComment();

    public abstract String getShare();


}
