package solver.ofc;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
//	public static Random rand = new Random();
	private static final String dfi = "yyyyMMdd_HHmmss";

	public static BigInteger factorial(int f) {
		BigInteger result = BigInteger.ONE;
		for (int i = 1; i <= f; i++)
			result = result.multiply(BigInteger.valueOf(i));
		return result;
	}
	
	public static BigInteger combinationCount(int n, int m) {
		return factorial(n).divide(factorial(m).multiply(factorial(n-m)));
	}

	public static int deadCardsCount(int playedCardsCount) {
		int sizeDead = 0;
		switch (playedCardsCount) {
			case 0:
				sizeDead = 0;
				break;
			case 5:
				sizeDead = 0;
				break;
			case 7:
				sizeDead = 1;
				break;
			case 9:
				sizeDead = 2;
				break;
			case 11:
				sizeDead = 3;
				break;
			case 13:
				sizeDead = 4;
				break;
			default:
				throw new IllegalArgumentException("Unexpected size of cards: " + playedCardsCount);
		}
		return sizeDead;
	}
	
	public static long getTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(System.currentTimeMillis()));
		c.add(Calendar.YEAR, 0);
		return c.getTime().getTime();
	}

	public static void sleep(long t) {
		try {
			Thread.sleep(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String dateFormat(long t, String f) {
		SimpleDateFormat df = new SimpleDateFormat(f);
        return df.format(t);
	}
	public static String dateFormatIntel(long t) {
        return dateFormat(t, dfi);
	}

	public static void main(String[] args) {
		System.out.println(combinationCount(33, 8));
	}
}
