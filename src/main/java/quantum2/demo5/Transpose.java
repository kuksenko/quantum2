package quantum2.demo5;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@Fork(3)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class Transpose {

    // transpose matrix

    double[][] matrix;

    @Param({"253", "254", "255", "256"})
    int size;

    @Setup
    public void setup() {
        matrix = new double[size][size];
        Random r = new Random();
        for (int i = 0; i < matrix.length; ++i) {
            double[] mm = matrix[i];
            for (int j = 0; j < mm.length; j++) {
                mm[j] = r.nextDouble();
            }
        }
    }

    @Benchmark
    public void transpose() {
        for (int i = 1; i < size; i++) {
            for (int j = 0; j < i; j++) {
                double tmp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = tmp;
            }
        }
    }


}
