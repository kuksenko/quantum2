package quantum2.demo4;

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
import org.openjdk.jol.vm.*;
import sun.misc.Unsafe;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@Fork(3)
@Warmup(iterations = 3, time=3)
@Measurement(iterations = 3, time =3)
public class FillBytes {

    public static final int K = 1024;
    public static final int M = K * K;
    public static final int SIZE = 32 * M;

    byte[] a;

    @Param({ ""+2*M})
    int align;

//    @Param({"128", "256", "512", "1024", ""+16*K, ""+64*K})
    @Param({"256", "512", "1024"})
    int asize;

    @Param({"-64", "-32", "-16", "-8", "-4", "-2", "-1", "0", "1", "2", "4", "8", "16", "32", "64"})
    int offset;

    int from;

    @Setup
    public void setup() {
        a = new byte[SIZE];
        Random r = new Random();
        for (int i = 0; i < a.length; ++i) {
            a[i] = (byte)r.nextInt();
        }

        System.gc();
        long addr = VM.current().addressOf(a);
        long start = addr + Unsafe.ARRAY_BYTE_BASE_OFFSET;
        from = (int) (align(start, align) - start) / Unsafe.ARRAY_BYTE_INDEX_SCALE;
    }

    static long align(long addr, int a) {
        if (addr % a != 0) {
            return ((addr / a) + 1) * a;
        }
        return addr;
    }

    @Benchmark
    public void fill() {
        Arrays.fill(a, from + offset, from + offset + asize, (byte)42);
    }

}
