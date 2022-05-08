package utils;

public class Benchmark
{
    private static long startTime;

    public static void start() {
        startTime = System.currentTimeMillis();
    }

    public static void end() {
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Benchmark: " + totalTime);
    }
}
