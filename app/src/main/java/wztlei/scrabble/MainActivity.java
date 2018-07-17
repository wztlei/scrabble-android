package wztlei.scrabble;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class  MainActivity extends AppCompatActivity {

    ArrayList<String> boardStrings;
    HashMap<Integer, Square> boardButtonIDs;
    int lastSquareClickedID;

    /**
     * Changes the height and width of each button in the grid of Scrabble squares
     * so that they are squares and together they completely fill the device screen
     */
    protected void setButtonDimensions() {

        // Get the width of the device in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;

        // Get the table by ID
        TableLayout tableLayout = findViewById(R.id.table_scrabble_board);

        // Go through every button in the Scrabble board
        for (int row = 0; row < 15; row++) {
            TableRow tableRow = (TableRow)tableLayout.getChildAt(row);

            for (int col = 0; col < tableRow.getChildCount(); col++) {
                // Change the height and width of the button to make it a square
                View view = tableRow.getChildAt(col);
                Button square = findViewById(view.getId());
                square.getLayoutParams().height = displayWidth / 15;
                square.getLayoutParams().width = displayWidth / 15;
            }
        }
    }

    /**
     * Sets the padding of the row of buttons displaying the tiles in the rack
     * so that they are centered on the screen and the width of a square below the board.
     */
    protected void setTileRowPadding() {

        // Get the width of the device in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;

        // Get the tableRow and set its left padding
        TableLayout tableLayout = findViewById(R.id.table_scrabble_board);

        // Convert from pixels to density-independent pixels
        float pxToDPscale = getResources().getDisplayMetrics().density;
        int _20DP = (int) (20*pxToDPscale + 0.5f);

        TableRow tableRow = (TableRow)tableLayout.getChildAt(15);
        tableRow.setPadding(displayWidth/15, _20DP, _20DP, 0);
    }

    protected void readBoardStrings() {
        TextFileNames textFileNames = new TextFileNames();
        String boardFileName = textFileNames.boardFileName;

        Scanner boardDataFile = null;

        try {
            InputStream inputStream = getAssets().open(boardFileName);
            boardDataFile = new Scanner (inputStream);

        } catch (FileNotFoundException ex) {
            System.out.println("Could not open " + boardFileName);
        } catch (IOException ex) {
            System.out.println("IOException due to " + boardFileName);
        }

        boardStrings = new ArrayList<>();

        // Ensure file is open
        if (boardDataFile != null) {

            // Get all the lines in the board text file
            while (boardDataFile.hasNextLine()) {

                // Read a line from the text file
                String line = boardDataFile.nextLine();
                boardStrings.add(line);
            }
        }
    }

    /**
     * Stores all the button IDs in the Scrabble board.
     */
    @SuppressLint("UseSparseArrays")
    protected void storeButtonIDs() {

        // Get the table by ID
        TableLayout tableLayout = findViewById(R.id.table_scrabble_board);

        // Initialize the array to store all the IDs
        boardButtonIDs = new HashMap<>();

        // Go through every button in the Scrabble board
        // getChildCount() is not used since there are items
        // below the grid that should not be changed
        for (int tableRowNum = 0; tableRowNum < 15; tableRowNum++) {

            TableRow tableRow = (TableRow)tableLayout.getChildAt(tableRowNum);

            for (int tableColNum = 0; tableColNum < tableRow.getChildCount(); tableColNum++) {

                View view = tableRow.getChildAt(tableColNum);
                Square sqr = new Square();
                sqr.row = tableRowNum + 1;
                sqr.col = tableColNum + 1;
                boardButtonIDs.put(view.getId(), sqr);
            }
        }
    }

    /**
     * Sets the colours of the buttons so that they reflect the colours of a Scrabble board.
     */
    protected void setButtonColors() {

        // Get the table by ID
        TableLayout tableLayout = findViewById(R.id.table_scrabble_board);

        // Initialize the array to store all the IDs

        // Go through every button in the Scrabble board and the tiles below
        for (int tableRowNum = 0; tableRowNum < 15; tableRowNum++) {

            TableRow tableRow = (TableRow)tableLayout.getChildAt(tableRowNum);

            for (int tableColNum = 0; tableColNum < tableRow.getChildCount(); tableColNum++) {

                View view = tableRow.getChildAt(tableColNum);
                Button square = findViewById(view.getId());
                char squareChar = boardStrings.get(tableRowNum+1).charAt(tableColNum+1);

                // Change the background to the proper drawable resource
                switch (squareChar) {
                    case 'W': square.setBackgroundResource(R.drawable.triple_word_square);   break;
                    case 'w': square.setBackgroundResource(R.drawable.double_word_square);   break;
                    case 'L': square.setBackgroundResource(R.drawable.triple_letter_square); break;
                    case 'l': square.setBackgroundResource(R.drawable.double_letter_square); break;
                    case '.': square.setBackgroundResource(R.drawable.regular_square);       break;
                    case 't': square.setBackgroundResource(R.drawable.tile_square);          break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readBoardStrings();
        storeButtonIDs();
        setButtonDimensions();
        setButtonColors();

        lastSquareClickedID = 0;
    }

    /**
     * Function is called to hide the keyboard
     *
     * @param activity the activity where the keyboard needs to be hidden
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();

        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();

        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }

        imm.showSoftInput(view, 0);
    }

    /**
     * Function is called when the user clicks "Enter" to change a tile on the board
     *
     * @param view the ID of the button whose border needs to be changed to white
     */
    public void onClickEnterBoardTile(View view) {

        if (lastSquareClickedID != 0) {
            // Change the text displayed on the tile on the board
            Button boardSquare = findViewById(lastSquareClickedID);
            EditText boardEditText = findViewById(R.id.edit_text_board);
            String inputtedTileLetter = boardEditText.getText().toString();
            boardSquare.setText(inputtedTileLetter);

            // Hide the keyboard
            hideKeyboard(this);

            // Change the background of the button
            drawButtonBlackBorder(lastSquareClickedID);
        }
    }

    /**
     * Changes the border of the button to white
     *
     * @param buttonID the ID of the button whose border needs to be changed to white
     */
    protected void drawButtonWhiteBorder(int buttonID) {

        Button boardSquare = findViewById(buttonID);

        // Get the row and col number of the button pressed
        int rowNum = boardButtonIDs.get(buttonID).row;
        int colNum = boardButtonIDs.get(buttonID).col;
        char squareChar = boardStrings.get(rowNum).charAt(colNum);

        // Use the tile background if the button has a tile on it
        if (boardSquare.getText().length() > 0 && !boardSquare.getText().equals(" ")) {
            boardSquare.setBackgroundResource(R.drawable.tile_square);
            return;
        }

        // Change the background of the current button to the proper drawable resource
        // This will cause the button to have a black border
        switch (squareChar) {
            case 'W':
                boardSquare.setBackgroundResource(R.drawable.triple_word_square);
                break;
            case 'w':
                boardSquare.setBackgroundResource(R.drawable.double_word_square);
                break;
            case 'L':
                boardSquare.setBackgroundResource(R.drawable.triple_letter_square);
                break;
            case 'l':
                boardSquare.setBackgroundResource(R.drawable.double_letter_square);
                break;
            case '.':
                boardSquare.setBackgroundResource(R.drawable.regular_square);
                break;
            case 't':
                boardSquare.setBackgroundResource(R.drawable.tile_square);
                break;
        }
    }

    /**
     * Changes the border of the button to black
     *
     * @param buttonID the ID of the button whose border needs to be changed to black
     */
    protected void drawButtonBlackBorder(int buttonID) {

        Button boardSquare = findViewById(buttonID);

        // Get the row and col number of the button pressed
        int rowNum = boardButtonIDs.get(buttonID).row;
        int colNum = boardButtonIDs.get(buttonID).col;
        char squareChar = boardStrings.get(rowNum).charAt(colNum);

        System.out.println(boardSquare.getText());

        // Use the tile background if the button has a tile on it
        if (boardSquare.getText().length() > 0 && !boardSquare.getText().equals(" ")) {
            boardSquare.setBackgroundResource(R.drawable.tile_square_pressed);
            return;
        }

        // Change the background of the current button to the proper drawable resource
        // This will cause the button to have a black border
        switch (squareChar) {
            case 'W':
                boardSquare.setBackgroundResource(R.drawable.triple_word_square_pressed);
                break;
            case 'w':
                boardSquare.setBackgroundResource(R.drawable.double_word_square_pressed);
                break;
            case 'L':
                boardSquare.setBackgroundResource(R.drawable.triple_letter_square_pressed);
                break;
            case 'l':
                boardSquare.setBackgroundResource(R.drawable.double_letter_square_pressed);
                break;
            case '.':
                boardSquare.setBackgroundResource(R.drawable.regular_square_pressed);
                break;
            case 't':
                boardSquare.setBackgroundResource(R.drawable.tile_square_pressed);
                break;
        }
    }

    /**
     * Function is called when a button in the Scrabble board is pressed
     *
     * @param view the view of the button pressed
     */
    public void onClickBoardButton (View view) {

        int currButtonID = view.getId();
        Button boardSquare = findViewById(currButtonID);

        showKeyboard(this);

        // Set the text of the input text box (to change a tile on the board)
        // to the text currently on the button
        EditText boardEditText = findViewById(R.id.edit_text_board);
        boardEditText.setText(boardSquare.getText());
        boardEditText.requestFocus();
        boardEditText.selectAll();

        // Change the borders of the last button pressed
        if (lastSquareClickedID != 0) {
            drawButtonWhiteBorder(lastSquareClickedID);
        }

        // Change the borders of the current button pressed
        drawButtonBlackBorder(view.getId());

        // Update the variable storing the ID of the last button pressed
        lastSquareClickedID = view.getId();
    }

    /**
     * Function is called when the user clicks "Enter" to change the tiles in the rack
     *
     * @param view the ID of the button whose border needs to be changed to white
     */
    public void onClickEnterRackTiles(View view) {
        hideKeyboard(this);
    }
}
