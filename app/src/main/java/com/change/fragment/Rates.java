package com.change.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.change.R;
import com.change.utils.ExchangeRatesBNB;
import com.change.utils.InternetConnection;

public class Rates extends Fragment {

    private Activity activity;

    private ProgressBar progressBar;
    private TextView noInternetView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rates, container, false);
        activity = getActivity();
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        if (InternetConnection.isConnected(activity)) {
            ExchangeRatesBNB exchangeRatesBNB = new ExchangeRatesBNB(activity, progressBar);
            exchangeRatesBNB.execute();
        } else {
            noInternetView = (TextView) view.findViewById(R.id.noInternetView);
            noInternetView.setVisibility(View.VISIBLE);
            noInternetView.setClickable(true);
            noInternetView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (InternetConnection.isConnected(activity)) {
                        ExchangeRatesBNB exchangeRatesBNB = new ExchangeRatesBNB(activity, progressBar);
                        exchangeRatesBNB.execute();
                        noInternetView.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        noInternetView.setVisibility(View.GONE);

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                noInternetView.setVisibility(View.VISIBLE);
                            }
                        }, 2000);
                    }
                }
            });
        }

        return view;
    }
}