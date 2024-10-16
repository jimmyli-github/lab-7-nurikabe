package nurikabe;

import backtracking.Configuration;
import com.sun.security.jgss.GSSUtil;
import test.INurikabe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;

/**
 * A class to represent a single configuration in the Nurikabe puzzle.  This is the minimal
 * pruning strategy that waits until all cells are populated before checking everything.
 *
 * @author RIT CS
 * @author YOUR NAME HERE
 */
public class NurikabeConfig implements Configuration, INurikabe {
    /** empty cell */
    private final static char EMPTY = '.';
    /** island cell */
    private final static char ISLAND = '#';
    /** sea cell */
    private final static char SEA = '@';

    // TODO
    /** The dimensions of the grid with dimensions[0] being the rows and dimensions[1] being the columns **/
    private static Integer[] dimensions;
    /** A 2D list that represents the grid with all the characters **/
    private char[][] grid;
    /** The current position with cursor[0] being the current row and cursor[1] being the current column **/
    private Integer[] cursor;

    /**
     * Construct the initial configuration from an input file whose contents
     * are, for example:<br>
     * <tt><br>
     * 3 3      # rows columns<br>
     * 1 . #    # row 1, .=empty, 1-9=numbered island, #=island, &#64;=sea<br>
     * &#64; . 3    # row 2<br>
     * 1 . .    # row 3<br>
     * </tt><br>
     * @param filename the name of the file to read from
     * @throws IOException if a problem opening the file or reading data.
     */
    public NurikabeConfig(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String[] fields = in.readLine().split("\\s+");
            dimensions = new Integer[]{Integer.valueOf(fields[0]), Integer.valueOf(fields[1])};
            cursor = new Integer[]{0, -1};
            grid = new char[dimensions[0]][dimensions[1]];
            int count = 0;
            fields = in.readLine().split("\\s+");
            while (fields.length != 1) {
                char[] temp = new char[dimensions[1]];
                for (int i = 0; i < fields.length; i++) {
                    temp[i] = fields[i].charAt(0);
                }
                grid[count] = temp;
                count++;
                fields = in.readLine().split("\\s+");
            }
        }
    }

    /**
     * Getter method for max rows
     * @return rows
     */
    @Override
    public int getRows() {
        return dimensions[0];
    }

    /**
     * Getter method for max cols
     * @return cols
     */
    @Override
    public int getCols() {
        return dimensions[1];
    }

    /**
     * Getter method for a specific value on the grid given the row and column
     * @param row the row
     * @param col the column
     * @return specific value on the grid
     */
    @Override
    public char getVal(int row, int col) {
        return grid[row][col];
    }

    /**
     * Getter method for current row position
     * @return current row
     */
    @Override
    public int getCursorRow() {
        return cursor[0];
    }

    /**
     * Getter method for current col position
     * @return current col
     */
    @Override
    public int getCursorCol() {
        return cursor[1];
    }

    /**
     * The copy constructor takes a config, other, and makes a full "deep" copy
     * of its instance data.  It also populates the board with an island or sea
     * cell if desired.
     *
     * @param other the config to copy
     * @param val the value to store at the next cursor spot
     */
    protected NurikabeConfig(NurikabeConfig other, char val) {
       // TODO
        this.cursor = other.cursor.clone();

        this.cursor[1]++;
        if (cursor[1].equals(dimensions[1])) {
            cursor[0]++;
            cursor[1] = 0;
        }

        this.grid = new char[dimensions[0]][dimensions[1]];
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(other.grid[i], 0, grid[i], 0, grid[i].length);
        }

        if (Character.isDigit(grid[cursor[0]][cursor[1]])) {
            this.grid[cursor[0]][cursor[1]] = other.grid[cursor[0]][cursor[1]];
        }
        else {
            this.grid[cursor[0]][cursor[1]] = val;
        }
    }

    /**
     * There are two successors that can come from a valid config.  They
     * are generated here by using the copy constructor.  At the cursor
     * cell we either use an ISLAND cell or SEA cell..
     *
     * @return the successors
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        List<Configuration> successors = new LinkedList<>();

        NurikabeConfig nurikabeConfig = new NurikabeConfig(this, ISLAND);
        successors.add(nurikabeConfig);
        nurikabeConfig = new NurikabeConfig(this, SEA);
        successors.add(nurikabeConfig);

        return successors;
    }

    /**
     * There are four conditions that must be checked to determine if a successor
     * config is valid:
     *
     * 1. The sea is completely connected
     * 2. There are no pools which are 2 x 2 of SEA cells
     * 3. All the islands sum up to the correct number of cells (Condition for when grid if fully populated)
     * 4. Islands are connected (Condition for when grid if fully populated)
     *
     * @return whether this new config is valid or not
     */
    @Override
    public boolean isValid() {
        int row = cursor[0];
        int col = cursor[1];

        if (isGoal()) {
            return correctIslandSum(row, col) & noPools(row, col) & allSeaConnected() & islandsConnected();
        }
        return correctIslandSum(row, col) & noPools(row, col);

    }

    /**
     * Checks to see if there are no pools in the grid.
     * If the current is not on the first row and col,
     * it will check if the cursor is on SEA. If it is,
     * it sees if the top, left, and top-left cells are
     * also SEA. If they are all SEA, it returns false.
     * Otherwise, it will return true.
     * @param row
     * @param col
     * @return
     */
    public boolean noPools(int row, int col) {
        if (row != 0 && col != 0) {
            return grid[row][col] != SEA ||
                    grid[row][col] != grid[row - 1][col] ||
                    grid[row][col] != grid[row][col - 1] ||
                    grid[row][col] != grid[row - 1][col - 1];
        }
        return true;
    }

    /**
     * Checks if the island count matches with the digit that it is
     * attached to. This is done by using stack, DFS, and keeping count of
     * the amount of islands connected. Once a digit is found, it will
     * be set to a variable and if count ever becomes greater than the digit,
     * the method will return false.
     * @param row the current row
     * @param col the current col
     * @return whether there is a correct island sum
     */
    public boolean correctIslandSum(int row, int col) {
        int count = 0;
        int numCount = 0;
        int num = Integer.MAX_VALUE;
        Stack<ArrayList<Integer>> stack = new Stack<>();
        HashSet<ArrayList<Integer>> hashSet = new HashSet<>();
        ArrayList<Integer> check = new ArrayList<>();
        check.add(row);
        check.add(col);
        stack.add(check);

        while (!stack.empty()) {
            ArrayList<Integer> current = stack.pop();
            int currentRow = current.get(0);
            int currentColumn = current.get(1);
            char currentChar = grid[currentRow][currentColumn];
            if (!hashSet.contains(current)) {
                hashSet.add(current);
                if (currentChar == ISLAND || Character.isDigit(currentChar)) {
                    count++;
                    if (Character.isDigit(currentChar)) {
                        numCount++;
                        num = Character.getNumericValue(currentChar);
                        if (numCount > 1) {
                            return false;
                        }
                    }
                    if (count > num) {
                        return false;
                    }

                    ArrayList<Integer> top = new ArrayList<>();
                    if (currentRow - 1 >= 0) {
                        top.add(currentRow - 1);
                        top.add(currentColumn);
                        stack.push(top);
                    }
                    ArrayList<Integer> right = new ArrayList<>();
                    if (currentColumn + 1 < dimensions[1]) {
                        right.add(currentRow);
                        right.add(currentColumn + 1);
                        stack.push(right);
                    }
                    ArrayList<Integer> bottom = new ArrayList<>();
                    if (currentRow + 1 < dimensions[0]) {
                        bottom.add(currentRow + 1);
                        bottom.add(currentColumn);
                        stack.push(bottom);
                    }
                    ArrayList<Integer> left = new ArrayList<>();
                    if (currentColumn - 1 >= 0) {
                        left.add(currentRow);
                        left.add(currentColumn - 1);
                        stack.push(left);
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks to see if all SEA are connected after the grid is
     * fully made. It loops through each cell on the grid to find
     * the first SEA and count the total amount of SEAs on the grid.
     * Stack and DFS is then used to add the connected SEAs to a
     * hashSet. By the end of the method, if the size of the hashSet
     * is not equal to the count of total SEAs, then it will return false.
     * Otherwise, it will return true.
     * @return whether all SEA are connected
     */
    public boolean allSeaConnected() {
        Stack<ArrayList<Integer>> stack = new Stack<>();
        HashSet<ArrayList<Integer>> hashSet = new HashSet<>();

        int seaCount = 0;
        for (int row = 0; row < dimensions[0]; row++) {
            for (int col = 0; col < dimensions[1]; col++) {
                if (grid[row][col] == SEA) {
                    ArrayList<Integer> coords = new ArrayList<>();
                    coords.add(row);
                    coords.add(col);
                    if (stack.empty()) {
                        stack.add(coords);
                    }
                    seaCount++;
                }
            }
        }

        while (!stack.empty()) {
            ArrayList<Integer> current = stack.pop();
            int currentRow = current.get(0);
            int currentColumn = current.get(1);
            char currentChar = grid[currentRow][currentColumn];
            if (currentChar == SEA) {
                if (!hashSet.contains(current)) {
                    hashSet.add(current);
                    ArrayList<Integer> top = new ArrayList<>();
                    if (currentRow - 1 >= 0) {
                        top.add(currentRow - 1);
                        top.add(currentColumn);
                        stack.push(top);
                    }
                    ArrayList<Integer> right = new ArrayList<>();
                    if (currentColumn + 1 < dimensions[1]) {
                        right.add(currentRow);
                        right.add(currentColumn + 1);
                        stack.push(right);
                    }
                    ArrayList<Integer> bottom = new ArrayList<>();
                    if (currentRow + 1 < dimensions[0]) {
                        bottom.add(currentRow + 1);
                        bottom.add(currentColumn);
                        stack.push(bottom);
                    }
                    ArrayList<Integer> left = new ArrayList<>();
                    if (currentColumn - 1 >= 0) {
                        left.add(currentRow);
                        left.add(currentColumn - 1);
                        stack.push(left);
                    }
                }
            }
        }
        return hashSet.size() == seaCount;
    }

    /**
     * Checks to see if all islands are connected. It loops
     * through each cell on the grid and checks to see if
     * the cell is an ISLAND. If it is an ISLAND, then stack
     * and DFS is used to check what cells are connected to it.
     * If it is connected to a number, a boolean is updated to true.
     * After finishing the stack, if no digit is connected to ISLAND,
     * and the boolean is still false, then it will return false.
     * Otherwise, it will continue to loop through the entire grid.
     * If the entire grid gets finished, the method will return
     * true.
     * @return whether all the islands are connected to a digit
     */
    public boolean islandsConnected() {
        for (int row = 0; row < dimensions[0]; row++) {
            for (int col = 0; col < dimensions[1]; col++) {
                if (grid[row][col] == ISLAND) {
                    boolean connected = false;
                    Stack<ArrayList<Integer>> stack = new Stack<>();
                    HashSet<ArrayList<Integer>> hashSet = new HashSet<>();
                    ArrayList<Integer> check = new ArrayList<>();
                    check.add(row);
                    check.add(col);
                    stack.add(check);
                    while (!stack.empty()) {
                        ArrayList<Integer> current = stack.pop();
                        int currentRow = current.get(0);
                        int currentColumn = current.get(1);
                        char currentChar = grid[currentRow][currentColumn];
                        if (currentChar == ISLAND || Character.isDigit(currentChar)) {
                            if (!hashSet.contains(current)) {
                                hashSet.add(current);
                                if (Character.isDigit(currentChar)) {
                                    connected = true;
                                    break;
                                }
                                ArrayList<Integer> top = new ArrayList<>();
                                if (currentRow - 1 >= 0) {
                                    top.add(currentRow - 1);
                                    top.add(currentColumn);
                                    stack.push(top);
                                }
                                ArrayList<Integer> right = new ArrayList<>();
                                if (currentColumn + 1 < dimensions[1]) {
                                    right.add(currentRow);
                                    right.add(currentColumn + 1);
                                    stack.push(right);
                                }
                                ArrayList<Integer> bottom = new ArrayList<>();
                                if (currentRow + 1 < dimensions[0]) {
                                    bottom.add(currentRow + 1);
                                    bottom.add(currentColumn);
                                    stack.push(bottom);
                                }
                                ArrayList<Integer> left = new ArrayList<>();
                                if (currentColumn - 1 >= 0) {
                                    left.add(currentRow);
                                    left.add(currentColumn - 1);
                                    stack.push(left);
                                }
                            }
                        }
                    }
                    if (!connected) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks to see if the cursor is at the end of the grid
     * @return whether the cursor is at the end of the grid
     */
    @Override
    public boolean isGoal() {
        return Objects.equals(cursor[0], dimensions[0] - 1) && Objects.equals(cursor[1], dimensions[1] - 1);
    }

    /**
     * Returns the string representation of the puzzle, e.g.: <br>
     * <tt><br>
     * 1 . #<br>
     * &#64; . 3<br>
     * 1 . .<br>
     * </tt><br>
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int row=0; row<getRows(); ++row) {
            for (int col=0; col<getCols(); ++col) {
                result.append(getVal(row, col)).append(" ");
            }
            if (row != getRows()-1) {
                result.append(System.lineSeparator());
            }
        }
        return result.toString();
    }
}