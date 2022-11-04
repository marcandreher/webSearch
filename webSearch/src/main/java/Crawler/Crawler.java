package Crawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import Utils.MySQL;
import Utils.Prefix;

public class Crawler extends Thread{
    private ArrayList<String> list = new ArrayList<String>();
    private String file = "";
    private int delay = 0;

    //getter setter delay
    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void addAgent(String url) {
        list.add(url);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public void run() {
        
        try {

      
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            if(delay > 0) {
                System.out.println(Prefix.INFO + "Delaying " + delay + " domain(s)...");
                delay--;
                continue;
            }
            String[] slicedCrawl = line.split("\"");
            String domain = slicedCrawl[3];
            double score = Double.parseDouble(slicedCrawl[5]);

            ResultSet testIfValid = MySQL.Query("SELECT * FROM `sites` WHERE `domain` = ?", domain);
            try {
                if (testIfValid.next()) {

                } else {
                    try {
                        Document doc = Jsoup.connect("https://" + domain).userAgent(userAgent())
                                .timeout(10000)
                                .followRedirects(true)
                                .get();

                        String title = "";
                        String description = "";

                        Element descriptionMetaTag = doc.select("meta[name=description]").first();
                        description = descriptionMetaTag.attr("content");
                        title = doc.title();

                        for (Element el : doc.select("a")) {
                            if (el.attr("abs:href").contains("youtube.com")) {
                                String videoId = getVideoId(el.attr("abs:href"));
                                String link = el.attr("abs:href");
                                if(isValidYoutubeVideoId(videoId) ){
                                    MySQL.Exec(
                                        "INSERT INTO `youtube`(`link`, `title`, `video_id`) VALUES (?,?,?)",
                                        link, title, videoId);
                                        System.out.println(Prefix.INFO + "Crawled YouTube Video " + videoId + " / " + link);
                                }
                            }
                        }

                        MySQL.Exec(
                                "INSERT INTO `sites`(`domain`, `score`, `ver`, `description`, `title`) VALUES (?,?,?,?,?)",
                                domain, score + "", "1.0", description, title);

                        System.out.println(Prefix.INFO + "Crawled " + title + " / " + description + " / " + score);

                    } catch (Exception e) {
                        System.out.println(Prefix.ERROR + "Failed to crawl " + domain + " / " + score);
                    }
                }
            } catch (SQLException e) {

                e.printStackTrace();
            }
          
        }
        br.close();
    }catch(Exception e) {
        e.printStackTrace();
    }
    
    }

    public static boolean isValidYoutubeVideoId(String videoId) {
        if (videoId == null || videoId.length() != 11) {
            return false;
        }
        try {
            Long.parseLong(videoId, 16);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String getVideoId(String url) {
        String video_id = null;
        if (url != null && url.trim().length() > 0 && url.startsWith("http")) {
            String expression = "^.*((youtu.be\\/)|(v\\/)|(\\/u\\/\\w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(expression,
                    java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }

    public String userAgent() {
        int random = (int) (Math.random() * list.size());
        return list.get(random);
    }

}
