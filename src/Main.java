import ai.ReversiAi;
import utils.Benchmark;

public class Main
{
    public static void main(String[] args) {

        System.out.println("Starting tree creation");

        ReversiAi ai = new ReversiAi(8, 25, true);

        Benchmark.start();
        ai.createReversiTreeIDfs();
        Benchmark.end();
        System.out.println(ai.getExploredNodesCount());
    }
}
