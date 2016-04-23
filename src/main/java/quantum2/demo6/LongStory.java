package quantum2.demo6;

import quantum2.Utils;
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
import org.openjdk.jol.util.VMSupport;
import sun.misc.Unsafe;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@Fork(3)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
public class LongStory {

    public static final int K = 1024;
    public static final int M = K * K;
    public static final int SIZE = 8 * M;

    int[] a;

    int align = 2*M;

    Unsafe UNSAFE;

    @Param({"-8", "-4", "-2", "0", "2", "4", "8" })
    int offset;

    long from;

    @Setup
    public void setup() {
        UNSAFE = Utils.UNSAFE;
        a = new int[SIZE];
        Random r = new Random();
        for (int i = 0; i < a.length; ++i) {
            a[i] = (byte)r.nextInt();
        }

        System.gc();
        long addr = VMSupport.addressOf(a);
        long start = addr + Unsafe.ARRAY_INT_BASE_OFFSET + 2*M;
        from = (align(start, align)-addr);
    }

    static long align(long addr, int a) {
        if (addr % a != 0) {
            return ((addr / a) + 1) * a;
        }
        return addr;
    }

    @Benchmark
    public long getlong() {
        return UNSAFE.getLong(a, from + offset);
    }

    @Benchmark
    public void putlong() {
        UNSAFE.putLong(a, from + offset, 42L);
    }

}
