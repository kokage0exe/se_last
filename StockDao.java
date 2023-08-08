import java.sql.*;
import java.io.*;
import java.util.*;

public class StockDao {
	String dbName;
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

			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS DAY (DAY_ID INTEGER PRIMARY KEY, DAY_TEMPERTURE INTEGER, DAY_WEATHER TEXT)");

			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS STOCK (STOCK_ID INTEGER PRIMARY KEY, STOCK_NAME TEXT)");

			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CONSUMPTION (DAY_ID INTEGER, STOCK_ID INTEGER, STOCK_CONSUMPTION INTEGER, STOCK_LACK INTEGER)");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void importWeatherForecast(String filePath) {
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"));
			String line = bf.readLine();
			
			while(line != null){
				String[] day = line.split(", ");
				PreparedStatement insertStatement = conn.prepareStatement("INSERT OR IGNORE INTO DAY (DAY_ID, DAY_TEMPERTURE, DAY_WEATHER) VALUES (?, ?, ?)");
				insertStatement.setInt(1, Integer.parseInt(day[0]));
				insertStatement.setInt(2, Integer.parseInt(day[1]));
				insertStatement.setString(3, day[2]);
				insertStatement.executeUpdate();

				line = bf.readLine();
			}
			System.out.println("気象データを読み込みました");
		} catch (FileNotFoundException e) {
			System.out.println("気象データファイルを読み込めませんでした");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createStock(String stockName) {
		try {
			PreparedStatement checkStatement = conn.prepareStatement("SELECT STOCK_NAME FROM STOCK WHERE STOCK_NAME = ?");
			checkStatement.setString(1, stockName);
			ResultSet rs = checkStatement.executeQuery();
			
			if (rs.next()) {
				System.out.println("その商品名は既に使われています");
			} else {
				PreparedStatement maxIdStatement = conn.prepareStatement("SELECT MAX(STOCK_ID) FROM STOCK");
				ResultSet maxIdRs = maxIdStatement.executeQuery();
				
				int stockId = 1;
				if (maxIdRs.next()) {
						stockId = maxIdRs.getInt(1) + 1;
				}

				PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO STOCK (STOCK_ID, STOCK_NAME) VALUES (?, ?)");
				insertStatement.setInt(1, stockId);
				insertStatement.setString(2, stockName);
				insertStatement.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void importConsumption(String filePath) {
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"));
			String line = bf.readLine();
			
			while(line != null){
				String[] data = line.split(", ");
				PreparedStatement insertStatement = conn.prepareStatement("INSERT OR IGNORE INTO CONSUMPTION (DAY_ID, STOCK_ID, STOCK_CONSUMPTION, STOCK_LACK) VALUES (?, ?, ?, ?)");
				insertStatement.setInt(1, Integer.parseInt(data[0]));
				insertStatement.setInt(2, Integer.parseInt(data[1]));
				insertStatement.setInt(3, Integer.parseInt(data[2]));
				insertStatement.setInt(4, Integer.parseInt(data[3]));
				insertStatement.executeUpdate();
				
				line = bf.readLine();
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

	public Double getDayTempX(int day){
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			
			String query = "SELECT * FROM DAY WHERE DAY_ID == ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, day);
			ResultSet rs = stmt.executeQuery();
			
			int day_id_ = rs.getInt("DAY_ID");
			int day_temp_ = rs.getInt("DAY_TEMPERTURE");
			String day_weather_ = rs.getString("DAY_WEATHER");
			
			int weather = 0;
			switch(day_weather_){
				case "sunny":
					weather = 0;
					break;
				case "cloudy":
					weather = -5;
					break;
				case "rainy":
					weather = -10;
					break;
			}

			closeDB();
			return (double)(day_temp_ + weather);

		} catch (SQLException e) {
			e.printStackTrace();
			closeDB();
			return null;
		}
	}

	public int getStockId(String stock_name) {
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			
			String query = "SELECT * FROM STOCK WHERE STOCK_NAME == ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, stock_name);
			ResultSet rs = stmt.executeQuery();
			
			int stock_id_ = rs.getInt("STOCK_ID");
			String stock_name_ = rs.getString("STOCK_NAME");

			closeDB();
			return stock_id_;

		} catch (SQLException e) {
			e.printStackTrace();
			closeDB();
			return 0;
		}
	}

	public Map<Integer, Integer> getStockConsumptionY(int stock_id) {
		HashMap<Integer, Integer> map = new HashMap<>();

		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			
			String query = "SELECT * FROM CONSUMPTION WHERE STOCK_ID == ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, stock_id);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				int day_id_ = rs.getInt("DAY_ID");
				int stock_id_ = rs.getInt("STOCK_ID");
				int stock_consumption_ = rs.getInt("STOCK_CONSUMPTION");
				int stock_lack_ = rs.getInt("STOCK_LACK");
	
				map.put(day_id_, stock_consumption_ + stock_lack_);
			}

			closeDB();
			return map;

		} catch (SQLException e) {
			e.printStackTrace();
			closeDB();
			return null;
		}
	}
	
}
