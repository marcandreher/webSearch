import java.io.Console;
import java.io.File;
import java.io.IOException;

import Utils.Config;
import Utils.Prefix;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import spark.Spark;

public class run {

	public static Config cfg = null;
	public static Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_31);
	public static MySQL mysql = null;

	public static void main(String[] args) {
		try {
			System.out.println(Prefix.INFO + "search $$$ TERM 1.0/1.1");
			System.out.println(Prefix.INFO + "Loading config.json");
			try {
				cfg = new Config();
			} catch (Exception e) {
				System.out.println(Prefix.ERROR + "Failed to load config.json");
				System.exit(2);
			}

			Spark.port(cfg.getInt("port"));
			Spark.ipAddress(cfg.getString("ip"));
			System.out.println(
					Prefix.INFO + "webSearch for Servers is running on " + cfg.getString("ip") + ":"
							+ cfg.getInt("port"));
			mysql = new MySQL(cfg.getString("mysqlusername"), cfg.getString("mysqlpassword"),
					cfg.getString("mysqldatabase"), cfg.getString("mysqlip"),
					cfg.getInt("mysqlport"));

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

			while (true) {
				String input = System.console().readLine();

				if (input.equalsIgnoreCase("shutdown")) {
					System.out.println(Prefix.WARNING + "Planned shutdown");
					System.exit(1);
				} 
			}
		} catch (Exception e) {
			System.out.println(Prefix.WARNING + "Detected error restarting... " + e.getMessage());
			System.exit(1);
		}
	}
}
