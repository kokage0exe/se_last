import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.*;

import java.util.*;

public class PolynomialRegression {
  WeightedObservedPoints data;
  
  public PolynomialRegression(List<Double> x, List<Double> y){
    data = new WeightedObservedPoints();
    for(int i = 0; i < x.size(); i++){
      data.add(x.get(i), y.get(i));
    }
  }
  
  public void play(String temperture, String weather){
    int degree = 8;
    
    PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
    double[] coefficients = fitter.fit(data.toList());

    PolynomialFunction regressionFunction = new PolynomialFunction(coefficients);

    int weather_to_int = 0;
    switch(weather){
      case "sunny":
        weather_to_int = 0;
        break;
      case "cloudy":
        weather_to_int = -5;
        break;
      case "rainy":
        weather_to_int = -10;
        break;
    }

    double predictedValue = regressionFunction.value(Integer.parseInt(temperture) + weather_to_int);

    System.out.println(String.format("%s℃で予想される在庫消費数: " + predictedValue, temperture));
  }
}
