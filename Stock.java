import java.text.SimpleDateFormat;
import java.util.*;

public class Stock {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    StockDao sd = StockDao.getInstance();
    Calendar cal = Calendar.getInstance();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String day = sdf.format(cal.getTime());

    try {
      sd.importWeatherForecast("./input/weather.csv");
      sd.importStockName("./input/stock_name.csv");
      for (int i = Integer.parseInt(day); i >= 20230801; i--) {
        sd.importConsumption(String.format("./input/consumption_%d.csv", i));
      }
    } catch (Exception e) {
      System.out.println("規定ファイルが見つかりません");
    }

    System.out.println("管理したい在庫名を入力: ");
    String stock_name = sc.nextLine();
    int stockId = sd.getStockId(stock_name);
    if (stockId != 0) {
      Map<Integer, Integer> map = sd.getStockConsumptionY(stockId);

      List<Double> tempertureX = new ArrayList<>();
      List<Double> consumptionY = new ArrayList<>();

      for (int key : map.keySet()) {
        tempertureX.add(sd.getDayTempX(key));
        consumptionY.add((double) map.get(key));
      }
      PolynomialRegression pr = new PolynomialRegression(tempertureX, consumptionY);

      System.out.println("明日の予想最高気温を入力: ");
      String temperture = sc.nextLine();
      System.out.println("明日の予想天気を入力: ");
      String weather = sc.nextLine();

      pr.play(temperture, weather);
    } else {
      System.out.println("その商品は扱っていません");
    }

    sd.closeDB();
    sc.close();
  }
}
