package java.com.change.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.change.R;
import com.change.model.Rate;

import java.util.ArrayList;
import java.util.List;

public class RateAdapter extends ArrayAdapter<Rate> {

    private static class ViewHolder {
        ImageView flag;
        ImageView movementImage;
        TextView code;
        TextView rate;
        TextView reverseRate;
    }

    private List<Rate> rates;
    private Context context;

    public RateAdapter(Context context, int rateViewId, ArrayList<Rate> rates) {
        super(context, rateViewId);
        this.rates = rates;
        this.context = context;
    }

    @Override
    public int getCount() {
        return rates.size();
    }

    @Override
    public Rate getItem(int i) {
        return rates.get(i);
    }

    @Override
    public long getItemId(int i) {
        return rates.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder rateViewHolder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.rate_item, viewGroup, false);

            rateViewHolder = new ViewHolder();
            rateViewHolder.flag = (ImageView) view.findViewById(R.id.flag);
            rateViewHolder.movementImage = (ImageView) view.findViewById(R.id.movement);
            rateViewHolder.code = (TextView) view.findViewById(R.id.code);
            rateViewHolder.rate = (TextView) view.findViewById(R.id.rate);
            rateViewHolder.reverseRate = (TextView) view.findViewById(R.id.reverseRate);

            view.setTag(rateViewHolder);
        } else {
            rateViewHolder = (ViewHolder) view.getTag();
        }

        if (i % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.DarkSlateGray));
        } else {
            view.setBackgroundColor(view.getResources().getColor(R.color.LightSlateGray));
        }

        Rate rate = rates.get(i);

        switch (rate.movement) {
            case Rate.UP:
                rateViewHolder.movementImage.setBackgroundResource(R.drawable.green_arrow);
                break;
            case Rate.DOWN:
                rateViewHolder.movementImage.setBackgroundResource(R.drawable.red_arrow);
                break;
            default:
                rateViewHolder.movementImage.setBackgroundResource(0);
                break;
        }

        String code = rate.code;
        int flagId = context.getResources().getIdentifier(code.equalsIgnoreCase("TRY") ? "tryy" :
                code.toLowerCase(), "drawable", context.getPackageName());

        rateViewHolder.flag.setImageResource(flagId);
        rateViewHolder.code.setText(code);
        rateViewHolder.rate.setText(String.valueOf(rate.rate));
        rateViewHolder.reverseRate.setText(String.valueOf(rate.reverseRate));

        return view;
    }
}