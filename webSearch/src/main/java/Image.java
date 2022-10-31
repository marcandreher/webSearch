import java.io.StringWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Utils.Prefix;
import Utils.SearchItem;
import freemarker.template.Template;
import spark.Request;
import spark.Response;
import spark.Route;

public class Image implements Route {

    private Map<String, Object> freeMap = null;

    public Object handle(Request req, Response res) {
        freeMap = new HashMap<String, Object>();
        int howPages = 0;
        int allImg = 0;
        int page = 1;
        if(req.queryParams("page") != null) {
            page = Integer.parseInt(req.queryParams("page"));
        }
        freeMap.put("page", page);
        try {

            if (req.queryParams("q") != null) {
                freeMap.put("query", req.queryParams("q"));
                // Check Pagination
                ResultSet countRs = MySQL.Query("SELECT * FROM `img` WHERE `title` LIKE ?",
                        "%" + req.queryParams("q") + "%");

                while (countRs.next()) {
                    howPages++;
                    allImg++;
                }
                if(howPages < 100 && howPages > 0) {
                    howPages = 1;
                }else{
                    howPages = Math.round(howPages / 100);
                }
                freeMap.put("allQueries", allImg);
                ArrayList<SearchItem> items = new ArrayList<SearchItem>();
                ResultSet searchRs = null;
                if(page != 1) {
                    searchRs = MySQL.Query("SELECT * FROM `img` WHERE `title` LIKE ? LIMIT 100 OFFSET " + page * 100 + " ",
                        "%" + req.queryParams("q") + "%");
                }else{
                    searchRs = MySQL.Query("SELECT * FROM `img` WHERE `title` LIKE ? LIMIT 100",
                        "%" + req.queryParams("q") + "%");
                }
                
                while (searchRs.next()) {
                    SearchItem item = new SearchItem();
                    item.setDescription(searchRs.getString("src"));
                    item.setTitle(searchRs.getString("title"));
                    items.add(item);
                    freeMap.put("no", false);
                }
                if (!items.isEmpty()) {
                    freeMap.put("eg", items);
                } else {
                    freeMap.put("eg", null);
                }
            } else {
                freeMap.put("eg", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        freeMap.put("pages", howPages);

        System.out.println(Prefix.INFO + req.requestMethod() + ": \"" + req.pathInfo() + "\"");

        try {
            Template templateFree = run.freemarkerCfg.getTemplate("img.html");
            Writer out = new StringWriter();
            templateFree.process(freeMap, out);
            ;
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
