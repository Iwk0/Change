package com.change.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.change.R;
import com.change.model.Log;
import com.change.model.Rate;
import com.change.model.Stock;
import com.change.utils.Constants;
import com.change.utils.Database;
import com.change.utils.InternetConnection;
import com.change.utils.Keyboard;
import com.change.utils.XmlParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Exchange extends Fragment {

    private Activity activity;
    private AsyncTask asyncTask;
    private Database database;

    private View view;
    private TextView resultView;
    private TextView amountLabel;
    private TextView noInternetView;
    private EditText amountText;
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private ProgressBar progressBar;
    private Button exchange;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_exchange, container, false);
        activity = getActivity();
        resultView = (TextView) view.findViewById(R.id.result);
        amountLabel = (TextView) view.findViewById(R.id.amountLabel);
        noInternetView = (TextView) view.findViewById(R.id.noInternetView);
        amountText = (EditText) view.findViewById(R.id.amount);
        fromCurrencySpinner = (Spinner) view.findViewById(R.id.fromCurrencySpinner);
        toCurrencySpinner = (Spinner) view.findViewById(R.id.toCurrencySpinner);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        exchange = (Button) view.findViewById(R.id.exchange);

        database = new Database(activity);

        exchange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                calculate();
            }
        });

        noInternetView.setVisibility(View.VISIBLE);
        noInternetView.setClickable(true);
        noInternetView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (InternetConnection.isConnected(activity) || !database.getAllRates(Constants.TABLE_NAME_RATES).isEmpty()) {
                    addComponents();
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

        addComponents();
        return view;
    }

    private void addComponents() {
        ArrayList<Rate> rates = database.getAllRates(Constants.TABLE_NAME_RATES);

        if (InternetConnection.isConnected(activity) || !rates.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            noInternetView.setVisibility(View.GONE);

            setupUI(view.findViewById(R.id.exchangeLayout), activity);

            if (rates != null && !rates.isEmpty()) {
                ArrayAdapter<Rate> dataAdapter = new ArrayAdapter<>(activity,
                        android.R.layout.simple_spinner_item, rates);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                fromCurrencySpinner.setAdapter(dataAdapter);
                toCurrencySpinner.setAdapter(dataAdapter);

                setVisible();
            } else {
                asyncTask = new AsyncTask<Void, Void, ArrayList<Rate>>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected ArrayList<Rate> doInBackground(Void... voids) {
                        database.insertRate(XmlParser.parseBNBRates(activity), Constants.TABLE_NAME_RATES);
                        database.fillStockTable();

                        return database.getAllRates(Constants.TABLE_NAME_RATES);
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Rate> rates) {
                        super.onPostExecute(rates);

                        ArrayAdapter<Rate> dataAdapter = new ArrayAdapter<>(activity,
                                android.R.layout.simple_spinner_item, rates);

                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        fromCurrencySpinner.setAdapter(dataAdapter);
                        toCurrencySpinner.setAdapter(dataAdapter);

                        setVisible();
                        progressBar.setVisibility(View.GONE);
                    }
                }.execute();
            }
        }
    }

    private void calculate() {
        String amountString = amountText.getText().toString();

        if (amountString != null && !amountString.isEmpty()) {
            Rate fromRate = (Rate) fromCurrencySpinner.getSelectedItem();
            Rate toRate = (Rate) toCurrencySpinner.getSelectedItem();

            double amount = Double.valueOf(amountString);
            double result = (amount * fromRate.rate) / toRate.rate;

            resultView.setText(String.format("%.2f %s", result, toRate.code));

            Log log = new Log();
            log.currentDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            log.codeFrom = fromRate.code;
            log.codeTo = toRate.code;
            log.currencyFromAmount = amount;
            log.currencyToAmount = result;

            database.insertLog(log);

            if (!fromRate.code.equals(toRate.code)) {
                Stock stockTo = database.findStockById(Constants.TABLE_NAME_STOCKS, toRate.id);

                if (stockTo.stock - result >= 0) {
                    Stock stockFrom = database.findStockById(Constants.TABLE_NAME_STOCKS, fromRate.id);

                    database.update(Constants.TABLE_NAME_STOCKS, fromRate.code, stockFrom.stock + amount);
                    database.update(Constants.TABLE_NAME_STOCKS, toRate.code, stockTo.stock - result);
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.not_enough_in_stock),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setupUI(final View view, final Activity activity) {
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    Keyboard.hiddenKeyboard(view, activity);
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, activity);
            }
        }
    }

    private void setVisible() {
        resultView.setVisibility(View.VISIBLE);
        amountLabel.setVisibility(View.VISIBLE);
        amountText.setVisibility(View.VISIBLE);
        fromCurrencySpinner.setVisibility(View.VISIBLE);
        toCurrencySpinner.setVisibility(View.VISIBLE);
        exchange.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        if (asyncTask != null && !asyncTask.isCancelled()) {
            asyncTask.cancel(true);
        }
        super.onPause();
    }
}