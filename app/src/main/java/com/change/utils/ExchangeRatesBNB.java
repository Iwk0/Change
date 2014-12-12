package com.change.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.change.R;
import com.change.RateActivity;
import com.change.adapter.RateAdapter;
import com.change.model.Rate;

import java.util.ArrayList;

public class ExchangeRatesBNB extends AsyncTask<String, Void, ArrayList<Rate>>  {

    private Activity activity;
    private ProgressBar progressBar;

    public ExchangeRatesBNB(Activity activity, ProgressBar progressBar) {
        this.activity = activity;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<Rate> doInBackground(String... strings) {
        return XmlParser.parseBNBRates(activity);
    }

    @Override
    protected void onPostExecute(ArrayList<Rate> rates) {
        super.onPostExecute(rates);

        ListView listView = (ListView) activity.findViewById(R.id.ratesList);
        listView.addHeaderView(View.inflate(activity, R.layout.header_rates, null), null, false);
        listView.setAdapter(new RateAdapter(activity, R.layout.rate_item, rates));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, RateActivity.class);
                intent.putExtra(Constants.RATE, (Rate) adapterView.getItemAtPosition(i));
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        progressBar.setVisibility(View.GONE);
    }
}