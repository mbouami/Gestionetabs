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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mbouami on 12/11/2016.
 */

public class VillesFragment extends Fragment {

//    ArrayAdapter<String> mVillesAdapter;
    SimpleAdapter mVillesAdapter;
    ListView listView = null;
    JSONParserDonnees listevilles = new JSONParserDonnees();
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
                "Sun 6/29 - Sunny - 20/7",
                "test - test"
        };
        ArrayList<Map<String, String>> listeVilles = new ArrayList<Map<String, String>>();
        for(int j = 0; j < data.length; j++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", Integer.toString(j));
            map.put("nom", data[j]);
            listeVilles.add(map);
        }
//        try {
//            String baseUrl = "http://www.bouami.fr/gestionetabs/web/listevilles/";
//            URL url = new URL(baseUrl.concat("77"));
//            JSONParserDonnees lesdonnees = new JSONParserDonnees();
//            try {
//                String listedonnees = lesdonnees.parse(url,"GET");
//                mVillesAdapter = new SimpleAdapter(getActivity(),
//                                                    lesdonnees.getVilleDataFromJson(listedonnees),
//                                                    R.layout.list_item_villes, new String[] { "id", "nom" },
//                                                    new int[] { R.id.id, R.id.nom });
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        String villesJsonStr = null;
//        try {
//            final String QUERY_DEPART = "77";
//            String baseUrl = "http://www.bouami.fr/gestionetabs/web/listevilles/";
//            URL url = new URL(baseUrl.concat(QUERY_DEPART));
//            villesJsonStr = listevilles.parse(url,"GET");
//        } catch (IOException e) {
//            return null;
//        }
//        try {
//            mVillesAdapter = new SimpleAdapter(getActivity(),listevilles.getVilleDataFromJson(villesJsonStr), R.layout.list_item_villes, new String[] { "id", "nom" },new int[] { R.id.id, R.id.nom });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        mVillesAdapter = new SimpleAdapter(getActivity(),listeVilles, R.layout.list_item_villes, new String[] { "id", "nom" },new int[] { R.id.id, R.id.nom });
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        listView = (ListView) rootView.findViewById(R.id.listview_ville);
        listView.setAdapter(mVillesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Map<String, String> item = (Map<String, String>) mVillesAdapter.getItem(position);
                String villecast = item.get("id");
                Toast.makeText(getActivity(), villecast, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    public ArrayList<Map<String, String>> getVilleDataFromJson(String villeJsonStr) throws JSONException {

        final String OWM_DEPART = "93";
        final String OWM_ID= "id";
        final String OWM_NOM= "nom";
        final String OWM_DISTRICT= "district";
        final String OWM_CP= "cp";
        JSONObject villeJson = new JSONObject(villeJsonStr);
        JSONArray villeArray = villeJson.getJSONArray(OWM_DEPART);
//            String[] resultStrs = new String[villeArray.length()];
        ArrayList<Map<String, String>> resultStrs = new ArrayList<Map<String, String>>();
        for(int i = 0; i < villeArray.length(); i++) {
            JSONObject laville = villeArray.getJSONObject(i);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", laville.getString(OWM_ID));
            map.put("nom", laville.getString(OWM_NOM));
            resultStrs.add(map);
        }
//            for (String s : resultStrs) {
//                Log.v(LOG_TAG, "ville entry: " + s);
//            }
        return resultStrs;
    }

    public class FetchVillesTask extends AsyncTask<String, Void, ArrayList<Map<String, String>>> {

        private final String LOG_TAG = FetchVillesTask.class.getSimpleName();

        @Override
        protected ArrayList<Map<String, String>> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String villesJsonStr = null;
            try {
//                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
//                String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
//                URL url = new URL(baseUrl.concat(apiKey));
                final String QUERY_DEPART = params[0].toString();
                String baseUrl = "http://www.bouami.fr/gestionetabs/web/listevilles/";
                URL url = new URL(baseUrl.concat(QUERY_DEPART));
                villesJsonStr = listevilles.parse(url,"GET");
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
            try {
                return listevilles.getVilleDataFromJson(villesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList<Map<String, String>> result) {
            if (result != null) {
                mVillesAdapter = new SimpleAdapter(getActivity(),result, R.layout.list_item_villes, new String[] { "id", "nom" },new int[] { R.id.id, R.id.nom });
                listView.setAdapter(mVillesAdapter);
            }
        }
    }
}
