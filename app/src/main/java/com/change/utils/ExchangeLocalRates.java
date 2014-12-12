package com.change.utils;

import android.content.Context;
import android.os.AsyncTask;

public class ExchangeLocalRates extends AsyncTask<Void, Void, Void> {

    private Context context;

    public ExchangeLocalRates(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Database database = new Database(context);
        database.insertRate(XmlParser.parseBNBRates(context), Constants.TABLE_NAME_RATES);
        database.fillStockTable();

        return null;
    }
}