package test;

import backtracking.Configuration;
import nurikabe.NurikabeConfig;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestNurikabeInLab {
    /** Test loading a puzzle file and fully storing all the data. */
    @Test
    @Order(1)
    public void testLoad() {
        try {
            // load input03.txt
            NurikabeConfig config = new NurikabeConfig("data/input03.txt");

            // check dimensions
            assertEquals(3, config.getRows());
            assertEquals(4, config.getCols());

            // check cursor, initially at (0, -1)
            assertEquals(0, config.getCursorRow());
            assertEquals(-1, config.getCursorCol());

            // check toString to verify puzzle board
            final String expected =
                    ". 2 . . " + System.lineSeparator() +
                    ". . . 1 " + System.lineSeparator() +
                    ". 1 . . ";
            assertEquals(expected, config.toString());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    @Test
    @Order(2)
    public void testSuccessors() {
        try {
            // load input01.txt
            NurikabeConfig config = new NurikabeConfig("data/input01.txt");

            // generate successors at (0,0)
            List<Configuration> successors00 = new ArrayList<>(config.getSuccessors());

            // verify successors for (0,0)
            assertEquals(successors00.size(), 2);
            final String expected00Island =
                    "# . " + System.lineSeparator() +
                    "1 . ";
            NurikabeConfig successor00Island = (NurikabeConfig) successors00.get(0);
            assertEquals(expected00Island, successor00Island.toString());
            assertEquals(0, successor00Island.getCursorRow());
            assertEquals(0, successor00Island.getCursorCol());

            final String expected00Sea =
                    "@ . " + System.lineSeparator() +
                    "1 . ";
            NurikabeConfig successor00Sea = (NurikabeConfig) successors00.get(1);
            assertEquals(expected00Sea, successor00Sea.toString());
            assertEquals(0, successor00Sea.getCursorRow());
            assertEquals(0, successor00Sea.getCursorCol());

            // verify successors for (0,1) from (0,0) island
            List<Configuration> successors01 = new ArrayList<>(successor00Island.getSuccessors());
            assertEquals(2, successors01.size());
            String expected01Island =
                    "# # " + System.lineSeparator() +
                    "1 . ";
            NurikabeConfig successor01Island = (NurikabeConfig) successors01.get(0);
            assertEquals(expected01Island, successor01Island.toString());
            assertEquals(0, successor01Island.getCursorRow());
            assertEquals(1, successor01Island.getCursorCol());

            String expected01Sea=
                    "# @ " + System.lineSeparator() +
                    "1 . ";
            NurikabeConfig successor01Sea = (NurikabeConfig) successors01.get(1);
            assertEquals(expected01Sea, successor01Sea.toString());
            assertEquals(0, successor01Sea.getCursorRow());
            assertEquals(1, successor01Sea.getCursorCol());

            // verify successors for (0,1) from (0,0) sea
            successors01 = new ArrayList<>(successor00Sea.getSuccessors());
            expected01Island =
                    "@ # " + System.lineSeparator() +
                    "1 . ";
            successor01Island = (NurikabeConfig) successors01.get(0);
            assertEquals(expected01Island, successor01Island.toString());
            assertEquals(0, successor01Island.getCursorRow());
            assertEquals(1, successor01Island.getCursorCol());

            expected01Sea=
                    "@ @ " + System.lineSeparator() +
                    "1 . ";
            successor01Sea = (NurikabeConfig) successors01.get(1);
            assertEquals(expected01Sea, successor01Sea.toString());
            assertEquals(0, successor01Sea.getCursorRow());
            assertEquals(1, successor01Sea.getCursorCol());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }
}
