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
  
  public void play(){
    // 多項式回帰の次数を指定
    int degree = 10;
    
    // 多項式回帰の実行
    PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
    double[] coefficients = fitter.fit(data.toList());

    // 回帰多項式を作成
    PolynomialFunction regressionFunction = new PolynomialFunction(coefficients);

    // x = 5 のときの予測値を計算
    double predictedValue = regressionFunction.value(5);

    System.out.println("Regression coefficients:");
    for (int i = 0; i < coefficients.length; i++) {
      System.out.println("Coefficient " + i + ": " + coefficients[i]);
    }
    System.out.println("Predicted value at x = 5: " + predictedValue);
    
  }
}
