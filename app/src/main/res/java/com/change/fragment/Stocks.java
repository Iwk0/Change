package java.com.change.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.change.R;
import com.change.adapter.StocksAdapter;
import com.change.model.Stock;
import com.change.utils.Constants;
import com.change.utils.Database;

public class Stocks extends Fragment {

    private Activity activity;
    private Dialog dialog;
    private EditText stockText;

    private StocksAdapter stocksAdapter;
    private Stock stock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocks, container, false);

        activity = getActivity();

        Database database = new Database(activity);

        stocksAdapter = new StocksAdapter(activity, R.layout.item,
                database.getAllStocks(Constants.TABLE_NAME_STOCKS));

        ListView listView = (ListView) view.findViewById(R.id.stocksList);
        listView.addHeaderView(View.inflate(activity, R.layout.header_stocks, null), null, false);
        listView.setAdapter(stocksAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog = new Dialog(activity);
                dialog.setContentView(R.layout.dialog_stocks_edit);
                dialog.setTitle(activity.getResources().getString(R.string.edit));

                stockText = (EditText) dialog.findViewById(R.id.stock);

                stock = (Stock) adapterView.getItemAtPosition(i);

                stockText.setText(String.format("%.2f", stock.stock));

                dialog.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String stockAsString = stockText.getText().toString();

                        if (stockAsString.isEmpty()) {
                            Toast.makeText(activity, activity.getResources().getString(R.string.empty_fields),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            stock.stock = Double.valueOf(stockAsString);

                            new Database(activity).update(Constants.TABLE_NAME_STOCKS, stock.code, stock.stock);

                            stocksAdapter.notifyDataSetChanged();
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

        return view;
    }
}