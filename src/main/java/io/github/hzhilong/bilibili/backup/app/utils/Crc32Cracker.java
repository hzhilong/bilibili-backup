package io.github.hzhilong.bilibili.backup.app.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author https://github.com/xmcp/pakku.js/
 */
public class Crc32Cracker {

    public final int UID_MAX_DIGIT = 10;
    private final int POLY = 0xedb88320;
    private final long[] crc32_table;
    private long[] rainbow_0;
    private long[] rainbow_1;
    private long[] rainbow_pos;
    private long[] rainbow_hash;

    public Crc32Cracker() {
        crc32_table = new long[256];
        for (int i = 0; i < 256; i++) {
            int crc = i;
            for (int j = 0; j < 8; j++) {
                if ((crc & 1) == 1) {
                    crc = ((crc >>> 1) ^ POLY);
                } else {
                    crc = crc >>> 1;
                }
            }
            crc32_table[i] = crc;
        }
        rainbow_0 = makeRainbow(100000);
        int[] fiveZeros = new int[]{0, 0, 0, 0, 0};
        rainbow_1 = new long[rainbow_0.length];
        for (int i = 0; i < rainbow_0.length; i++) {
            long crc = rainbow_0[i];
            rainbow_1[i] = compute(fiveZeros, crc);
        }

        rainbow_pos = new long[65537];
        rainbow_hash = new long[200000];

        makeHash();
    }

    private long updateCrc(int by, long crc) {
        return ((crc >>> 8) ^ crc32_table[(int) (((crc & 0xff) ^ by) & 0xFFFFFFFFL)] & 0xFFFFFFFFL);
    }

    private long compute(int[] arr) {
        return compute(arr, 0);
    }

    private long compute(int[] arr, long init) {
        long crc = init;
        for (int j : arr) {
            crc = updateCrc(j, crc);
        }
        return crc;
    }

    private int[] getIntArr(int num) {
        int len = String.valueOf(num).length();
        int[] arr = new int[len];
        for (int i = len - 1; i >= 0; i--) {
            arr[i] = num % 10;
            num /= 10;
        }
        return arr;
    }

    private long[] makeRainbow(int n) {
        long[] rainbow = new long[n];
        for (int i = 0; i < n; i++) {
            rainbow[i] = compute(getIntArr(i));
        }
        return rainbow;
    }

    private void makeHash() {
        for (int i = 0; i < rainbow_0.length; i++) {
            rainbow_pos[(int) (rainbow_0[i] >>> 16)]++;
        }

        for (int i = 1; i <= 65536; i++) {
            rainbow_pos[i] += rainbow_pos[i - 1];
        }

        for (int i = 0; i < rainbow_0.length; i++) {
            long po = --rainbow_pos[(int) (rainbow_0[i] >>> 16)];
            rainbow_hash[(int) (po << 1 & 0xFFFFFFFFL)] = rainbow_0[i];
            rainbow_hash[(int) (po << 1 & 0xFFFFFFFFL | 1)] = i;
        }
    }

    private List<Long> lookup(long crc) {
        List<Long> results = new ArrayList<>();
        long first = rainbow_pos[(int) (crc >>> 16)];
        long last = rainbow_pos[(int) (1 + (crc >>> 16))];
        for (long i = first; i < last; i++) {
            if (rainbow_hash[(int) (i << 1 & 0xFFFFFFFFL)] == crc)
                results.add(rainbow_hash[(int) (i << 1 & 0xFFFFFFFFL | 1)]);
        }
        return results;
    }

    public List<Long> crack(long maincrc) {
        return crack(maincrc, UID_MAX_DIGIT);
    }

    public List<Long> crack(long maincrc, int max_digit) {
        List<Long> results = new ArrayList<>();
        maincrc = (~maincrc) & 0xFFFFFFFFL;
        long basecrc = 0xffffffffL;
        for (int ndigits = 1; ndigits <= max_digit; ndigits++) {
            basecrc = updateCrc(0x30, basecrc);
            if (ndigits < 6) {
                long first_uid = (long) Math.pow(10, ndigits - 1);
                long last_uid = (long) Math.pow(10, ndigits);
                for (long uid = first_uid; uid < last_uid; uid++) {
                    if (maincrc == ((basecrc ^ rainbow_0[(int) uid] & 0xFFFFFFFFL))) {
                        results.add(uid);
                    }
                }

            } else {
                long first_prefix = (long) Math.pow(10, ndigits - 6);
                long last_prefix = (long) Math.pow(10, ndigits - 5);
                for (long prefix = first_prefix; prefix < last_prefix; prefix++) {
                    long rem = (maincrc ^ basecrc & 0xFFFFFFFFL ^ rainbow_1[(int) prefix] & 0xFFFFFFFFL);
                    List<Long> items = lookup(rem);
                    for (Long item : items) {
                        results.add((prefix * 100000 + item) & 0xFFFFFFFFL);
                    }
                }
            }
        }
        return results;
    }
}
