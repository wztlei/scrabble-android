package wztlei.scrabble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;


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
        TableLayout tableLayout = (TableLayout) findViewById(R.id.table_scrabble_board);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonDimensions();
    }
}
