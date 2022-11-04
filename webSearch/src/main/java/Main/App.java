package Main;
import java.io.Console;
import java.io.File;
import java.io.IOException;

import Crawler.Crawler;
import Sites.Image;
import Sites.Search;
import Utils.Config;
import Utils.MySQL;
import Utils.Prefix;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import spark.Spark;

public class App {

	public static Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_31);
	public static MySQL mysql = null;

	public static void main(String[] args) {

		try {
			if(args.length == 0) {
			System.out.println(Prefix.INFO + "search $$$ TERM 1.0/1.1");
			System.out.println(Prefix.INFO + "Loading config.json");
			try {
				new Config();

			} catch (Exception e) {
				System.out.println(Prefix.ERROR + "Failed to load config.json");
				System.exit(2);
			}

			Spark.port(Config.getInt("port"));
			Spark.ipAddress(Config.getString("ip"));
			System.out.println(
					Prefix.INFO + "webSearch for Servers is running on " + Config.getString("ip") + ":"
							+ Config.getInt("port"));
			mysql = new MySQL(Config.getString("mysqlusername"), Config.getString("mysqlpassword"),
					Config.getString("mysqldatabase"), Config.getString("mysqlip"),
					Config.getInt("mysqlport"));

			freemarkerCfg.setDefaultEncoding("UTF-8");
			freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			freemarkerCfg.setLogTemplateExceptions(false);
			freemarkerCfg.setWrapUncheckedExceptions(true);

			File staticFiles = new File("static/");
			if (!staticFiles.exists()) {
				staticFiles.mkdir();
			}

			File templateFiles = new File("templates/");
			if (!templateFiles.exists()) {
				templateFiles.mkdir();
			}

			Spark.externalStaticFileLocation("static/");

			try {
				freemarkerCfg.setDirectoryForTemplateLoading(templateFiles);
			} catch (IOException e) {
				System.out.println(Prefix.ERROR + "Failed to load template folder");
			}

			Spark.get("/", new Search());
			Spark.get("/img", new Image());
		}else{
			System.out.println(Prefix.INFO + "search $$$ TERM 1.0/1.1");
			System.out.println(Prefix.INFO + "Loading config.json");
			try {
				new Config();

			} catch (Exception e) {
				System.out.println(Prefix.ERROR + "Failed to load config.json");
				System.exit(2);
			}
			mysql = new MySQL(Config.getString("mysqlusername"), Config.getString("mysqlpassword"),
					Config.getString("mysqldatabase"), Config.getString("mysqlip"),
					Config.getInt("mysqlport"));
		}

			while (true) {
				String input = System.console().readLine();
				String[] argsCmd = input.split(" ");
				if (argsCmd[0].equalsIgnoreCase("shutdown")) {
					System.out.println(Prefix.WARNING + "Planned shutdown");
					System.exit(1);
				} else if(argsCmd[0].equalsIgnoreCase("crawler")) {
					Crawler crawler = new Crawler();
					crawler.addAgent("Mozilla/5.0 (Windows NT 5.1; rv:11.0) Gecko Firefox/11.0 (via ggpht.com GoogleImageProxy)");;
					crawler.addAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");;
					crawler.addAgent("Mozilla/5.0 (compatible; Yahoo! Slurp; http://help.yahoo.com/help/us/ysearch/slurp)");;
					crawler.addAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");;
					if(argsCmd.length == 1) {
						System.out.println(Prefix.ERROR + "crawler <file.csv> <delay>");
					}else if(argsCmd.length == 3) {
						System.out.println(Prefix.WARNING + "Starting crawling with delay " + argsCmd[2]);
						crawler.setDelay(Integer.parseInt(argsCmd[2]));
						crawler.setFile(argsCmd[1]);
						crawler.start();
					}else if(argsCmd.length == 2){
						System.out.println(Prefix.WARNING + "Starting crawling");
						crawler.setFile(argsCmd[1]);
						crawler.start();
					}

					
					
				} else {
					System.out.println(Prefix.ERROR + "Unknown command");
				}
			}
		} catch (Exception e) {
			System.out.println(Prefix.WARNING + "Detected error restarting... " + e.getMessage().toString());
			System.exit(1);
		}
	}
}
