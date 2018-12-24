package com.wavemediaplayer.jamendo.info;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetData {

     JamendoApi api;
     String url = "";

    public GetData(String url) {
        this.url = url;
    }

    public  void getJsonData(JamendoApi api){
        this.api = api;
        new GetValue().execute(url);
    }


     class GetValue extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            api.beforeData();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            api.afterData();

        }

        @Override
        protected Void doInBackground(String... params) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(params[0]);
            api.getApiData(jsonStr);
            return null;
        }

    }
}
