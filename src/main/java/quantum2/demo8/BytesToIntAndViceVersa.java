package quantum2.demo8;

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

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations=3)
@Measurement(iterations=3)
public class BytesToIntAndViceVersa {

    ByteBuffer buf;

    public byte b0;
    public byte b1;
    public byte b2;
    public byte b3;

    public int  i0;

    @Setup
    public void setup() {
        buf = ByteBuffer.allocateDirect(4096);
    }

    @Benchmark
    public int bytesToInt() {
        buf.put(0, b0);
        buf.put(1, b1);
        buf.put(2, b2);
        buf.put(3, b3);
        return buf.getInt(0);
    }

    @Benchmark
    public int intToBytes() {
        buf.putInt(0, i0);
        return buf.get(0) + buf.get(1) + buf.get(2) + buf.get(3);
    }

}
