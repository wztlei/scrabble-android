/*
 * ScrabbleMove.java
 *
 * This code was written by William Lei.
 *
 * Contact Email: leiw9425@gmail.com
 */

package wztlei.scrabble;

import java.util.ArrayList;

/**
 *
 * @author  William Lei
 */

// A ScrabbleMove is just an ArrayList of Squares
// plus an additional property storing the number of points
public class ScrabbleMove extends ArrayList<Square> {

    public int points;

    ScrabbleMove() {
        points = 0;
    }
}
