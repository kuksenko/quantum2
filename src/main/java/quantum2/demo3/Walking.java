package quantum2.demo3;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations=3)
@Measurement(iterations=10)
public class Walking {

    public static final int K = 1024;
    public static final int M = K*K;

    int[] a;

    @Param({
            ""+8*K, ""+16*K,""+24*K,""+32*K,""+48*K,""+64*K,
            ""+96*K,""+128*K, ""+196*K, ""+256*K,""+320*K,""+384*K,""+448*K, ""+512*K,
            ""+1*M, ""+2*M,""+3*M, ""+4*M,""+5*M,""+6*M,""+7*M, ""+8*M,
    })
    int bytes;
    int size;

    Node64 root;
    Node64[] nodes;

    @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8"})
    int aperm;

    @Setup
    public void setup() {
        size = bytes/64;
        nodes = new Node64[size];
        root = setup(nodes);
    }

    public Node64 setup(Node64[] a){
        Arrays.setAll(a, Node64::new);
        List<Node64> l = new ArrayList<>(Arrays.asList(a));
        if(aperm ==0) {
            Collections.shuffle(l);
        } else {
            l = permute(l, aperm);
        }
        Node64 prev = null;
        Node64 first = null;
        for(Node64 n:l) {
            if(prev != null) {
                prev.next = n;
                n.prev = prev;
            } else {
                first = n;
            }
            prev = n;
        }
        prev.next = first;
        first.prev = prev;
        return first;
    }

    private List<Node64>  permute(List<Node64> l, int perm) {
        if(l.size()<=perm || perm==1) {
            return l;
        }
        List<Node64> res = new ArrayList<>(l);
        for(int p=0, j=0; p < perm; p++) {
            for(int i=p; i<l.size(); i+=perm) {
                res.set(j++, l.get(i));
            }
        }
        return res;
    }


    public static final int COUNT = 256*K;

    @Benchmark
    @OperationsPerInvocation(COUNT)
    public int walk() {
        return forward(root, COUNT);
    }

    public int forward(Node64 node, int cnt) {
        for(int i=0; i<cnt; i++) {
            node = node.next;
        }
        return node.value;
    }

    static class Node64 {  // 12
        Node64 next;       // 16
        Node64 prev;       // 20
        int value;         // 24
        int p1,p2;         // 32
        int a1,a2,a3,a4,a5,a6,a7,a8;

        public Node64(int value) {
            this.value = value;
        }
    }


}
