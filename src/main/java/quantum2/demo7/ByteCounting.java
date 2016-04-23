package quantum2.demo7;

import quantum2.Utils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(3)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class ByteCounting {

    public static final int K = 1024;
    static final int SIZE = 16 * K;

    byte[] source;

    @Param({"0", "20", "50", "80", "100"})
    int range;

    @Setup(Level.Trial)
    public void setup() {
        Random rnd = new Random();
        source = new byte[SIZE];
        rnd.nextBytes(source);
        Arrays.fill(source, 0, (SIZE * range) / 100, (byte)42);
        Utils.shuffle(source, rnd);
    }

    @Benchmark
    public int[] count1() {
        int[] table = new int[256];
        for (byte v : source) {
            table[v & 0xFF]++;
        }
        return table;
    }

    @Benchmark
    public int[] count2() {
        int[] table0 = new int[256];
        int[] table1 = new int[256];
        for (int i = 0; i < source.length; ) {
            table0[source[i++] & 0xFF]++;
            table1[source[i++] & 0xFF]++;
        }
        for (int i = 0; i < 256; i++) {
            table0[i] += table1[i];
        }
        return table0;
    }

    @Benchmark
    public int[] count4() {
        int[] table0 = new int[256];
        int[] table1 = new int[256];
        int[] table2 = new int[256];
        int[] table3 = new int[256];
        for (int i = 0; i < source.length; ) {
            table0[source[i++] & 0xFF]++;
            table1[source[i++] & 0xFF]++;
            table2[source[i++] & 0xFF]++;
            table3[source[i++] & 0xFF]++;
        }
        for (int i = 0; i < 256; i++) {
            table0[i] += table1[i] + table2[i] + table3[i];
        }
        return table0;
    }

}
