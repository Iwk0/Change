package com.change;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.change.model.Rate;
import com.change.utils.Constants;

public class RateActivity extends Activity {

    private Rate rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        TextView nameView = (TextView) findViewById(R.id.name);
        TextView codeView = (TextView) findViewById(R.id.code);
        TextView currencyView = (TextView) findViewById(R.id.currency);
        TextView rateView = (TextView) findViewById(R.id.rate);
        TextView reverseRateView = (TextView) findViewById(R.id.reverseRate);

        findViewById(R.id.graphButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
                intent.putExtra(Constants.CODE, rate.code);
                intent.putExtra(Constants.RATE_NAME, rate.name);
                startActivity(intent);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            rate = extras.getParcelable(Constants.RATE);

            nameView.setText(rate.name);
            codeView.setText(rate.code);
            currencyView.setText(String.valueOf(rate.ratio));
            rateView.setText(String.valueOf(rate.rate));
            reverseRateView.setText(String.valueOf(rate.reverseRate));
        }
    }
}