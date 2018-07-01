/*
 * Square.java
 *
 * This code was written by William Lei.
 *
 * Contact Email: leiw9425@gmail.com
 */

package wztlei.scrabble;

/**
 *
 * @author  William Lei
 */
public class Square {

    public SquareType type;
    public boolean[] downCrossCheck;
    public char letter; // Special values: '.' = empty square and
                        //                 lowercase letter = blank tile
    public int row;
    public int col;
    public int minAcrossWordLength;

    Square() {
        type = SquareType.OUTSIDE;
        letter = '.';
    }

}
