package com.change;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.change.model.MonthlyGraph;
import com.change.utils.Constants;
import com.change.utils.Database;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GraphActivity extends Activity {

    private static final SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Database database = new Database(this);
            String code = extras.getString(Constants.CODE);
            String startDate = getFirstDateOfCurrentMonth();
            String endDate = simpleFormat.format(new Date());

            ArrayList<MonthlyGraph> monthlyGraphs = database.getAllMonthlyGraphs(
                    Constants.TABLE_NAME_MONTHLY_GRAPH,
                    code,
                    startDate,
                    endDate);

            int SIZE = monthlyGraphs.size();

            GraphView.GraphViewData[] graphViewData = new GraphView.GraphViewData[SIZE];

            int day = 0;
            for (int i = 0; i < SIZE; i++) {
                MonthlyGraph monthlyGraph = monthlyGraphs.get(i);

                try {
                    day = getDay(monthlyGraph.date);
                } catch (ParseException e) {
                    Log.e("ParseException", e.getMessage());
                }

                graphViewData[i] = new GraphView.GraphViewData(day, monthlyGraph.rate);
            }

            GraphViewSeries monthlySeries = new GraphViewSeries(graphViewData);

            GraphView graphView = new LineGraphView(
                    this,
                    extras.getString(Constants.RATE_NAME)
            );

            double[] maxAndMin = database.findMaxAndMin(
                    Constants.TABLE_NAME_MONTHLY_GRAPH,
                    code,
                    startDate,
                    endDate);

            graphView.setManualYAxisBounds(maxAndMin[0], maxAndMin[1]);
            graphView.addSeries(monthlySeries);

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.graph);
            layout.addView(graphView);
        }
    }

    private String getFirstDateOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));

        return simpleFormat.format(cal.getTime());
    }

    private int getDay(String date) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(simpleFormat.parse(date));

        return cal.get(Calendar.DAY_OF_MONTH);
    }
}