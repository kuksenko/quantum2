package quantum2.demo2;

import quantum2.Utils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@Fork(3)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class DataSizeRightWay {

    byte[][] data = new byte[256][];

    @Setup
    public void setup() {
        Random rnd = new Random();
        Arrays.setAll(data, i -> new byte[512 * 1024 + rnd.nextInt(64 * 1024)]);
        System.out.println("  4K: "+ Utils.countStrideDistro(data, 4*1024));
        System.out.println(" 32K: "+ Utils.countStrideDistro(data, 32*1024));
        System.out.println("256K: "+ Utils.countStrideDistro(data, 256*1024));
        System.out.println("1M: "+ Utils.countStrideDistro(data, 1024*1024));
    }

    @Benchmark
    public int dataSize() {
        int s = 0;
        for (byte[] a : data) {
            s += a.length;
        }
        return s;
    }

}
