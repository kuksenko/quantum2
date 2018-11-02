package quantum2.demo9;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
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
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.CommandLineOptionException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;
import org.openjdk.jol.vm.*;
import sun.misc.Unsafe;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 100, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(10)
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ArrayCopyScan {

    static final int STORAGE_SIZE = 32 * 1024 * 1024;

    @Param({"1024"})
    int copySize;

    @Param("0")
    int src;

    @Param("0")
    int dst;

    byte[] data;

    long lastDataAddr;

    int from;

    @Setup(Level.Trial)
    public void setupData() throws InterruptedException {
        data = new byte[STORAGE_SIZE];
        System.gc();
        lastDataAddr = VM.current().addressOf(data);
        long start = lastDataAddr + Unsafe.ARRAY_BYTE_BASE_OFFSET;
        from = (int) (align(start, 4 * K) - start);
    }

    @Setup(Level.Iteration)
    public void setUp() throws InterruptedException {
        long addr = VM.current().addressOf(data);
        if (lastDataAddr != addr) {
            throw new IllegalStateException("Move detected: " + lastDataAddr + " " + addr);
        }
    }

    static long align(long addr, int a) {
        if (addr % a != 0) {
            return ((addr / a) + 1) * a;
        }
        return addr;
    }

    public static final int K = 1024;
    public static final int M = K * K;


    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void arraycopy() {
        System.arraycopy(data, from + src, data, from + dst, copySize);
    }


    public static void main(String... args) throws CommandLineOptionException {
        System.out.printf("%5s, %7s, %7s, %7s, ", "Src", "Dst", "Score", "Error");
        System.out.println();

        runMe(0, 4 * K - 32, "4*K-32", 1024);
        runMe(0, 4 * K - 31, "4*K-31", 1024);
        runMe(0, 4 * K - 30, "4*K-30", 1024);
        runMe(0, 4 * K - 16, "4*K-16", 1024);
        runMe(0, 4 * K - 15, "4*K-15", 1024);
        runMe(0, 4 * K - 1,  "4*K-1",  1024);
        runMe(0, 4 * K,      "4*K",   1024);
        runMe(0, 4 * K + 1,  "4*K+1", 1024);
        runMe(0, 4 * K + 2,  "4*K+2", 1024);
        runMe(0, 4 * K + 3,  "4*K+3", 1024);
        runMe(0, 4 * K + 4,  "4*K+4", 1024);
        runMe(0, 4 * K + 15,  "4*K+15", 1024);
        runMe(0, 4 * K + 16,  "4*K+16", 1024);
        runMe(0, 4 * K + 17,  "4*K+17", 1024);

        runMe(0, 1 * M  ,  "1*M", 1024);
        runMe(0, 1 * M  +1,  "1*M+1", 1024);
        runMe(0, 1 * M  +2,  "1*M+2", 1024);
        runMe(0, 1 * M  +3,  "1*M+3", 1024);
        runMe(0, 1 * M  +4,  "1*M+4", 1024);
        runMe(0, 1 * M  +8,  "1*M+8", 1024);
        runMe(0, 1 * M  +12,  "1*M+12", 1024);
        runMe(0, 1 * M  +16,  "1*M+16", 1024);
        runMe(0, 1 * M  +24,  "1*M+24", 1024);
        runMe(0, 1 * M  +28,  "1*M+28", 1024);
        runMe(0, 1 * M  +32,  "1*M+32", 1024);
        runMe(0, 1 * M  +36,  "1*M+36", 1024);
    }

    private static void runMe(int src, int dst, String dstim, int copySize) {
        try {
            Options opts = new OptionsBuilder()
                    .include(ArrayCopyScan.class.getName())
                    .param("copySize", String.valueOf(copySize))
                    .forks(1)
                    .warmupIterations(3)
                    .measurementIterations(3)
                    .warmupTime(TimeValue.milliseconds(100))
                    .measurementTime(TimeValue.milliseconds(1000))
                    .verbosity(VerboseMode.SILENT)
                    .param("src", String.valueOf(src))
                    .param("dst", String.valueOf(dst))
                    .build();

            RunResult result = new Runner(opts).runSingle();
            Result res = result.getPrimaryResult();
            System.out.printf("%5d, %7s, %7.2f, %7.2f, ",
                    src, dstim,
                    res.getScore(), res.getScoreError());


            System.out.println();


        } catch (RunnerException e) {
//                System.out.printf("%5d, %5d, FAILED%n", src, dst);
        }
    }
}