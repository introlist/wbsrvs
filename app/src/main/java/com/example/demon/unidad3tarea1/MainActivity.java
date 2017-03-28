package com.example.demon.unidad3tarea1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends Activity {

    EditText etResponse;
    TextView tvIsConnected;
    ListView listView;
    ArrayAdapter<String> itemsAdapter;
    ArrayList<String> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        etResponse = (EditText) findViewById(R.id.text);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        listView = (ListView) findViewById(R.id.lv);
        itemsAdapter =
                new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, lista);

        if (isConnected()) {
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("Conectado");
        } else {
            tvIsConnected.setText("NO conectado");
        }
        // call AsynTask
        new HttpAsyncTask().execute("http://petstore.swagger.io/v2/pet/findByStatus?status=sold");
    }

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {
            // HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // GET operacion
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            // recibe respuesta
            inputStream = httpResponse.getEntity().getContent();
            // procesa resp a string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Problemas!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        // onPostExecute despliega el resultado.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Info recibida!", Toast.LENGTH_LONG).show();
            etResponse.setText(result);

            try {
                //  System.out.println("LLEGO");
                lista = parseJSONObjects(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //System.out.println("PASO");
            if (!lista.isEmpty()) {


                listView.setAdapter(itemsAdapter);
                for (String elemento : lista) {
                    System.out.println(elemento);
                }
            }
        }

        public ArrayList<String> parseJSONObjects(String result) throws JSONException {
            //JSONObject obj = new JSONObject(result);
            ArrayList<String> list = new ArrayList<>();
            JSONArray array = new JSONArray(result);
            System.out.println(result);
            System.out.println(array.length());
            for (int i = 0; i < array.length(); i++) {
                //array.getJSONObject(i);
                //JSONObject pet = array.getJSONObject(i);
                list.add(array.getJSONObject(i).getString("name"));
                itemsAdapter.add(array.getJSONObject(i).getString("name"));
                //System.out.println(pet.getString("name"));

            }
            return list;

        }

    }
}

