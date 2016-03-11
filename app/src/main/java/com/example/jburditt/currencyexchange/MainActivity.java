package com.example.jburditt.currencyexchange;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View.OnClickListener;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // initialize GUI objects and currency formats
    Spinner spin;
    Spinner currencyTo;
    Resources res;
    String[] currs;
    ArrayAdapter<String> adapter;
    EditText amount;
    TextView amountTextView;
    TextView convertedAmountTextView;
    Double money;
    String currentCurrency;
    Button convertButton;

    NumberFormat currency = NumberFormat.getCurrencyInstance();
    NumberFormat currencyEnd = NumberFormat.getCurrencyInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        convertButton = (Button) findViewById(R.id.convertButton);
        convertButton.setOnClickListener(clickListener);
        amountTextView = (TextView) findViewById(R.id.amountTextView);
        convertedAmountTextView = (TextView) findViewById(R.id.convertedAmountTextView);
        spin = (Spinner) findViewById(R.id.spinner);
        currencyTo = (Spinner) findViewById(R.id.currencyToSpinner);
        res = getResources();
        currs = res.getStringArray(R.array.currencies);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, currs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        currencyTo.setAdapter(adapter);
        spin.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                currentCurrency = parent.getItemAtPosition(pos).toString();
                switch (currentCurrency) {
                    case "($) US Dollars":
                        currency = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
                        break;
                    case "(€) Euros":
                        currency = NumberFormat.getCurrencyInstance(new Locale("de", "DE"));
                        break;
                    case "(kr.) Danish Krones":
                        currency = NumberFormat.getCurrencyInstance(new Locale("da", "DK"));
                        break;
                    case "(Kč) Czech koruna":
                        currency = NumberFormat.getCurrencyInstance(new Locale("cs", "CZ"));
                        break;
                    case "(Ft) Hungarian forint":
                        currency = NumberFormat.getCurrencyInstance(new Locale("hu", "HU"));
                        break;
                    case "(zł) Polish zloty":
                        currency = NumberFormat.getCurrencyInstance(new Locale("pl", "PL"));
                        break;
                    case "(S/.) Peruvian Nuevo":
                        currency = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
                        break;
                    case "(₹) India Rupee":
                        currency = NumberFormat.getCurrencyInstance(new Locale("hi", "IN"));
                        break;
                    default:
                        currency = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
                }

                if (money != null)
                    amountTextView.setText(currency.format(money));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        amount = (EditText) findViewById(R.id.amountEditText);
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                try { // get bill amount and display currency formatted value
                    money = Double.parseDouble(s.toString()) / 100.0;
                    amountTextView.setText(currency.format(money));
                } catch (NumberFormatException e) { // if s is empty or non-numeric
                    amountTextView.setText("");
                    money = 0.0;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }
        });

    }

    public void convert() {
        // Exchange rates per dollar
        double[] rates = { 1.0, .91, 6.78, 24.56, 280.26 , 3.93, 3.44, 67.08 };
        double euroRate = .91;
        double danishKroneRate = 6.78;
        double czechRepublicKorunaRate = 24.56;
        double hungarianForintRate = 280.26;
        double zlotyRate = 3.93;
        double nuevoRate = 3.44;
        double rupeeRate = 67.08;
        try {
            int ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN;
            BigDecimal amt = new BigDecimal(currency.parse(amountTextView.getText().toString()).toString());
            amt = amt.setScale(2, ROUNDING_MODE);

            int positionTo = currencyTo.getSelectedItemPosition();
            int positionFrom = spin.getSelectedItemPosition();
            switch (currs[positionTo]) {
                case "($) US Dollars":
                    currencyEnd = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
                    break;
                case "(€) Euros":
                    currencyEnd = NumberFormat.getCurrencyInstance(new Locale("de", "DE"));
                    break;
                case "(kr.) Danish Krones":
                    currencyEnd = NumberFormat.getCurrencyInstance(new Locale("da", "DK"));
                    break;
                case "(Kč) Czech koruna":
                    currencyEnd = NumberFormat.getCurrencyInstance(new Locale("cs", "CZ"));
                    break;
                case "(Ft) Hungarian forint":
                    currencyEnd = NumberFormat.getCurrencyInstance(new Locale("hu", "HU"));
                    break;
                case "(zł) Polish zloty":
                    currencyEnd = NumberFormat.getCurrencyInstance(new Locale("pl", "PL"));
                    break;
                case "(S/.) Peruvian Nuevo":
                    currencyEnd = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
                    break;
                case "(₹) India Rupee":
                    currencyEnd = NumberFormat.getCurrencyInstance(new Locale("hi", "IN"));
                    break;
                default:
                    currencyEnd = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
            }


            BigDecimal bdRateFrom = new BigDecimal("" + rates[positionFrom]);
            BigDecimal bdRateTo = new BigDecimal("" + rates[positionTo]);
            convertedAmountTextView.setText(currencyEnd.format(amt.multiply(bdRateTo).divide(bdRateFrom, 2, ROUNDING_MODE)));
        } catch (ParseException e) {
            // ignore
        }
    }

    private final OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            convert();
        }
    };
}
