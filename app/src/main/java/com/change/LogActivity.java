package com.change;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.change.model.Log;
import com.change.utils.Constants;

public class LogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        TextView date = (TextView) findViewById(R.id.date);
        TextView currencyFromAmount = (TextView) findViewById(R.id.currencyFromAmount);
        TextView codeFrom = (TextView) findViewById(R.id.codeFrom);
        TextView currencyToAmount = (TextView) findViewById(R.id.currencyToAmount);
        TextView codeTo = (TextView) findViewById(R.id.codeTo);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log log = extras.getParcelable(Constants.LOG);

            date.setText(log.currentDay);
            currencyFromAmount.setText(String.format("%.2f", log.currencyFromAmount));
            codeFrom.setText(log.codeFrom);
            currencyToAmount.setText(String.format("%.2f", log.currencyToAmount));
            codeTo.setText(log.codeTo);
        }
    }
}