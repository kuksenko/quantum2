package quantum2.demo1;


import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@Fork(3)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class CopyMe {


    public static final int K = 1024;
    public static final int M = K * K;


    int[] a;
    int[] b;

    @Param("" + 512 * K)
    int size;

    @Param({"-8", "0"})
    int delta;

    @Setup
    public void setup() {
        size += delta;
        a = new int[size];
        b = new int[size];
    }

    @Benchmark
    public void arraycopy() {
        System.arraycopy(a, 0, b, 0, a.length);
    }

    @Benchmark
    public void reversecopy() {
        for (int i = a.length - 1; i >= 0; i--) {
            b[i] = a[i];
        }
    }

}
