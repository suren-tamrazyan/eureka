package util;


import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Misc {
    public static String sf(String fmt, Object... pars) {
        return String.format(fmt, pars);
    }

    public static Random rand = new Random();

    public static int random(int minInclusive, int maxInclusive) {
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }

    public static long random(long minInclusive, long maxInclusive) {
        return ThreadLocalRandom.current().nextLong(minInclusive, maxInclusive + 1);
    }

    public static long getTime() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        c.add(Calendar.YEAR, 0);
        return c.getTime().getTime();

//		return System.currentTimeMillis() /*- 1000*60*60*/;
    }

}
