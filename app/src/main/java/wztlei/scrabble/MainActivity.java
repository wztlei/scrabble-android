package wztlei.scrabble;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import wztlei.scrabble.TextFileNames;


public class  MainActivity extends AppCompatActivity {

    /**
     * Changes the height and width of each button in the grid of Scrabble squares
     * so that they are squares and together they completely fill the device screen
     */
    protected void setButtonDimensions(){

        // Get the width of the device in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;

        // Get the table by ID
        TableLayout tableLayout = findViewById(R.id.table_scrabble_board);

        // Go through every button in the Table
        for (int row = 0; row < tableLayout.getChildCount(); row++) {
            TableRow tableRow = (TableRow)tableLayout.getChildAt(row);

            for (int col = 0; col < tableRow.getChildCount(); col++) {

                // Change the height and width of the button to make it a square
                View view = tableRow.getChildAt(col);
                Button square = findViewById(view.getId());
                square.getLayoutParams().height = displayWidth/15;
                square.getLayoutParams().width = displayWidth/15;
            }
        }
    }

    /**
     *
     */
    protected void setButtonColors(){

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


        ArrayList<String> boardDataStrings = new ArrayList<>();

        // Ensure file is open
        if (boardDataFile != null) {

            // Get all the lines in the board text file
            while (boardDataFile.hasNextLine()) {

                // Read a line from the text file
                String line = boardDataFile.nextLine();
                boardDataStrings.add(line);
                System.out.println(line);
            }
        }

        // Get the table by ID
        TableLayout tableLayout = findViewById(R.id.table_scrabble_board);

        // Go through every button in the Table
        for (int tableRowNum = 0; tableRowNum < tableLayout.getChildCount(); tableRowNum++) {

            TableRow tableRow = (TableRow)tableLayout.getChildAt(tableRowNum);

            for (int tableColNum = 0; tableColNum < tableRow.getChildCount(); tableColNum++) {

                View view = tableRow.getChildAt(tableColNum);
                Button square = findViewById(view.getId());


                // Change the background to the proper drawable resource
                switch (boardDataStrings.get(tableRowNum+1).charAt(tableColNum+1)) {
                    case 'W': square.setBackgroundResource(R.drawable.triple_word_square);   break;
                    case 'w': square.setBackgroundResource(R.drawable.double_word_square);   break;
                    case 'L': square.setBackgroundResource(R.drawable.triple_letter_square); break;
                    case 'l': square.setBackgroundResource(R.drawable.double_letter_square); break;
                    case '.': square.setBackgroundResource(R.drawable.regular_square);       break;
                }
                square.setText(" ");
                if (tableRowNum == 7 && (tableColNum == 6 || tableColNum == 7 || tableColNum == 8 || tableColNum == 9|| tableColNum == 10)){
                    square.setBackgroundResource(R.drawable.tile_square);
                    square.setText("X");
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonDimensions();
        setButtonColors();
    }
}
