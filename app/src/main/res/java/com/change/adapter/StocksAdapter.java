package java.com.change.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.change.R;
import com.change.model.Stock;

import java.util.ArrayList;
import java.util.List;

public class StocksAdapter extends ArrayAdapter<Stock> {

    private static class ViewHolder {
        TextView firstItem;
        TextView secondItem;
    }

    private List<Stock> stocks;
    private Context context;

    public StocksAdapter(Context context, int stocksViewId, ArrayList<Stock> stocks) {
        super(context, stocksViewId);
        this.stocks = stocks;
        this.context = context;
    }

    @Override
    public int getCount() {
        return stocks.size();
    }

    @Override
    public Stock getItem(int i) {
        return stocks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return stocks.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder stockViewHolder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item, viewGroup, false);

            stockViewHolder = new ViewHolder();
            stockViewHolder.firstItem = (TextView) view.findViewById(R.id.firstItem);
            stockViewHolder.secondItem = (TextView) view.findViewById(R.id.secondItem);

            view.setTag(stockViewHolder);
        } else {
            stockViewHolder = (ViewHolder) view.getTag();
        }

        if (i % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.DarkSlateGray));
        } else {
            view.setBackgroundColor(view.getResources().getColor(R.color.LightSlateGray));
        }

        Stock stock = stocks.get(i);

        stockViewHolder.firstItem.setText(stock.code);
        stockViewHolder.secondItem.setText(String.format("%.2f", stock.stock));

        return view;
    }
}