package com.change.utils;

import android.content.Context;
import android.util.Log;

import com.change.model.Movement;
import com.change.model.Rate;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XmlParser {

    public static ArrayList<Rate> parseBNBRates(Context context) {
        ArrayList<Rate> rates = new ArrayList<>();

        try {
            URL url = new URL("http://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERForeignCurrencies/index.htm?download=xml&search=&lang=BG");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();
            parser.setInput(conn.getInputStream(), null);

            Rate rate = null;
            String curText = null;
            String currentDay = null;
            int event = parser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase(Constants.ROW)) {
                            if (rate != null) {
                                rates.add(rate);
                            }

                            rate = new Rate();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        curText = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase(Constants.NAME)) {
                            rate.name = curText;
                        } else if (tagName.equalsIgnoreCase(Constants.CODE)) {
                            rate.code = curText;
                        } else if (tagName.equalsIgnoreCase(Constants.RATIO)) {
                            rate.ratio = isNumeric(curText) ? Integer.valueOf(curText) : 0;
                        } else if (tagName.equalsIgnoreCase(Constants.REVERSE_RATE)) {
                            rate.reverseRate = isNumeric(curText) ? Double.valueOf(curText) : 0;
                        } else if (tagName.equalsIgnoreCase(Constants.RATE)) {
                            rate.rate = isNumeric(curText) ? Double.valueOf(curText) : 0;
                        } else if (tagName.equalsIgnoreCase(Constants.CURRENT_DAY)) {
                            currentDay = curText;
                        }
                        break;
                }

                event = parser.next();
            }

            rates.remove(0);

            try {
                currencyStatus(context, rates, currentDay);
            } catch (ParseException e) {
                Log.e("ParseException", e.getMessage());
            }

            fixedRates(rates);
        } catch (MalformedURLException e) {
            Log.e("MalformedURLException", e.getMessage());
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        } catch (XmlPullParserException e) {
            Log.e("XmlPullParserException", e.getMessage());
        }

        return rates;
    }

    private static void fixedRates(ArrayList<Rate> rates) {
        for (int i = FixedRates.RATES.length - 1; i >=0; i--) {
            Rate rate = new Rate();

            rate.name = (String) FixedRates.RATES[i][0];
            rate.code = (String) FixedRates.RATES[i][1];
            rate.ratio = (Integer) FixedRates.RATES[i][2];
            rate.rate = (Double) FixedRates.RATES[i][3];
            rate.reverseRate = (Double) FixedRates.RATES[i][4];
            rate.fixed = true;

            rates.add(0, rate);
        }
    }

    private static void currencyStatus(Context context, List<Rate> rates, String currentDay) throws ParseException {
        Database database = new Database(context);
        Movement movement = database.findLastRow();

        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = simpleFormat.parse(currentDay);

        simpleFormat.applyPattern("yyyy-MM-dd");
        String formattedDate = simpleFormat.format(date);

        if (movement != null) {
            final int SIZE = movement.rates.size();

            for (int i = 0; i < SIZE; i++) {
                Rate tempRate = rates.get(i);
                Rate tempMovementRate = movement.rates.get(i);

                if (tempMovementRate.rate > tempRate.rate) {
                    tempRate.movement = Rate.DOWN;
                } else if (tempMovementRate.rate < tempRate.rate) {
                    tempRate.movement = Rate.UP;
                } else if (!movement.date.equals(formattedDate) && tempMovementRate.rate == tempRate.rate) {
                    tempRate.movement = Rate.EQUAL;
                } else {
                    tempRate.movement = tempMovementRate.movement;
                }

                rates.set(i, tempRate);
            }

            if (!movement.date.equals(formattedDate)) {
                movement = new Movement();
                movement.date = formattedDate;
                movement.rates = rates;

                database.insertMovement(movement);
                database.insertMonthlyGraph(movement, Constants.TABLE_NAME_MONTHLY_GRAPH);
            }
        } else {
            movement = new Movement();
            movement.date = formattedDate;
            movement.rates = rates;

            database.insertMovement(movement);
            database.insertMonthlyGraph(movement, Constants.TABLE_NAME_MONTHLY_GRAPH);
        }
    }

    private static boolean isNumeric(String value) {
        return value.matches("-?\\d+(\\.\\d+)?");
    }
}