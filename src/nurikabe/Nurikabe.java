package nurikabe;

import backtracking.Backtracker;
import backtracking.Configuration;

import java.io.IOException;
import java.util.Optional;

/**
 * Main program for Nurikabe puzzle.
 *
 * @author RIT CS
 */
public class Nurikabe {
    /**
     * The main method.
     *
     * $ java Nurikabe file debug min
     * - file is the filename
     * - debug is true for debugging, otherwise false
     * - min is for the minimal pruner, otherwise it is the regular one
     *
     * @param args the command line arguments (name of input file and debug)
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java nurikabe.Nurikabe file debug min");
        } else {
            System.out.println("Puzzle " + args[0]);

            try {
                // create the initial config from the file
                Configuration init;
                init = new NurikabeConfig(args[0]);

                // display initial config
                System.out.println(init);

                // create the backtracker with the debug flag
                boolean debug = args[1].equals("true");
                Backtracker bt = new Backtracker(debug);

                // start the clock
                double start = System.currentTimeMillis();

                // attempt to solve the puzzle
                Optional<Configuration> sol = bt.solve(init);

                // compute the elapsed time
                System.out.println("Elapsed time: " +
                        (System.currentTimeMillis() - start) / 1000.0 + " seconds.");
                System.out.println(bt.getConfigCount() + " configurations generated.");

                // indicate whether there was a solution, or not
                if (sol.isPresent()) {
                    System.out.println("Solution:\n" + sol.get());
                } else {
                    System.out.println("No solution!");
                }
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
            }
        }
    }
}
