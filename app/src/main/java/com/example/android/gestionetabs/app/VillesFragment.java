package com.example.android.gestionetabs.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mbouami on 12/11/2016.
 */

public class VillesFragment extends Fragment {

    ArrayAdapter<String> mVillesAdapter;

    public VillesFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.villesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchVillesTask villeTask = new FetchVillesTask();
            villeTask.execute("93");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        List<String> listeVilles = new ArrayList<String>(Arrays.asList(data));
        mVillesAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_villes, // The name of the layout ID.
                        R.id.list_item_ville_textview, // The ID of the textview to populate.
                        listeVilles);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_ville);
        listView.setAdapter(mVillesAdapter);

        return rootView;
    }

    public class FetchVillesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchVillesTask.class.getSimpleName();

        private String[] getVilleDataFromJson(String villeJsonStr) throws JSONException {

            final String OWM_DEPART = "93";
            final String OWM_ID= "id";
            final String OWM_NOM= "nom";
            final String OWM_DISTRICT= "district";
            final String OWM_CP= "cp";
            JSONObject villeJson = new JSONObject(villeJsonStr);
            JSONArray villeArray = villeJson.getJSONArray(OWM_DEPART);
            String[] resultStrs = new String[villeArray.length()];
            for(int i = 0; i < villeArray.length(); i++) {
                JSONObject laville = villeArray.getJSONObject(i);
                resultStrs[i] = laville.getString(OWM_ID)+"---"+ laville.getString(OWM_NOM);
            }
//            for (String s : resultStrs) {
//                Log.v(LOG_TAG, "ville entry: " + s);
//            }
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String villesJsonStr = null;

            try {
//                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
//                String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
//                URL url = new URL(baseUrl.concat(apiKey));
                final String QUERY_DEPART = params[0].toString();
                String baseUrl = "http://www.bouami.fr/gestionetabs/web/listevilles/";
                URL url = new URL(baseUrl.concat(QUERY_DEPART));
                Log.v(LOG_TAG, "Built URI " + url.toString());
//                ChargerDonnees lesdonnees =  new ChargerDonnees(baseUrl,"GET",null);
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                villesJsonStr = buffer.toString();
//                Log.v(LOG_TAG, "villesJsonStr string: " + villesJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getVilleDataFromJson(villesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mVillesAdapter.clear();
                for(String villeStr : result) {
                    mVillesAdapter.add(villeStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}
