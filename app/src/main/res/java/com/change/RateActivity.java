package java.com.change;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.change.R;
import com.change.model.Rate;
import com.change.utils.Constants;

public class RateActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        TextView nameView = (TextView) findViewById(R.id.name);
        TextView codeView = (TextView) findViewById(R.id.code);
        TextView currencyView = (TextView) findViewById(R.id.currency);
        TextView rateView = (TextView) findViewById(R.id.rate);
        TextView reverseRateView = (TextView) findViewById(R.id.reverseRate);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Rate rate = extras.getParcelable(Constants.RATE);

            nameView.setText(rate.name);
            codeView.setText(rate.code);
            currencyView.setText(String.valueOf(rate.ratio));
            rateView.setText(String.valueOf(rate.rate));
            reverseRateView.setText(String.valueOf(rate.reverseRate));
        }
    }
}