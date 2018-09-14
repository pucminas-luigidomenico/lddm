package pucminas.computacao.luigi.graphics;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {

    private GraphView graph;
    private Button btnShowGraph;
    private EditText valueAEditText;
    private EditText valueBEditText;
    private EditText valueCEditText;
    private EditText valueNEditText;

    // Icon error for EditText
    private Drawable iconError;

    // Private class to watch input texts and show error
    private class EditTextWatcher implements TextWatcher {

        // EditText which is being watched
        private EditText editText;

        /**
         * Constructor for EditTextWatcher
         * @param editText EditText
         */
        public EditTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        /**
         * If edit text is empty, show error icon
         * @param editable
         */
        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() == 0) { // EditText empty, put icon error
                // Set icon error
                editText.setCompoundDrawables(null, null, iconError, null);

                // Disable the show graph button
                btnShowGraph.setEnabled(false);
            } else { // EditText has something
                // Remove icon error
                editText.setCompoundDrawables(null, null, null, null);
            }

            // If all of EditTexts are not empty, enable the button to show graph
            int lengthA = valueAEditText.getText().toString().length();
            int lengthB = valueBEditText.getText().toString().length();
            int lengthC = valueCEditText.getText().toString().length();
            int lengthN = valueNEditText.getText().toString().length();
            if (lengthA != 0 && lengthB != 0 && lengthC != 0 && lengthN !=0) {
                btnShowGraph.setEnabled(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Config iconError
        iconError = ContextCompat.getDrawable(this, R.drawable.ic_error_black_24dp);
        iconError.setBounds(0, 0, iconError.getIntrinsicWidth(), iconError.getIntrinsicHeight());

        graph = (GraphView) findViewById(R.id.graph);
        valueAEditText = (EditText) findViewById(R.id.valueAEditText);
        valueBEditText = (EditText) findViewById(R.id.valueBEditText);
        valueCEditText = (EditText) findViewById(R.id.valueCEditText);
        valueNEditText = (EditText) findViewById(R.id.valueNEditText);
        btnShowGraph = (Button) findViewById(R.id.btnShowGraph);

        // Disable the btnShowGraph
        btnShowGraph.setEnabled(false);

        // Set text watcher to EditTexts
        valueAEditText.addTextChangedListener(new EditTextWatcher(valueAEditText));
        valueBEditText.addTextChangedListener(new EditTextWatcher(valueBEditText));
        valueCEditText.addTextChangedListener(new EditTextWatcher(valueCEditText));
        valueNEditText.addTextChangedListener(new EditTextWatcher(valueNEditText));

        // Set listener to button click event
        btnShowGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGraph();
            }
        });
    }

    /**
     * Create graph accordingly with values A, B, C and N of EditTexts.
     */
    private void createGraph() {
        // Hide keyboard
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(valueAEditText.getWindowToken(), 0);

        // Reset old data
        graph.removeAllSeries();

        // Graph: y = aX² + bX + c
        // Variables to graph equation
        double a = Double.parseDouble(valueAEditText.getText().toString());
        double b = Double.parseDouble(valueBEditText.getText().toString());
        double c = Double.parseDouble(valueCEditText.getText().toString());

        // Create points from -n to n
        int n = Integer.parseInt(valueNEditText.getText().toString());

        // Set min and max values of X
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-n);
        graph.getViewport().setMaxX(n);

        // Set scalable and scrollable
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        // Precision
        int precision = 10;

        // Max DataPoints
        int maxDataPoints = n * 2 * (precision + 1);

        // LineGraphSeries of DataPoints
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        // Get coordinates and put into array of DataPoints
        double x;
        double[] coordinates;

        for (int i = -n; i < n; i++) {
            // Calculate the rest of coordinates (accordingly to precision)
            for (int j = 0; j < precision; j++) {
                x = i + ((1.0 / precision) * j);
                coordinates = calcY(x, a, b, c);

                series.appendData(new DataPoint(coordinates[0], coordinates[1]), false, maxDataPoints);
            }
        }

        // Calculate the last pair of coordinates (for N = 2, last value: X = 2)
        x = n;
        coordinates = calcY(x, a, b, c);
        series.appendData(new DataPoint(coordinates[0], coordinates[1]), false, maxDataPoints);


        // Create graph
        graph.addSeries(series);
    }

    /**
     * Calculate value of coordinate Y, where Y = ax² + bx + c
     * @param x Value of coordinate X
     * @param a Value a on equation
     * @param b Value b on equation
     * @param c Valeu c on equation
     * @return coordinates X and Y, in array
     */
    private double[] calcY(double x, double a, double b, double c) {
        double[] coordinates = new double[2];

        // Coordinate X
        coordinates[0] = x;

        // Coordinate Y = aX² + bX + c
        coordinates[1] = (a * Math.pow(coordinates[0], 2)) + (b * coordinates[0]) + c;

        return coordinates;
    }
}
