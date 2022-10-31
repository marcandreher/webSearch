

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Utils.Prefix;

public final class MySQL {
	private static Connection con = null;

	public MySQL(String Username, String Password, String Database, String Hostname, int Port) {
		connect(Username, Password, Database, Hostname, Port);
	}

	public static Connection getCon() {
		return con;
	}

	private void connect(String Username, String Password, String Database, String Hostname, int Port) {
		String conStr = "jdbc:mysql://" + Hostname + ":" + Port + "/" + Database + "?user=" + Username + "&password="
				+ Password + "&serverTimezone=UTC";
		try {
			if (con != null && !con.isClosed())
				throw new Exception("Connection already established!");
			con = DriverManager.getConnection(conStr);
			ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor();
			thread.scheduleAtFixedRate(new Runnable() {
				public void run() {
					try {
						MySQL.this.antiTimeoutLoop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 0L, 1L, TimeUnit.SECONDS);
		} catch (Exception ex) {
			System.out.println(Prefix.MYSQL + ex.getMessage());
		}
	}

	public static ResultSet Query(String sql, String... args) {
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			for (int i = 0; i < args.length; i++)
				stmt.setString(i + 1, args[i]);
			if (Boolean.parseBoolean(run.cfg.getString("debug")) == true)
				System.out.println(Prefix.MYSQL + stmt.toString());
			return stmt.executeQuery();
		} catch (Exception ex) {
			System.out.println(Prefix.MYSQL + ex.getMessage());
			return null;
		}
	}

	public static boolean Exec(String sql, String... args) {
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			for (int i = 0; i < args.length; i++)
				stmt.setString(i + 1, args[i]);
			if (Boolean.parseBoolean(run.cfg.getString("debug")) == true)
				System.out.println(Prefix.MYSQL + stmt.toString());
			return stmt.execute();
		} catch (Exception ex) {
			System.out.println(Prefix.MYSQL + ex.getMessage());
			return false;
		}
	}

	private void antiTimeoutLoop() throws SQLException, InterruptedException {
		while (!con.isClosed()) {
			PreparedStatement stmt = con.prepareStatement("SELECT 1+1");
			stmt.execute();
			Thread.sleep(3000L);
		}
	}
}