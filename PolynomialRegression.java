import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.*;

import java.util.*;

public class PolynomialRegression {
  WeightedObservedPoints data;

  public PolynomialRegression(List<Double> x, List<Double> y) {
    data = new WeightedObservedPoints();
    for (int i = 0; i < x.size(); i++) {
      data.add(x.get(i), y.get(i));
    }
  }

  public void play(String temperture, String weather) {
    int degree = 4;

    PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
    double[] coefficients = fitter.fit(data.toList());

    PolynomialFunction regressionFunction = new PolynomialFunction(coefficients);

    int weatherInt = 0;
    switch (weather) {
      case "sunny":
        weatherInt = 0;
        break;
      case "cloudy":
        weatherInt = -5;
        break;
      case "rainy":
        weatherInt = -10;
        break;
    }

    double predictedValue = regressionFunction.value(Integer.parseInt(temperture) + weatherInt);

    System.out.println(String.format("%s℃(%s)で予想される在庫消費数: " + predictedValue, temperture, weather));
  }
}
