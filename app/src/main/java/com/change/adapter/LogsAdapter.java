package com.change.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.change.R;
import com.change.model.Log;

import java.util.ArrayList;
import java.util.List;

public class LogsAdapter extends ArrayAdapter<Log> {

    private static class ViewHolder {
        TextView fromCurrency;
        TextView toCurrency;
    }

    private List<Log> logs;
    private Context context;

    public LogsAdapter(Context context, int logViewId, ArrayList<Log> logs) {
        super(context, logViewId);
        this.logs = logs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return logs.size();
    }

    @Override
    public Log getItem(int i) {
        return logs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return logs.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder logViewHolder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item, viewGroup, false);

            logViewHolder = new ViewHolder();
            logViewHolder.fromCurrency = (TextView) view.findViewById(R.id.firstItem);
            logViewHolder.toCurrency = (TextView) view.findViewById(R.id.secondItem);

            view.setTag(logViewHolder);
        } else {
            logViewHolder = (ViewHolder) view.getTag();
        }

        if (i % 2 == 0) {
            view.setBackgroundResource(R.color.DarkSlateGray);
        } else {
            view.setBackgroundResource(R.color.LightSlateGray);
        }

        Log log = logs.get(i);

        logViewHolder.fromCurrency.setText(String.format("%.2f %s", log.currencyFromAmount, log.codeFrom));
        logViewHolder.toCurrency.setText(String.format("%.2f %s", log.currencyToAmount, log.codeTo));

        return view;
    }
}