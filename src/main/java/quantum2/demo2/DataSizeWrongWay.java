package quantum2.demo2;

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
public class DataSizeWrongWay {

    static class MyData {

        private byte[] bytes;
        private int length;

        public MyData(int length) {
            this.bytes = new byte[length];
            this.length = length;
        }

        public int length() {
            return length;
        }
    }

    MyData[] data = new MyData[256];

    @Setup
    public void setup() {
        Random rnd = new Random();
        Arrays.setAll(data, i -> new MyData(512 * 1024 + rnd.nextInt(64 * 1024)));
    }

    @Benchmark
    public int dataSize() {
        int s = 0;
        for (MyData a : data) {
            s += a.length();
        }
        return s;
    }

}
