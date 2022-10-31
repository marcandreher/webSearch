package Utils;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Config {

	public Object config = null;
	public JSONObject objConfig = null;
	private File configFile = null;

	// I don't know why line 38 is unchecked the Type of the Map seems to be
	// incorrect.
	@SuppressWarnings("unchecked")
	public Config() throws Exception {
		configFile = new File("config.json");
		if (!configFile.exists()) {
			JSONObject configObj = new JSONObject();
			Map<String, Object> configMap = new HashMap<String, Object>();

			configMap.put("port", 80);
			configMap.put("ip", "0.0.0.0");
			configMap.put("domain", "localhost");

			configMap.put("mysqlip", "localhost");
			configMap.put("mysqlport", "3306");
			configMap.put("mysqlusername", "root");
			configMap.put("mysqldatabase", "scampi");
			configMap.put("mysqlpassword", "");

			configMap.put("debug", "true");

			configObj.putAll(configMap);
			;

			PrintWriter writer = new PrintWriter("config.json", "UTF-8");
			writer.println(configObj.toJSONString());
			writer.close();

			System.out.println(Prefix.INFO + "You can now edit your config.json");
			System.exit(0);

		}

		JSONParser parser = new JSONParser();
		config = parser.parse(new FileReader("config.json"));
		objConfig = (JSONObject) config;

		System.out.println(Prefix.INFO + "Sucessfully loaded config.json");
	}

	public String getString(String key) {
		return objConfig.get(key).toString();
	}

	public Boolean getBool(String key) {
		return Boolean.parseBoolean(objConfig.get(key).toString());
	}

	public int getInt(String key) {
		return Integer.parseInt(objConfig.get(key).toString());
	}
}