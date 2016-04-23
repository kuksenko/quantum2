package quantum2.demo1;

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

import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class CopyMe {


    public static final int K = 1024;

    int[] a;
    int[] b;

    @Param(""+512*K)
    int size;

    @Param({"-8", "0"})
    int delta;

    @Setup
    public void setup() {
        size+=delta;
        a = new int[size];
        b = new int[size];
//        System.out.println("A="+Long.toHexString(VMSupport.addressOf(a)));
//        System.out.println("B="+Long.toHexString(VMSupport.addressOf(b)));
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
