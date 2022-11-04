import java.io.StringWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Classes.SearchItem;
import Classes.Wiki;
import Jwiki.Jwiki;
import Utils.Prefix;
import freemarker.template.Template;
import spark.Request;
import spark.Response;
import spark.Route;

public class Search implements Route {

    private Map<String, Object> freeMap = null;

    public Object handle(Request req, Response res) {
        freeMap = new HashMap<String, Object>();
        int howManyPages = 0;
        int allQueries = 0;
        int page = 1;
        try {
            Jwiki jwiki = new Jwiki(req.queryParams("q"));
            Wiki wiki = new Wiki();
            wiki.setTitle(jwiki.getDisplayTitle());
            wiki.setImage(jwiki.getImageURL());
            wiki.setText(jwiki.getExtractText());
            freeMap.put("wiki", wiki);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (req.queryParams("page") != null) {
            page = Integer.parseInt(req.queryParams("page"));
        }
        freeMap.put("page", page);
        try {

            if (req.queryParams("q") != null) {
                freeMap.put("query", req.queryParams("q"));

                ResultSet countRs = MySQL.Query("SELECT * FROM `sites` WHERE `title` LIKE ?",
                        "%" + req.queryParams("q") + "%");

                while (countRs.next()) {
                    howManyPages++;
                    allQueries++;
                }
                if (howManyPages < 10 && howManyPages > 0) {
                    howManyPages = 1;
                } else {
                    howManyPages = Math.round(howManyPages / 10);
                }

                freeMap.put("allQueries", allQueries);
                ArrayList<SearchItem> items = new ArrayList<SearchItem>();
                ResultSet searchRs = null;
                if (page != 1) {
                    searchRs = MySQL.Query(
                            "SELECT * FROM `sites` WHERE `title` LIKE ? LIMIT 10 OFFSET " + page * 10 + " ",
                            "%" + req.queryParams("q") + "%");
                } else {
                    searchRs = MySQL.Query("SELECT * FROM `sites` WHERE `title` LIKE ? LIMIT 10",
                            "%" + req.queryParams("q") + "%");
                }

                while (searchRs.next()) {
                    SearchItem item = new SearchItem();
                    item.setDescription(searchRs.getString("description"));
                    item.setDomain(searchRs.getString("domain"));
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
        freeMap.put("pages", howManyPages);

        System.out.println(Prefix.INFO + req.requestMethod() + ": \"" + req.pathInfo() + "\"");

        try {
            Template templateFree = App.freemarkerCfg.getTemplate("searchNew.html");
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
