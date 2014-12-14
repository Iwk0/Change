package com.change;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.change.model.MonthlyGraph;
import com.change.utils.Constants;
import com.change.utils.Database;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

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
            ArrayList<MonthlyGraph> monthlyGraphs = new Database(this).getAllMonthlyGraphs(
                    Constants.TABLE_NAME_MONTHLY_GRAPH,
                    extras.getString(Constants.CODE),
                    getFirstDateOfCurrentMonth(),
                    simpleFormat.format(new Date()));

            int SIZE = monthlyGraphs.size();

            GraphView.GraphViewData[] graphViewData = new GraphView.GraphViewData[SIZE];

            for (int i = 0; i < SIZE; i++) {
                graphViewData[i] = new GraphView.GraphViewData(i + 1, monthlyGraphs.get(i).rate);
            }

            GraphViewSeries monthlySeries = new GraphViewSeries(graphViewData);

            GraphView graphView = new LineGraphView(
                    this,
                    extras.getString(Constants.RATE_NAME)
            );//TODO да намеря максимум и минимум и да направя на интервали първото число
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
}