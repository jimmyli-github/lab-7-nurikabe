package test;

/**
 * An interface to the Nurikabe puzzle representation that is used by the JUnit test
 * and Magnet's toString().
 */
public interface INurikabe {
    /**
     * Get the number of rows in the puzzle.
     * @return number of rows
     */
    int getRows();

    /**
     * Get the number of columns in the puzzle.
     * @return number of columns
     */
    int getCols();

    /**
     * Get the stored value at (row, col) for a particular configuration.
     * @param row the row
     * @param col the column
     * @return the val
     */
    char getVal(int row, int col);

    /**
     * Get the cursor row location.
     * @return cursor row
     */
    int getCursorRow();

    /**
     * Get the cursor column location.
     * @return cursor column
     */
    int getCursorCol();

}
