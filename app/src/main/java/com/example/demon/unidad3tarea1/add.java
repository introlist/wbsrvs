package com.example.demon.unidad3tarea1;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class add extends AppCompatActivity implements View.OnClickListener{

    EditText nombre;
    EditText tag1;
    EditText tag2;
    EditText url;
    Button btnPost;
    TextView tvIsConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        nombre = (EditText) findViewById(R.id.nombre);
        url = (EditText) findViewById(R.id.url);
        tag1 = (EditText) findViewById(R.id.tag1);
        tag2 = (EditText) findViewById(R.id.tag2);
        btnPost = (Button) findViewById(R.id.btnPost);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);


        // check if you are connected or not
        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are conncted");
        }
        else{
            tvIsConnected.setText("You are NOT conncted");
        }

        // add click listener to Button "POST"
        btnPost.setOnClickListener(this);

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

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    public static String POST(String url, String name, String photo, String tag1, String tag2){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("name", name);
            jsonObject.accumulate("photoUrls", photo);
            jsonObject.accumulate("tags", tag1);
            jsonObject.accumulate("tags",tag2);
            jsonObject.accumulate("status","sold");

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.btnPost:
                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                // call AsynTask to perform network operation on separate thread
                new HttpAsyncTask().execute("http://hmkcode.appspot.com/jsonservlet");
                break;
        }

    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        String sNombre = nombre.getText().toString().trim();
        String sTag1 = tag1.getText().toString().trim();
        String sUrl = url.getText().toString().trim();
        String sTag2 =tag2.getText().toString().trim();;




        @Override
        protected String doInBackground(String... urls) {

            if (sTag2.isEmpty()){
                sTag2 = "";
            }
            return POST(urls[0],sNombre,sUrl,sTag1,sTag2);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validate(){
        if(nombre.getText().toString().trim().equals(""))
            return false;
        else if(tag1.getText().toString().trim().equals(""))
            return false;
        else if(url.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }


}

