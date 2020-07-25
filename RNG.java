import java.util.*;

public abstract class RNG {

	private static Random r = new Random();

	public static void setRNGSeed(long seed) {
		r = new Random(seed);
	}
	public static double random() {
		return r.nextDouble();
	}
	public static long randSeed() {
		return (long) (Math.random() * 1e18);
	}
	public static int randInt(int low, int high) {
		return (int) (RNG.random() * (high - low + 1)) + low;
	}
	public static boolean flipCoin() {
		return r.nextBoolean();
	}
}
