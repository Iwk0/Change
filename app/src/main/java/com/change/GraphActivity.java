package com.change;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class GraphActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        GraphViewSeries exampleSeries = new GraphViewSeries(new GraphView.GraphViewData[] {
                new GraphView.GraphViewData(1, 2.0d),
                new GraphView.GraphViewData(2, 1.5d),
                new GraphView.GraphViewData(3, 2.5d),
                new GraphView.GraphViewData(4, 1.0d)
        });

        GraphView graphView = new LineGraphView(
                this,
                "GraphViewDemo"
        );
        graphView.addSeries(exampleSeries);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.graph);
        layout.addView(graphView);
    }
}