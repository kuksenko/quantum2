package quantum2;

import org.openjdk.jol.vm.*;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Utils {

    public static final Unsafe UNSAFE;

    static {
        UNSAFE = AccessController.doPrivileged(
                (PrivilegedAction<Unsafe>) () -> {
                    try {
                        Field unsafe = Unsafe.class.getDeclaredField("theUnsafe");
                        unsafe.setAccessible(true);
                        return (Unsafe) unsafe.get(null);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
        );
    }

    public static void shuffle(byte[] d, Random rnd) {
        for (int i = d.length; i > 1; i--) {
            int j = rnd.nextInt(i);
            byte tmp = d[j];
            d[j] = d[i-1];
            d[i-1] = tmp;
        }
    }

    public static void shuffle(int[] d, Random rnd) {
        for (int i = d.length; i > 1; i--) {
            int j = rnd.nextInt(i);
            int tmp = d[j];
            d[j] = d[i-1];
            d[i-1] = tmp;
        }
    }

    public static long align(long addr, int a) {
        if (addr % a != 0) {
            return ((addr / a) + 1) * a;
        }
        return addr;
    }

    public static int align(int[] arr, int a) {
        long lastDataAddr = VM.current().addressOf(arr);
        long start = lastDataAddr + Unsafe.ARRAY_INT_BASE_OFFSET;
        return (int) (align(start, a) - start)/Unsafe.ARRAY_INT_INDEX_SCALE;
    }

    public static long address(Object o) {
        return VM.current().addressOf(o);
    }


    public static int countHitsToStride(byte[][] arr, int stride) {
        Set<Long> set = new HashSet<Long>();
        for(byte[] d: arr) {
            set.add(VM.current().addressOf(d) % stride);
        }
        return set.size();
    }

    public static String countStrideDistro(byte[][] arr, int stride) {
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        for(byte[] d: arr) {
            map.compute((VM.current().addressOf(d) % stride)>>6, (k, v) -> (v == null) ? 1 : v+1);
        }
        List<Integer> l = new ArrayList<>(map.values());
        Collections.sort(l, Comparator.reverseOrder());
        return l.toString();
    }
}
