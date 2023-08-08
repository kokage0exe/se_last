import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class PolynomialRegressionExample {
    public static void main(String[] args) {
        // データの設定
        double[] xData = {1, 2, 3, 4, 5};
        double[] yData = {2.1, 3.8, 6.1, 8.9, 12.5};

        // 重み付きの観測点を作成
        WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < xData.length; i++) {
            obs.add(xData[i], yData[i]);
        }

        // 多項式回帰の次数を指定
        int degree = 2;

        // 多項式カーブフィッターを作成
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);

        // 回帰パラメータを推定
        double[] coefficients = fitter.fit(obs.toList());

        // 推定された係数を表示
        System.out.println("Estimated coefficients:");
        for (int i = 0; i < coefficients.length; i++) {
            System.out.println("x^" + i + ": " + coefficients[i]);
        }
    }
}
