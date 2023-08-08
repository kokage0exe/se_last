import java.text.SimpleDateFormat;
import java.util.*;

public class Stock {
  public static void main(String[] args) {
    StockDao sd = StockDao.getInstance();
    Calendar cal = Calendar.getInstance();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String day = sdf.format(cal.getTime());

    try {
      sd.importWeatherForecast(String.format("./input/weather_%s.csv", day));
      sd.createStock("apple");
      sd.createStock("banana");
      sd.importConsumption(String.format("./input/consumption_%s.csv", day));
      
      sd.closeDB();
    } catch (Exception e) {
      System.out.println("規定ファイルが見つかりません");
    }
  }
}
