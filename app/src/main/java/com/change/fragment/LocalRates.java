package com.change.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.change.R;
import com.change.adapter.RateAdapter;
import com.change.model.Rate;
import com.change.utils.Constants;
import com.change.utils.Database;

import java.util.ArrayList;

public class LocalRates extends Fragment {

    private AsyncTask asyncTask;
    private Activity activity;
    private RateAdapter rateAdapter;
    private Database database;

    private Dialog dialog;
    private EditText ratioText;
    private EditText rateText;
    private EditText reverseRateText;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_rates, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        activity = getActivity();
        database = new Database(activity);

        asyncTask = new AsyncTask<Void, Void, ArrayList<Rate>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected ArrayList<Rate> doInBackground(Void... voids) {
                ArrayList<Rate> rates;

                do {
                    rates = database.getAllRates(Constants.TABLE_NAME_RATES);
                } while (rates.isEmpty());

                return rates;
            }

            @Override
            protected void onPostExecute(ArrayList<Rate> rates) {
                super.onPostExecute(rates);

                rateAdapter = new RateAdapter(activity, R.layout.rate_item, rates);

                ListView listView = (ListView) activity.findViewById(R.id.ratesList);
                listView.addHeaderView(View.inflate(activity, R.layout.header_rates, null), null, false);
                listView.setAdapter(rateAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.dialog_local_rates_edit);
                        dialog.setTitle(activity.getResources().getString(R.string.edit));

                        ratioText = (EditText) dialog.findViewById(R.id.ratio);
                        rateText = (EditText) dialog.findViewById(R.id.rate);
                        reverseRateText = (EditText) dialog.findViewById(R.id.reverseRate);

                        final Rate rate = (Rate) adapterView.getItemAtPosition(i);

                        ratioText.setText(String.valueOf(rate.ratio));
                        rateText.setText(String.valueOf(rate.rate));
                        reverseRateText.setText(String.valueOf(rate.reverseRate));

                        dialog.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                String ratioAsString = ratioText.getText().toString();
                                String rateAsString = rateText.getText().toString();
                                String reverseRateAsString = reverseRateText.getText().toString();

                                if (ratioAsString.isEmpty() || rateAsString.isEmpty() || reverseRateAsString.isEmpty()) {
                                    Toast.makeText(activity, activity.getResources().getString(R.string.empty_fields),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    rate.ratio = Integer.valueOf(ratioAsString);
                                    rate.rate = Double.valueOf(rateAsString);
                                    rate.reverseRate = Double.valueOf(reverseRateAsString);

                                    database.update(rate, Constants.TABLE_NAME_RATES, rate.id);

                                    rateAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            }
                        });

                        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });

                progressBar.setVisibility(View.GONE);
            }
        }.execute();

        return view;
    }

    @Override
    public void onPause() {
        if (asyncTask != null && !asyncTask.isCancelled()) {
            asyncTask.cancel(true);
        }
        super.onPause();
    }
}