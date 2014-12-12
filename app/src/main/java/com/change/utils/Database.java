package com.change.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.change.model.Log;
import com.change.model.Movement;
import com.change.model.Rate;
import com.change.model.Stock;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    public Database(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String createTableRates = String.format(Constants.TABLE_RATES,
                Constants.TABLE_NAME_RATES,
                Constants.ID,
                Constants.RATE_NAME,
                Constants.CODE,
                Constants.RATIO,
                Constants.RATE,
                Constants.REVERSE_RATE,
                Constants.MOVEMENT);

        String createTableTransactions = String.format(Constants.TABLE_TRANSACTIONS,
                Constants.TABLE_NAME_LOGS,
                Constants.ID,
                Constants.DATE,
                Constants.CURRENCY_FROM_AMOUNT,
                Constants.CODE_FROM,
                Constants.CURRENCY_TO_AMOUNT,
                Constants.CODE_TO);

        String createTableStock = String.format(Constants.TABLE_STOCKS,
                Constants.TABLE_NAME_STOCKS,
                Constants.ID,
                Constants.CODE,
                Constants.STOCK);

        String createTableMovement = String.format(Constants.TABLE_MOVEMENT,
                Constants.TABLE_NAME_MOVEMENT,
                Constants.ID,
                Constants.DATE,
                Constants.RATES);

        database.execSQL(createTableRates);
        database.execSQL(createTableTransactions);
        database.execSQL(createTableStock);
        database.execSQL(createTableMovement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_RATES);
        database.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_LOGS);
        database.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_STOCKS);
        database.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_MOVEMENT);
        onCreate(database);
    }

    public void fillStockTable() {
        getWritableDatabase().execSQL("INSERT INTO stocks (CODE, stock) SELECT CODE as CODE, 5000 as stock FROM rates");
    }

    public void insertLog(Log log) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.DATE, log.currentDay);
        values.put(Constants.CURRENCY_FROM_AMOUNT, log.currencyFromAmount);
        values.put(Constants.CODE_FROM, log.codeFrom);
        values.put(Constants.CURRENCY_TO_AMOUNT, log.currencyToAmount);
        values.put(Constants.CODE_TO, log.codeTo);

        db.insert(Constants.TABLE_NAME_LOGS, null, values);
        db.close();
    }

    public void insertRate(List<Rate> rates, String table) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement insertQuery = db.compileStatement(
                String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?);",
                table,
                Constants.RATE_NAME,
                Constants.CODE,
                Constants.RATIO,
                Constants.RATE,
                Constants.REVERSE_RATE,
                Constants.MOVEMENT));

        db.beginTransaction();

        try {
            for (Rate rate : rates) {
                insertQuery.bindString(1, rate.name);
                insertQuery.bindString(2, rate.code);
                insertQuery.bindLong(3, rate.ratio);
                insertQuery.bindDouble(4, rate.rate);
                insertQuery.bindDouble(5, rate.reverseRate);
                insertQuery.bindString(6, rate.movement);
                insertQuery.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            android.util.Log.e("Exception", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void insertMovement(Movement movement) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        Gson gson = new Gson();

        values.put(Constants.DATE, movement.date);
        values.put(Constants.RATES, gson.toJson(movement.rates));

        db.insert(Constants.TABLE_NAME_MOVEMENT, null, values);
        db.close();
    }

    public int update(Rate rate, String table, int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.RATE_NAME, rate.name);
        values.put(Constants.CODE, rate.code);
        values.put(Constants.RATIO, rate.ratio);
        values.put(Constants.RATE, rate.rate);
        values.put(Constants.REVERSE_RATE, rate.reverseRate);

        return db.update(table, values, Constants.ID + " = ?", new String[] { String.valueOf(id) });
    }

    public int update(String table, String code, double value) {
        ContentValues values = new ContentValues();

        values.put(Constants.STOCK, value);

        return getWritableDatabase().update(table, values, Constants.CODE + " = ?", new String[]{code});
    }

    public Movement findLastRow() {
        Cursor cursor = getWritableDatabase().
                rawQuery(String.format("SELECT * FROM %s ORDER BY id DESC LIMIT 1;",
                        Constants.TABLE_NAME_MOVEMENT), null);

        Movement movement = null;
        if (cursor.moveToFirst()) {
            Gson gson = new Gson();
            movement = new Movement();

            do {
                movement.id = cursor.getInt(0);
                movement.date = cursor.getString(1);
                String json = cursor.getString(2);

                Type type = new TypeToken<List<Rate>>() {
                }.getType();
                List<Rate> rates = gson.fromJson(json, type);

                for (Rate rate : rates) {
                    Rate temp = new Rate();

                    temp.id = rate.id;
                    temp.name = rate.name;
                    temp.code = rate.code;
                    temp.rate = rate.rate;
                    temp.ratio = rate.ratio;
                    temp.reverseRate = rate.reverseRate;
                    temp.movement = rate.movement;

                    movement.rates.add(temp);
                }
            } while (cursor.moveToNext());
        }

        return movement;
    }

    public Stock findStockById(String table, int code) {
        Cursor cursor = getWritableDatabase().
                rawQuery(String.format("SELECT * FROM %s where id = %d", table, code), null);

        Stock stock = new Stock();
        if (cursor.moveToFirst()) {
            do {
                stock.id = cursor.getInt(0);
                stock.code = cursor.getString(1);
                stock.stock = cursor.getDouble(2);
            } while (cursor.moveToNext());
        }

        return stock;
    }

    public ArrayList<Rate> getAllRates(String table) {
        ArrayList<Rate> rates = new ArrayList<>();

        Cursor cursor = getWritableDatabase().
                rawQuery(String.format("SELECT * FROM %s", table), null);

        if (cursor.moveToFirst()) {
            do {
                Rate rate = new Rate();
                rate.id = cursor.getInt(0);
                rate.name = cursor.getString(1);
                rate.code = cursor.getString(2);
                rate.ratio = cursor.getInt(3);
                rate.rate = cursor.getDouble(4);
                rate.reverseRate = cursor.getDouble(5);

                rates.add(rate);
            } while (cursor.moveToNext());
        }

        return rates;
    }

    public ArrayList<Log> getAllLogs(String table) {
        ArrayList<Log> logs = new ArrayList<>();

        Cursor cursor = getWritableDatabase().
                rawQuery(String.format("SELECT * FROM %s order by date DESC", table), null);

        if (cursor.moveToFirst()) {
            do {
                Log log = new Log();
                log.id = cursor.getInt(0);
                log.currentDay = cursor.getString(1);
                log.currencyFromAmount = cursor.getDouble(2);
                log.codeFrom = cursor.getString(3);
                log.currencyToAmount = cursor.getDouble(4);
                log.codeTo = cursor.getString(5);

                logs.add(log);
            } while (cursor.moveToNext());
        }

        return logs;
    }

    public ArrayList<Stock> getAllStocks(String table) {
        ArrayList<Stock> stocks = new ArrayList<>();

        Cursor cursor = getWritableDatabase().
                rawQuery(String.format("SELECT * FROM %s", table), null);

        if (cursor.moveToFirst()) {
            do {
                Stock stock = new Stock();
                stock.id = cursor.getInt(0);
                stock.code = cursor.getString(1);
                stock.stock = cursor.getDouble(2);

                stocks.add(stock);
            } while (cursor.moveToNext());
        }

        return stocks;
    }
}