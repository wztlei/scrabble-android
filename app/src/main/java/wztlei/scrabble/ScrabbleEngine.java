/*
 * ScrabbleEngine.java
 *
 * This code is the property of its creator William Lei.
 *
 * Purpose: I created this project for the IB English Scrabble tournament.
 *          It uses a variant of Appel and Jacobson's algorithm to create a
 *          computer program that can play scrabble. The algorithm is
 *          simplified since it uses a trie rather than a dawg. This Java
 *          program was translated from its original version in C++.
 *
 * References: https://pdfs.semanticscholar.org/da31/
 *                  cb24574f7c881a5dbf008e52aac7048c9d9c.pdf
 *             https://web.stanford.edu/class/cs221/2017/restricted/p-final/
 *                  cajoseph/final.pdf
 *
 *
 * Contact Email: leiw9425@gmail.com
 */

package wztlei.scrabble;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;


/**
 *
 * @author  William Lei
 */
public class ScrabbleEngine {

    // Define properties of the ScrabbleProgram class
    public HashMap <String, Integer> words;
    public TrieNode trieRoot;
    public Tile[] tiles;

    final String tilesFileName;
    final String wordsFileName;
    final String boardFileName;
    final String gameFileName;
    final int numBoardRows;
    final int numBoardCols;
    final int numRackTiles;

    // Constructor function for the main class
    public ScrabbleEngine () {

        tilesFileName = "tiles.txt";
        wordsFileName = "jonbcard_github_words.txt";
        boardFileName = "board.txt";
        gameFileName = "test_game_blank.txt";
        numBoardRows = 15;
        numBoardCols = 15;
        numRackTiles = 7;

        // Get the data for the tiles and words
        words = readWordData();
        trieRoot = createWordTrie();
        tiles = readTileData();
    }

    /**
     * @return  an unordered map of Strings containing all the words in the
     *          scrabble dictionary. The key is type String since it is stores
     *          the word. The mapped value is type integer since it stores if
     *          the word is worth a bonus multiplier.
     */
    public HashMap <String, Integer> readWordData () {
        // Declare vector to store all of the words in the scrabble dictionary
        HashMap <String, Integer> wordHashMap =
                new HashMap <String, Integer> ();

        // Open file containing the word data
        Scanner wordDataFile = null;

        // Try opening the file
        try {
            wordDataFile = new Scanner(new FileInputStream(wordsFileName));
        } catch (FileNotFoundException ex) {
            System.out.println("Could not open " + wordsFileName);
        }

        // If file is opened
        if (wordDataFile != null) {
            while(wordDataFile.hasNextLine()) {
                // IMPORTANT: The words must all be in uppercase.
                String word = wordDataFile.nextLine();

                // Insert the word into the unordered map
                wordHashMap.put(word, 1);
            }
        }

        return wordHashMap;
    }

    /**
     * @return  a TrieNode that is the root of the trie
     */
    public TrieNode createWordTrie () {
        TrieNode root = new TrieNode ();
        root.letter = '*';
        root.isTerminalNode = false;

        // Add every word in the words HashMap
        for (String word : words.keySet()) {

            if (word.length() > 2 && word.matches("[A-Z]+")) {
                insertIntoTrie(root, word);
            }
        }

        return root;
    }

    /**
     * Inserts TrieNodes into the trie to store the word in the data structure.
     *
     * @param   root    a TrieNode that is the root of the trie
     * @param   word   the String of letters to be inserted
     */
    public void insertIntoTrie (TrieNode root, String word) {

        TrieNode currNode = root;

        // Go through each letter in the word -> each letter is word[i]
        for (int i = 0; i < word.length(); i++) {
            // Calculate the index for the letterIndexes property
            // for the letter in the word
            // letterIndex allows the program to determine whether a
            // child has the letter word[i] in O(1) time
            int letterIndex = word.charAt(i) - 'A';

            // Check to see if there are no children with the letter
            // in the word
            if (currNode.letterIndexes[letterIndex] == -1) {

                // Create a new node
                TrieNode newNode = new TrieNode();
                newNode.letter = word.charAt(i);
                newNode.isTerminalNode = false;

                // Update the current node by adding newNode as a child
                currNode.children.add(newNode);

                // Also, update the letterIndexes property by storing
                // the index of the child where the letter word[i] can be found
                // It is the index of the last child in the children property
                // since the node was just added
                currNode.letterIndexes[letterIndex] =
                        currNode.children.size() - 1;
            }

            // Go to the child of currNode that contains the letter in the word
            int childIndex = currNode.letterIndexes[letterIndex];
            currNode = currNode.children.get(childIndex);
        }

        currNode.isTerminalNode = true;
    }

    /**
     * Prints a word trie to the console.
     * Uses recursive calls to go down the trie.
     *
     * @param   node    a TrieNode containing the data for a node
     */
    public void printWordTrie (TrieNode node) {

        // Output the node's letter property
        System.out.println(node.letter);

        // Go through all the node's children's letters
        for (TrieNode child : node.children) {
            System.out.print(String.valueOf(child.letter) + " ");
        }

        System.out.println();
        System.out.println();

        // Print each child of the node
        for (TrieNode child : node.children) {
            printWordTrie(child);
        }
    }

    /**
     * @return  an ArrayList of Tiles with each tile object containing the
     *          right data.
     */
    public Tile[] readTileData () {

        // Declare array to store all the Tiles
        Tile[] tileArray = new Tile[27];

        Scanner tileDataFile = null;

        // Try opening the file
        try {
            tileDataFile = new Scanner (new FileInputStream(tilesFileName));
        } catch (FileNotFoundException ex) {
            System.out.println("Could not open " + tilesFileName);
        }

        // Ensure data file is open
        if (tileDataFile != null) {
            // Loop through all 27 possible tiles and add them to the vector
            for (int i = 0; i < 27; i++) {
                Tile tile = new Tile();
                tile.letter = tileDataFile.next().charAt(0);
                tile.points = Integer.parseInt(tileDataFile.next());
                tile.total = Integer.parseInt(tileDataFile.next());
                tileArray[i] = tile;
            }
        }

        return tileArray;
    }

    /**
     * @return  a SquareGrid containing the data for each square on the board.
     *          Key for the text file's characters:
     *              W = Triple Word Score
     *              w = Double Word Score
     *              L = Triple Letter Score
     *              l = Double Letter Score
     *              . = Regular Square
     *              * = Square is out of bounds
     */
    public Square [][] readBoardData () {

        // Declare board ArrayList
        Square[][] board = new Square[numBoardRows+2][numBoardCols+2];

        Scanner boardDataFile = null;

        try {
            boardDataFile = new Scanner (new FileInputStream (boardFileName));
        } catch (FileNotFoundException ex) {
            System.out.println("Could not open " + boardFileName);
        }

        // Ensure file is open
        if (boardDataFile != null) {
            int rowNum = 0;

            // Get all the rows in the board
            // The rows of x's around the actual board are to ensure that
            // tiles are not added outside the board
            while (boardDataFile.hasNextLine()) {

                // Read a line from the text file
                String line = boardDataFile.nextLine();

                // Declare an Array of Squares to store the data for each row
                Square[] row = new Square[numBoardCols+2];

                // Go through all the characters in each line
                for (int i = 0; i < line.length(); i++) {
                    Square sqr = new Square();
                    sqr.letter = '.';
                    sqr.row = rowNum;
                    sqr.col = i;

                    // Assign the Square type to sqr
                    switch (line.charAt(i)) {
                        case 'W': sqr.type = SquareType.TRIPLE_WORD;   break;
                        case 'w': sqr.type = SquareType.DOUBLE_WORD;   break;
                        case 'L': sqr.type = SquareType.TRIPLE_LETTER; break;
                        case 'l': sqr.type = SquareType.DOUBLE_LETTER; break;
                        case '.': sqr.type = SquareType.REGULAR;       break;
                        case 'x': sqr.type = SquareType.OUTSIDE;       break;
                    }

                    // Assign the downCrossCheck vector to sqr
                    switch (line.charAt(i)) {
                        case 'x':
                            sqr.downCrossCheck = new boolean[26];
                            Arrays.fill(sqr.downCrossCheck, false);
                            sqr.letter = '.';
                            break;
                        default:
                            sqr.downCrossCheck = new boolean[26];
                            Arrays.fill(sqr.downCrossCheck, true);
                            sqr.letter = '.';
                            break;
                    }

                    row[i] = sqr;
                }

                board[rowNum] = row;
                rowNum++;
            }

        }

        return board;
    }

    /**
     * Fills the board with letters which are read from a text file.
     *
     * @param   board   a square grid containing the data for the state of the
     *                  game.
     */
    public void readTestGameData (Square[][] board) {
        // Open file containing the data
        Scanner gameDataFile = null;

        try {
            gameDataFile = new Scanner (new FileInputStream (gameFileName));
        } catch (FileNotFoundException ex) {
            System.out.println("Could not open " + gameFileName);
        }

        // Ensure file is open
        if (gameDataFile != null) {
            // Go through all the rows
            for (int rowNum = 0; rowNum < numBoardRows; rowNum++) {
                // Get each row as input
                String input = gameDataFile.next();

                // Go through all the rows
                for (int colNum = 0; colNum < numBoardCols; colNum++) {
                    // row+1 and row+1 are used since the top row and column
                    // (row 0 and column 0) of board are used to mark outside
                    // squares
                    // Fill in the tiles on the board
                    board[rowNum+1][colNum+1].letter = input.charAt(colNum);
                }
            }
        }
    }

    /**
     * Updates the downCrossCheck property of each square in the board
     * Ex. board[row][col].downCrossCheck[3] == true indicates that the
     *     letter 'D' (since 'D' - 'A' == 3) can be placed at board[row][col]
     *
     * @param   board   a SquareGrid containing the data for the state of the
     *                  game
     */
    public void updateDownCrossChecks (Square[][] board) {

        // Go through all the squares in the board where tiles can be placed
        for (int row = 1; row <= numBoardRows; row++) {
            for (int col = 1; col <= numBoardCols; col++) {

                // Only check squares on which tiles can be placed
                if (board[row][col].letter == '.') {
                    String aboveSquare = new String ();
                    String belowSquare = new String ();
                    int checkRow = row - 1;

                    // Add characters above the cross-check square
                    while (board[checkRow][col].letter != '.' &&
                            board[checkRow][col].type   != SquareType.OUTSIDE) {
                        aboveSquare = Character.toUpperCase
                                (board[checkRow][col].letter)
                                + aboveSquare;
                        checkRow--;
                    }

                    checkRow = row + 1;

                    // Add characters below the cross-check square
                    while (board[checkRow][col].letter != '.' &&
                            board[checkRow][col].type   != SquareType.OUTSIDE) {
                        belowSquare = belowSquare +
                                Character.toUpperCase
                                        (board[checkRow][col].letter);
                        checkRow++;
                    }

                    // No need to update if there are blank squares
                    // above and below
                    if (aboveSquare.equals("") && belowSquare.equals("")) {
                        continue;
                    }

                    // Go through all 26 of the letters that could possibly
                    // occupy board[row][col]
                    for (int testLetter = 'A'; testLetter <= 'Z';
                         testLetter++) {
                        String testWord = aboveSquare + (char)(testLetter)
                                + belowSquare;

                        // Find in the words HashTable
                        // If it is found, then make that letter true
                        // (or valid) in the downCrossCheck property
                        board[row][col].downCrossCheck
                                [testLetter-'A'] = words.containsKey(testWord);
                    }
                }
            }
        }
    }

    /**
     * Updates the minAcrossWordLength property of every square on the board.
     * This property stores the minimum length of the word going across
     * starting from that square so that the word created connects with
     * pre-existing words.
     * Ex. If board[row][col].minAcrossWordLength == 4 indicates that a word
     *     must be 4 letters long before it connects with pre-existing words.
     *     Otherwise, the word will be disconnected.
     *
     *
     * @param   board   a SquareGrid containing the data for the state of the
     *                  game
     */
    public void updateMinAcrossWordLength (Square[][] board) {

        // Go through all the rows
        for (int row = 1; row <= numBoardRows; row++) {
            // Set the minimum word length as -1 to signify
            // squares rightward of any adjacent square
            // These squares cannot be used as the leftmost square from which
            // to extend rightwards
            int minWordLength = -1;

            // Go through all the squares in the row from right to left
            for (int col = numBoardCols; col >= 1; col--) {
                // If the square to its immediate left is occupied with a
                // letter, then the square at board[row][col] cannot be the
                // left-most square. Thus, minAcrossWordLength == -1
                if (board[row][col-1].letter != '.') {
                    board[row][col].minAcrossWordLength = -1;
                }
                // Check to see if there are tiles above, below,
                // right, or on the square
                // If so, then set the minAcrossWordLength to 1
                else if (board[row-1][col].letter != '.' ||
                        board[row+1][col].letter != '.' ||
                        board[row][col+1].letter != '.' ||
                        board[row][col].letter   != '.' ) {
                    board[row][col].minAcrossWordLength = 1;
                    minWordLength = 1;
                }
                // For squares on the extreme right which cannot be used to
                // build a word since there are no squares to the right from
                // which tiles can be added.
                // Ie. Extending right from this square will always create a
                // word that is separated from the rest of the words already
                // on the board.
                else if (minWordLength == -1) {
                    board[row][col].minAcrossWordLength = -1;
                }
                // These squares are not adjacent to any square, but extending
                // right will eventually reach a square
                else {
                    minWordLength++;
                    board[row][col].minAcrossWordLength = minWordLength;
                }
            }
        }
    }

    /**
     * Returns a vector of integers which represents the letters on a Scrabble
     * rack. These letters are available to be placed on the board.
     *
     * @param   letters     a String of all the letters in the rack
     * @return              an Array of 27 integers where each element
     *                      represents the number of tiles of that letter.
     *                      Ex. rack[4] == 2 indicates 2 E's are in the rack
     *                          rack[27] == 1 indicates 2 blank tiles
     */
    int[] fillRack (String letters) {

        int[] rack = new int [27];
        Arrays.fill(rack, 0);

        // Set the number of characters to read as
        // the min of numRackTiles and the length of the String "letters"
        int numCharsRead = (numRackTiles > letters.length()) ?
                (letters.length()) : (numRackTiles);

        // Go through all the necessary characters to read
        for (int i = 0; i < numCharsRead; i++){

            // For regular tiles
            if (Character.isUpperCase(letters.charAt(i))) {
                rack[letters.charAt(i) - 'A']++;
            }
            // For blank tiles
            else if (letters.charAt(i) == '*') {
                rack[26]++;
            }
        }

        return rack;
    }

    /**
     * Find the highest scoring possible move and the points obtained based on
     * board and rack.
     *
     * @param   board   stores the state of the Scrabble board
     * @param   rack    stores the number of each possible tile
     */
    ScrabbleMove findBestMove (Square[][] board, int[] rack) {

        // Go through all the squares to check for any squares that have tiles
        // It will find the best move and exit the function
        // as soon as it finds a tile
        for (int row = 1; row <= numBoardRows; row++) {
            for (int col = 1; col <= numBoardCols; col++) {
                // Check to see if the square has a tile
                // Only find the best move if a square on the board has a tile
                if (board[row][col].letter != '.') {
                    // Get the best move for placing tiles across and
                    // for placing tiles down
                    ScrabbleMove bestAcrossMove = findBestAcrossMove
                            (board, rack);
                    ScrabbleMove bestDownMove = findBestDownMove(board, rack);

                    // Select either the best across move or the down move
                    // Only find the best move once
                    // by returning and exiting the function
                    if (bestAcrossMove.points > bestDownMove.points) {
                        return bestAcrossMove;
                    }
                    else {
                        return bestDownMove;
                    }
                }
            }
        }

        //
        // If the function has reached this line, the board is empty
        // This means the program needs to find the best starting move
        // Scrabble rules dictate that the first move must contain 2 or more tiles
        //

        // Initialize ScrabbleMove storing the best move
        ScrabbleMove bestMove = new ScrabbleMove();

        // Find the middle row and column since these
        // determine the starting square
        int midRow = numBoardRows/2 + 1;
        int midCol = numBoardCols/2 + 1;

        // Go through all the squares left of and including the center square
        for (int col = 1; col <= midCol; col++) {
            // Set the minimum length of the first word to be placed so that
            // it covers the center square
            board[midRow][col].minAcrossWordLength = midCol - col + 1;

            // There is an exception for the center square if it is the
            // leftmost square of the starting move
            // One must place at least 2 tiles to start the game, so its
            // minAcrossWordLength is 2
            if (col == midCol) {
                board[midRow][midCol].minAcrossWordLength = 2;
            }

            // Declare variables necessary to call the function extendRight()
            ScrabbleMove currMove = new ScrabbleMove ();
            Square sqr = board[midRow][col];
            int minWordLength = sqr.minAcrossWordLength;

            // Only call extendRight when necessary
            // Ie. When less than 7 characters are needed to connect to
            // pre-existing words AND it is possible to connect to pre-existing
            // words to the right of the square
            if (minWordLength <= numRackTiles && minWordLength != -1) {
                extendRight(board, rack, trieRoot, sqr,
                        minWordLength, currMove, bestMove);
            }
        }

        return bestMove;
    }

    /**
     * Returns a vector of Squares that is the move that scores the most possible
     * points by placing tiles horizontally for a given Scrabble board and a rack.
     *
     * @param   board   stores the state of the Scrabble board
     * @param   rack    stores the number of each possible tile
     * @return          a vector of Squares storing the highest scoring move
     *                  involving tiles placed horizontally
     */
    ScrabbleMove findBestAcrossMove (Square[][] board, int[] rack) {
        // Declare a vector and a variable to store
        // the best move and highest number of points
        ScrabbleMove bestMove = new ScrabbleMove();

        // Go through all the squares in the board
        for (int row = 1; row <= numBoardRows; row++) {
            for (int col = 1; col <= numBoardCols; col++) {

                // Declare variables necessary to call extendRight()
                ScrabbleMove currMove = new ScrabbleMove();
                Square sqr = board[row][col];
                int minWordLength = sqr.minAcrossWordLength;

                // Only call extendRight when necessary
                // Ie. When less than 7 characters are needed to connect to
                // pre-existing words AND it is possible to connect to pre-existing
                // words to the right of the square
                if (minWordLength <= numRackTiles && minWordLength != -1) {
                    extendRight(board, rack, trieRoot, sqr,
                            minWordLength, currMove, bestMove);
                }
            }
        }

        return bestMove;
    }

    /**
     * Returns a vector of Squares that is the move that scores the most possible
     * points by placing tiles vertically for a given Scrabble board and a rack.
     *
     * @param   board   stores the state of the Scrabble board
     * @param   rack    stores the number of each possible tile
     * @return          a vector of Squares storing the highest scoring move
     *                  involving tiles placed vertically
     */
    ScrabbleMove findBestDownMove (Square[][] board, int[] rack) {

        Square[][] invertedBoard = invertBoard(board);

        // Find the best down move by calling the findBestAcrossMove() function
        // on the inverted board
        ScrabbleMove bestDownMove = findBestAcrossMove(invertedBoard, rack);
        return invertMove(bestDownMove);
    }

    /**
     * Finds the best move by extending rightwards from a given square
     *
     * @param   board           Array storing the state of the board
     * @param   rack            an Array of integers storing the number of
     *                          each type of tile
     * @param   node            the node in the trie storing the last
     *                          letter added to the word being built and
     *                          the next possible letters
     * @param   currSquare      the square on which a new tile may be placed
     *                          for the current move
     * @param   minWordLength   the minimum word length of the word to be
     *                          created so that it connects with
     *                          pre-existing words
     * @param   currMove        the Squares on which tiles have been placed
     *                          of the current move that is being attempted
     * @param   bestMove        the best possible move thus far represented
     *                          by a Array of squares
     */
    public void extendRight (Square[][] board, int[] rack, TrieNode node,
                             Square currSquare, int minWordLength,
                             ScrabbleMove currMove, ScrabbleMove bestMove) {

        Square sqr = board[currSquare.row][currSquare.col];

        // If the square is empty then simply do nothing
        if (sqr.type == SquareType.OUTSIDE) {
        }
        // If the current square is empty
        else if (sqr.letter == '.')
        {
            // Determine if a legal move has been found ie. a word is created and
            // the word is long enough so that it can connect with pre-existing tiles
            if (node.isTerminalNode && currMove.size() >= minWordLength) {

                calcAcrossPts(board, currMove);

                if (currMove.points > bestMove.points) {
                    bestMove.clear();
                    bestMove.addAll(currMove);
                    bestMove.points = currMove.points;
                }
            }

            // Go through all the children of the node
            for (int i = 0; i < node.children.size(); i++) {

                char childLetter = node.children.get(i).letter ;
                int childLetterIndex = childLetter - 'A';

                // Check to see if the letter of the child is in our rack AND
                // it is in the downCrossCheck set of the square
                if (rack[childLetterIndex] > 0 &&
                        sqr.downCrossCheck[childLetterIndex]) {

                    // Remove the tile from the rack
                    rack[childLetterIndex]--;

                    // Add the square onto the current move
                    addSqrToMove(sqr.row, sqr.col, childLetter, currMove);

                    // Move rightwards to the next square
                    Square nextSquare = board[sqr.row][sqr.col+1];

                    // Recursively call itself to continued extending right
                    extendRight(board, rack, node.children.get(i), nextSquare,
                            minWordLength, currMove, bestMove);

                    // Remove the square from the current move
                    currMove.remove(currMove.size() - 1);

                    // Place tile back in the rack
                    rack[childLetterIndex]++;
                }
                // Otherwise try using a blank tile
                else if (rack[26] > 0 && sqr.downCrossCheck[childLetterIndex]) {
                    // Remove the tile from the rack
                    rack[26]--;

                    // Add the square onto the current move
                    addSqrToMove(sqr.row, sqr.col,
                            Character.toLowerCase(childLetter),
                            currMove);

                    // Move rightwards to the next square
                    Square nextSquare = board[sqr.row][sqr.col+1];

                    // Recursively call itself to continued extending right
                    extendRight(board, rack, node.children.get(i), nextSquare,
                            minWordLength, currMove, bestMove);

                    // Remove the square from the current move
                    currMove.remove(currMove.size() - 1);

                    // Place tile back in the rack
                    rack[26]++;
                }
            }
        }
        // The square contains a letter
        else {
            int sqrLetterIndex = Character.toUpperCase(sqr.letter) - 'A';
            int childIndex = node.letterIndexes[sqrLetterIndex];

            // Check to see if node has a child with the letter occupying the square
            if (childIndex != -1)
            {
                // Move rightwards to the next square
                Square nextSquare = board[sqr.row][sqr.col+1];

                // Recursively call itself to continued extending right
                extendRight(board, rack, node.children.get(childIndex),
                        nextSquare, minWordLength, currMove, bestMove);
            }
        }
    }

    /**
     * Adds the square, on which a tile has just been placed,
     * onto the current move.
     *
     * @param   row         the row of the square to be added
     * @param   col         the column of the square to be added
     * @param   letter      the letter of the square to be added
     * @param   currMove    the ArrayList storing the current move.
     */
    public void addSqrToMove (int row, int col, char letter,
                              ScrabbleMove currMove) {
        Square sqr = new Square();
        sqr.row = row;
        sqr.col = col;
        sqr.letter = letter;
        currMove.add(sqr);
    }

    /**
     * Calculates the number of points obtained for a given across move.
     *
     * @param   board           a pointer to the SquareGrid containing all the data
     *                          for a Scrabble Board
     * @param   acrossMove     a vector of Squares storing all the squares on which
     *                          a tile has been placed for a given across move
     */
    public void calcAcrossPts (Square[][] board, ScrabbleMove acrossMove) {

        // If no squares are in the current move, then no points are awards
        if (acrossMove.isEmpty()) {
            acrossMove.points = 0;
        }

        // Set the variables that will increment at 0
        int rowPts = 0;
        int totalCrossPts = 0;
        int numDoubleWord = 0;
        int numTripleWord = 0;

        // Go through all the squares in the current move
        for (int i = 0; i < acrossMove.size(); i++) {

            // Store the square in the move, its row, column,
            // and number of letter points obtained without any bonuses
            Square sqr = acrossMove.get(i);
            int row = sqr.row;
            int col = sqr.col;
            int letterPts = 0;
            int colCrossPts = 0;

            // Only add points if the letter is uppercase (lowercase = blanks)
            if (Character.isUpperCase(sqr.letter)) {
                letterPts = tiles[sqr.letter - 'A'].points;
            }

            // Account for double letter, or triple letter bonuses
            // by multiplying the points obtained by the letter by 2 or 3
            if (board[row][col].type == SquareType.DOUBLE_LETTER) {
                letterPts *= 2;
            }
            else if (board[row][col].type == SquareType.TRIPLE_LETTER) {
                letterPts *= 3;
            }

            rowPts += letterPts;

            // Calculate the number of cross points
            // Column cross points are points obtained by forming vertical words
            // when playing a horizontal word across the board
            if (board[row-1][col].letter != '.' ||
                    board[row+1][col].letter != '.') {

                colCrossPts += calcColCrossPts(board, row, col);
                colCrossPts += letterPts;
            }

            // Account for double or triple word bonuses
            // by recording the number of word bonuses for the row points
            // and multiplying the column cross points by 2 or 3
            if (board[row][col].type == SquareType.DOUBLE_WORD) {
                numDoubleWord++;
                colCrossPts *= 2;
            }
            else if (board[row][col].type == SquareType.TRIPLE_WORD) {
                numTripleWord++;
                colCrossPts *= 3;
            }

            totalCrossPts += colCrossPts;
        }

        // Prepare to go through all the squares left of the move
        int row = acrossMove.get(0).row;
        int col = acrossMove.get(0).col - 1;

        // Go through all the squares left of the first tile
        // placed in the row for the move
        while (board[row][col].type != SquareType.OUTSIDE &&
                board[row][col].letter != '.') {

            char letter = board[row][col].letter;

            // Only add points if the letter is uppercase (lowercase = blanks)
            if (Character.isUpperCase(letter)) {
                rowPts += tiles[letter - 'A'].points;
            }

            col--;
        }

        // Prepare to go through all the squares in between the move
        col = acrossMove.get(0).col;

        // Go through all the squares in between the first and last tile
        // placed in the row for the move
        while (col <= acrossMove.get(acrossMove.size()-1).col) {

            char letter = board[row][col].letter;

            // Only add points if the letter is uppercase (lowercase = blanks)
            if (Character.isUpperCase(letter)) {
                rowPts += tiles[letter - 'A'].points;
            }

            col++;
        }

        // Prepare to go through all the squares right of the move
        col = acrossMove.get(acrossMove.size()-1).col + 1;

        // Go through all the squares right of the last tile
        // placed in the row for the move
        while (board[row][col].type != SquareType.OUTSIDE &&
                board[row][col].letter != '.') {

            char letter = board[row][col].letter;

            // Only add points if the letter is uppercase (lowercase = blanks)
            if (Character.isUpperCase(letter)) {
                rowPts += tiles[letter - 'A'].points;
            }

            col++;
        }

        // Double the row points for a double word bonus
        for (int i = 1; i <= numDoubleWord; i++) {
            rowPts *= 2;
        }

        // Triple the row points for a double word bonus
        for (int i = 1; i <= numTripleWord; i++) {
            rowPts *= 3;
        }

        // If you use 7 tiles in your move, you get a bingo of 50 points
        if (acrossMove.size() >= 7) {
            acrossMove.points = rowPts + totalCrossPts + 50;
        }
        else {
            acrossMove.points = rowPts + totalCrossPts;
        }
    }

    /**
     * Calculates the number of points obtained from tiles above and below a square.
     * Ex. Hypothetically, if the word "DRAG" and the tiles "M" and "O" are on the
     *     board and then the word "cake" is create horizontally by adding the tiles
     *     "C", "K", and "E". The function
     *     returns the points obtained by placing the tiles "C", "K", and "e" only.
     *
     *     . D . . .          . D . . .
     *     . R . M .   -->    . R . M .
     *     * * * * *          C A K E D
     *     . G . . O          . G . . O
     *
     * @param   board       an Array containing all the data for a Scrabble
     *                      Board that is being played
     * @param   startRow    the row number of the square above and below which
     *                      the function must calculate the number of
     *                      column cross points
     * @param   col         the column number of the square above and below the
     *                      function must calculate the number of column cross
     *                      points
     * @return              the number of points obtained from tiles directly
     *                      above and below a square
     */
    public int calcColCrossPts (Square[][] board, int startRow, int col) {

        int colCrossPts = 0;

        // Start one row above the square
        int row = startRow - 1;

        // Calculate points formed by letters above the square
        while (board[row][col].type != SquareType.OUTSIDE &&
                board[row][col].letter != '.') {
            char letter = board[row][col].letter;

            // Only add points if the letter is uppercase (lowercase = blanks)
            if (Character.isUpperCase(letter)) {
                colCrossPts += tiles[letter - 'A'].points;
            }

            row--;
        }

        // Now, start on the row below the square
        row = startRow + 1;

        // Calculate points formed by letters below the square
        while (board[row][col].type != SquareType.OUTSIDE &&
                board[row][col].letter != '.') {

            char letter = board[row][col].letter;

            // Only add points if the letter is uppercase (lowercase = blanks)
            if (Character.isUpperCase(letter)) {
                colCrossPts += tiles[letter - 'A'].points;
            }

            row++;
        }

        return colCrossPts;
    }

    /**
     * Inverts a board so that for each board[row][col] == invertedBoard[col][row].
     * In other words, it swaps rows and columns.
     *
     * @param   board       a 2D array containing all the data for
     *                      a Scrabble Board
     * @return              the inverted board
     */
    public Square[][] invertBoard (Square[][] board) {

        // Invert the board by changing rows to columns and vice versa,
        // so that for each Square in board,
        // board[row][col] == invertedBoard[col][row]
        Square[][] invertedBoard = new Square[numBoardRows+2][numBoardCols+2];

        // Fill through all the squares in the inverted board
        for (int row = 0; row <= numBoardRows + 1; row++) {
            for (int col = 0; col <= numBoardCols + 1; col++) {
                invertedBoard[row][col] = new Square();
                invertedBoard[row][col].type = board[col][row].type;
                invertedBoard[row][col].letter = board[col][row].letter;
                invertedBoard[row][col].row = row;
                invertedBoard[row][col].col = col;
                invertedBoard[row][col].downCrossCheck = new boolean[26];
                invertedBoard[row][col].minAcrossWordLength = 0;
            }
        }

        // Update the properties of the inverted board
        updateDownCrossChecks(invertedBoard);
        updateMinAcrossWordLength(invertedBoard);

        return invertedBoard;
    }

    /**
     * Inverts a move so that for each Square in the move, it swaps the row
     * and col. In other words, if the row and column of a square are 5 and 8
     * respectively, the row and column will become 8 and 5.
     *
     * @param   acrossMove      an ArrayList of Squares storing the squares on
     *                          which a tile has been placed for a given move
     * @return                  a move with the rows and columns swapped for
     *                          each square
     */
    public ScrabbleMove invertMove (ScrabbleMove acrossMove) {

        ScrabbleMove downMove = acrossMove;

        for (int i = 0; i < downMove.size(); i++) {
            downMove.get(i).row = acrossMove.get(i).col;
            downMove.get(i).col = acrossMove.get(i).row;
        }

        return downMove;
    }

    /**
     * Adds a move to the board by placing the appropriate tiles.
     *
     * @param   board   the state of the Scrabble board stored in a 2D Array
     * @param   move   the move containing the Squares upon which have new
     *                  tiles have been placed
     *
     */
    public void addMoveToBoard (Square[][] board, ScrabbleMove move) {

        for (int i = 0; i < move.size(); i++) {
            board[move.get(i).row][move.get(i).col] = move.get(i);
        }

        updateDownCrossChecks(board);
        updateMinAcrossWordLength(board);
    }

    /**
     * Outputs the scrabble board onto the console.
     * Only outputs the letters with heading, row numbers, and column numbers
     *
     * @param   board  the variable storing all the data for the board
     */
    public void outputBoard (Square[][] board) {
        // String storing the row header that is displayed vertically
        String rowNumHeader = "    ROW NUMBER        ";

        // Column header
        System.out.println("            COLUMN NUMBER         ");
        System.out.println("       2   4   6   8  10  12  14    ");

        // Go through all rows of the scrabble board (usually 15)
        for (int row = 1; row <= numBoardRows; row++) {

            // Output a letter if necessary of the row header
            System.out.print(rowNumHeader.charAt(row) + " ");

            // Output the row number only if it is even
            if (row%2 == 0) {

                // Output an extra space if i is only 1 digit
                if (row <= 9) {
                    System.out.print(" ");
                }

                System.out.print (row + " ");
            }
            else {
                System.out.print("   ");
            }

            // Output every letter on the board (period or . means an empty square)
            for (int col = 1; col <= numBoardCols; col++) {
                System.out.print(board[row][col].letter + " ");
            }

            System.out.println();
        }
    }


    public Square[][] createBoardCopy (Square[][] board) {
        Square[][] boardCopy = new Square[numBoardRows+2][numBoardCols+2];

        // Copy each square
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                boardCopy[row][col] = board[row][col];
            }
        }

        return boardCopy;
    }

    /**
     * Function that is called that allows the user to execute the code which
     * find the best move based on a board and a rack.
     * This function allows the user to change the tiles on the board, change
     * the tiles on the rack, find the best move, and exit.
     */
    public void runScrabble () {
        // Get the data for the board
        Square[][] board = readBoardData();
        readTestGameData(board);
        String rackStr = "ENTIREE";
        int[] rack = fillRack(rackStr);
        Scanner reader = new Scanner(System.in);

        // Loop infinitely until the user decides to exit
        while (true) {
            // Update the state of the board
            updateDownCrossChecks(board);
            updateMinAcrossWordLength(board);

            // Output the board and the rack
            outputBoard(board);
            System.out.println();
            System.out.println("RACK TILES: " + rackStr);
            System.out.println();

            // Find the best move
            ScrabbleMove bestMove = findBestMove(board, rack);

            // Output the best move
            System.out.println();
            System.out.println("BEST MOVE");
            System.out.println("Points: " + bestMove.points);

            // Only output the specifics of the move if it exists
            if (bestMove.size() > 0) {
                System.out.println("Tiles: ");
                System.out.println("Start Row: " + bestMove.get(0).row);
                System.out.println("Start Col: " + bestMove.get(0).col);

                for (int i = 0; i < bestMove.size(); i++) {
                    System.out.println(bestMove.get(i).letter
                            + " " + bestMove.get(i).row
                            + " " + bestMove.get(i).col);
                }
            }

            System.out.println();

            // Output what the best move would look like
            Square[][] newBoard = createBoardCopy(board);
            addMoveToBoard(newBoard, bestMove);
            outputBoard(newBoard);

            // Get the next user input
            while (true) {
                boolean invalidTile = false;

                // Give the user options
                String input;
                System.out.println();
                System.out.println("Enter 'b' to change a tile on the board.");
                System.out.println("Enter 'r' to change the tiles "
                        + "in the rack.");
                System.out.println("Enter 'f' to find the best move.");
                System.out.println("Enter another key to exit.");
                input = reader.next();

                // If the user decides to add a tile to the board
                if (input.equals("b") || input.equals("B")) {
                    char letter;
                    int row, col;

                    // Get input
                    System.out.println("Enter a tile's letter, row, and column"
                            + " separated by spaces:");
                    System.out.println("Ex. \"E 4 7\" indicates an 'E' "
                            + "at row 4, col 7.");

                    letter = reader.next().charAt(0);
                    row = reader.nextInt();
                    col = reader.nextInt();

                    // Ensure the letter is uppercase and the row and col are
                    // the right size
                    if ( (Character.isLetter(letter) || letter == '.')
                            && 1 <= row && row <= numBoardRows
                            && 1 <= col && col <= numBoardCols) {
                        board[row][col].letter = letter;
                    }
                    else {
                        invalidTile = true;
                    }
                }
                // If the user decides to change the tiles in the rack
                else if (input.equals("r") || input.equals("R")) {
                    System.out.println("Enter the tiles in the rack in " +
                            "uppercase letters and no spaces: ");
                    rackStr = reader.next();
                    rack = fillRack(rackStr);
                }
                // If the user decides to find the best move
                else if (input.equals("f") || input.equals("F")) {
                    System.out.println("____________________________________");
                    break;
                }
                // Exits the program
                else {
                    reader.close();
                    return;
                }

                // Output the board and the rack
                System.out.println("____________________________________");
                outputBoard(board);
                System.out.println();
                System.out.println("RACK TILES: " + rackStr);
                System.out.println();

                // Account for invalid tile input
                if (invalidTile) {
                    System.out.println("Invalid tile input");
                }
            }
        }
    }


}
