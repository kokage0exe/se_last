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
      sd.importWeatherForecast(String.format("./input/weather.csv", day));
      sd.createStock("apple");
      sd.createStock("banana");
      for(int i = Integer.parseInt(day) - 1; i > 20230801; i--){
        sd.importConsumption(String.format("./input/consumption_%d.csv", i));
      }
    } catch (Exception e) {
      System.out.println("規定ファイルが見つかりません");
    }

    System.out.println("管理したい在庫名を入力: ");
    String stock_name = sc.nextLine();
    int stock_id = sd.getStockId(stock_name);
    Map<Integer, Integer> map = sd.getStockConsumptionY(stock_id);

    List<Double> x_temperture = new ArrayList<>();
    List<Double> y_consumption = new ArrayList<>();

    for(int key : map.keySet()){
      x_temperture.add(sd.getDayTempX(key));
      y_consumption.add((double)map.get(key));
    }
    PolynomialRegression pr = new PolynomialRegression(x_temperture, y_consumption);

    System.out.println("明日の予想最高気温を入力: ");
    String temperture = sc.nextLine();
    System.out.println("明日の予想天気を入力: ");
    String weather = sc.nextLine();

    pr.play(temperture, weather);

    sd.closeDB();
    sc.close();
  }
}
