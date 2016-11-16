package com.example.android.gestionetabs.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mbouami on 12/11/2016.
 */

public class VillesFragment extends Fragment {

//    ArrayAdapter<String> mVillesAdapter;
    SimpleAdapter mVillesAdapter;
    ListView listView = null;

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
            updateVille();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (ListView) rootView.findViewById(R.id.listview_ville);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Map<String, String> item = (Map<String, String>) mVillesAdapter.getItem(position);
//                String villecast = item.get("id");
//                Toast.makeText(getActivity(), villecast, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(), EtabActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, villecast);
                Intent intent = new Intent(getActivity(), EtabActivity.class)
                        .putExtra("idville", item.get("id")).putExtra("nomville",item.get("nom"));
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateVille() {
            FetchVillesTask villeTask = new FetchVillesTask();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String depart = prefs.getString(getString(R.string.pref_depart_key),getString(R.string.pref_depart_default));
//            String depart = prefs.getString(getString(R.string.pref_depart_key),
//                    getString(R.string.pref_depart_default));
//            Toast.makeText(getActivity(), depart, Toast.LENGTH_SHORT).show();
            villeTask.execute(depart);
            TextView titrevilles = (TextView) getActivity().findViewById(R.id.titre_ville);
            titrevilles.setText(getString(R.string.titre_ville)+" "+depart);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateVille();
    }

    public class FetchVillesTask extends AsyncTask<String, Void, ArrayList<Map<String, String>>> {

        private final String LOG_TAG = FetchVillesTask.class.getSimpleName();
        private JSONParserDonnees listevillespardepart = new JSONParserDonnees();

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
                villesJsonStr = listevillespardepart.parse(url,"GET");
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
            try {
                return listevillespardepart.getVilleDataFromJson(villesJsonStr,params[0].toString());
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList<Map<String, String>> result) {
            if (result != null) {
                Log.v(LOG_TAG, "ville entry: "+result.size());
                mVillesAdapter = new SimpleAdapter(getActivity(),result, R.layout.list_item_villes, new String[] { "id", "nom" },new int[] { R.id.id, R.id.nom });
                listView.setAdapter(mVillesAdapter);
            }
        }
    }
}
