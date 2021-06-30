package com.example.cryptocurrenciestrackingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    Button convert;
    String text;




    Spinner spinner;
    String apiKey = "f48cb97a-999a-45fd-837b-f8ea907a6805";
    String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";

    ArrayList< String >result_to_print = new ArrayList<>();
    ListView listView;
    ArrayAdapter<String> itemsAdapter;
    ArrayList< String >final_res = new ArrayList<>();
    double res=1.0;



    //    private static final String URL_DATA =
//            "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms=NGN,USD,EUR,JPY,GBP,AUD,CAD,CHF,CNY,KES,GHS,UGX,ZAR,XAF,NZD,MYR,BND,GEL,RUB,INR";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.choice);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);




        itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, result_to_print);
        listView = (ListView) findViewById(R.id.list);

        convert = (Button) findViewById(R.id.convert);

        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData process = new fetchData();
                process.execute();
                Toast.makeText(MainActivity.this, "wait for some sec and click again", Toast.LENGTH_SHORT).show();

                listView.setAdapter(itemsAdapter);

            }
        });




    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        text = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
//        fetchData process = new fetchData();
//        process.execute();


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }









    /// API TO FECTCH cryptocurrencies

    ArrayList< String> Crypto_name = new ArrayList<>();
    ArrayList<Double> price_in_$ = new ArrayList<>();

    Double value=1.0;
    public class fetchData extends AsyncTask< Void, Void, Void >  {

        String data = "";






        @Override
        protected Void doInBackground(Void... voids) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?limit=10");


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("X-CMC_PRO_API_KEY", "f48cb97a-999a-45fd-837b-f8ea907a6805");

                InputStream in = urlConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while(line!=null){
                    line = bufferedReader.readLine();
                    data=data+line;

                }
//            Log.i("data is ",data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            try {

                JSONObject jsonObject = new JSONObject(data);
                String maindata = jsonObject.getString("data");
                Log.i("maindata  ",maindata);
                JSONArray arr = new JSONArray(maindata);
                Crypto_name.clear();
                price_in_$.clear();
                for (int i = 0; i < arr.length(); i++) {
                    String mess = "";

                    JSONObject jsonPart = arr.getJSONObject(i);
                    String name = "";
                    double value_in_USD = -1.0;
                    name = jsonPart.getString("name");

                    Log.i("name is : ", name);
                    JSONObject company = jsonPart.getJSONObject("quote");
                    JSONObject company1 = company.getJSONObject("USD");
                    value_in_USD = company1.getDouble("price");




                    if (name != "" && value_in_USD != -1.0) {
                        Log.i("message is : ", String.valueOf(value_in_USD));
                        Crypto_name.add(name);
                        price_in_$.add(value_in_USD);
                    } else {
                        Log.i("error", "not able to find a bit coin");
                    }


                }
            } catch(JSONException ex) {
                ex.printStackTrace();
            }

            if(text.matches("USD")){

                result_to_print.clear();
                listView.setAdapter(itemsAdapter);

                for (int i = 0; i < price_in_$.size(); i++) {
                    @SuppressLint("DefaultLocale") String form = Crypto_name.get(i) + "             " + String.format("%.4f", price_in_$.get(i));
                    result_to_print.add(form);

                }
                listView.setAdapter(itemsAdapter);

            }
            else {

                fetchAPI_for_currency_relation process1 = new fetchAPI_for_currency_relation();
                process1.execute();

                // changing the list


//                Log.i("VALUE OF RES is", String.valueOf(value))
            }











//        MainActivity.data.setText(this.data);
        }
    }






    // API TO FETCH CURRENCIES

    public class fetchAPI_for_currency_relation  extends AsyncTask< Void, Void, Void > {

        String data1 = "";







        @Override
        protected Void doInBackground(Void... voids) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL("https://api.exchangeratesapi.io/latest?base=USD");


                urlConnection = (HttpURLConnection) url.openConnection();


                InputStream in = urlConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while(line!=null){
                    line = bufferedReader.readLine();
                    data1=data1+line;

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                JSONObject mainObject = new JSONObject(data1);


                JSONObject jsonObject =  mainObject.getJSONObject("rates");
               Log.i("data is ", String.valueOf(jsonObject));
//                JSONObject company = jsonObject.getJSONObject(text);
//                Log.i("data is ", String.valueOf(company));
//            JSONArray arr = new JSONArray(rate);
//                Log.i("text is : ",text);
                res = jsonObject.getDouble(text);

//                Log.i("res is ", String.valueOf(res));





            } catch(JSONException ex) {
                ex.printStackTrace();
            }

            result_to_print.clear();
            listView.setAdapter(itemsAdapter);
            ArrayList< Double >sample = new ArrayList<>();

            for (int i = 0; i < price_in_$.size(); i++)
            {
                Double new_val = price_in_$.get(i);
                new_val *= value;
                sample.add(new_val);
            }

//                final_res.clear();

            for (int i = 0; i < sample.size(); i++) {
                String form = Crypto_name.get(i) + "             " + String.format("%.4f", sample.get(i));
                result_to_print.add(form);

            }
            listView.setAdapter(itemsAdapter);
            value=res;






//        MainActivity.data.setText(this.data);
        }
    }


}

