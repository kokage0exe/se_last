import java.sql.*;
import java.io.*;

public class StockDao {
	String dbName;
	private static final String dayTableName = "DAY";
	private static final String stockTableName = "STOCK";
	private static final String consumptionTableName = "CONSUMPTION";
	Connection conn = null;
	Statement stmt = null;
	
	public StockDao(String dbName) {
		this.dbName = dbName;
		connectionDatabase();
		createTable();
	}
	
	private static StockDao manager = new StockDao("FOOD_STOCK");

	public static StockDao getInstance() {
		return manager;
	}

	public void connectionDatabase() {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);

			System.out.println("接続成功");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("接続失敗");
		}
	}

	public void createTable() {
		try {
			stmt = conn.createStatement();

			stmt.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %s (DAY_ID INTEGER PRIMARY KEY, DAY_TEMPERTURE INTEGER, DAY_WEATHER STRING)", dayTableName));

			stmt.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %s (STOCK_ID INTEGER PRIMARY KEY, STOCK_NAME STRING)", stockTableName));

			stmt.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS %s (DAY_ID INTEGER, STOCK_ID INTEGER, STOCK_CONSUMPTION INTEGER, STOCK_LACK INTEGER)", consumptionTableName));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void importWeatherForecast(String filePath) {
		try {
			Statement stmt = conn.createStatement();
			BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"));
			String line = bf.readLine();
			
			while(line != null){
				String[] day = line.split(" ");
				stmt.executeUpdate(String.format("INSERT INTO DAY VALUES(%d, %d, %s)", Integer.parseInt(day[0]), Integer.parseInt(day[1]), day[2]));
			}
			System.out.println("気象データを読み込みました");
		} catch (FileNotFoundException e) {
			System.out.println("気象データファイルを読み込めませんでした");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createStock(String stockName) {
		int stockId = 0;
		try{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT COUNT(*) AS count FROM STOCK WHERE STOCK_NAME = '%s'", stockName));
			rs = stmt.executeQuery("SELECT max(rowid) FROM STOCK");
			
			if (rs.next()) {
				int count = rs.getInt("count");
				if (count > 0) {
					System.out.println("その商品名は既に使われています");
				}
			}
			
			while (rs.next()) {
				stockId = rs.getInt(1) + 1;
			}
			stmt.executeUpdate(String.format("INSERT INTO STOCK VALUES(%d, '%s')", stockId, stockName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void importConsumption(String filePath) {
		try {
			Statement stmt = conn.createStatement();
			BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"));
			String line = bf.readLine();
			
			while(line != null){
				String[] data = line.split(" ");
				stmt.executeUpdate(String.format("INSERT INTO CONSUMPTION VALUES(%d, %d, %s)", Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3])));
			}
			System.out.println("在庫データを読み込みました");
		} catch (FileNotFoundException e) {
			System.out.println("在庫データファイルを読み込めませんでした");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeDB() {
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
