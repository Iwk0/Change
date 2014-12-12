package com.change.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.change.LogActivity;
import com.change.R;
import com.change.adapter.LogsAdapter;
import com.change.model.Log;
import com.change.utils.Constants;
import com.change.utils.Database;

public class Logs extends Fragment {

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs, container, false);
        activity = getActivity();

        Database database = new Database(activity);

        ListView listView = (ListView) view.findViewById(R.id.stocksList);
        listView.addHeaderView(View.inflate(activity, R.layout.header_logs, null), null, false);
        listView.setAdapter(new LogsAdapter(activity, R.layout.item, database.getAllLogs(Constants.TABLE_NAME_LOGS)));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, LogActivity.class);
                intent.putExtra(Constants.LOG, (Log) adapterView.getItemAtPosition(i));

                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        return view;
    }
}