package com.example.android.gestionetabs.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohammed on 16/11/2016.
 */

public class EtabsFragment extends Fragment {
    ListView listViewEtabs=null;
    SimpleAdapter mEtabsAdapter = null;
    private String idville= null;

    public EtabsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("idville")) {
            idville = intent.getStringExtra("idville");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.etabsfragment, menu);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_etab, container, false);
        listViewEtabs = (ListView) rootView.findViewById(R.id.listview_etab);
        listViewEtabs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Map<String, String> item = (Map<String, String>) mEtabsAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailEtabActivity.class)
                        .putExtra("idetab", item.get("id")).putExtra("nometab",item.get("nom")).putExtra("idville",idville);
                startActivity(intent);
//                Toast.makeText(getActivity(), item.get("id")+ "-"+item.get("nom"), Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    private void UpdateEtabs() {
        Intent intent = getActivity().getIntent();
        FetchEtabsTask etabsTask = new FetchEtabsTask();
        if (intent != null && intent.hasExtra("idville") && intent.hasExtra("nomville")) {
//            idville = intent.getStringExtra("idville");
            TextView titreetabs = (TextView) getActivity().findViewById(R.id.titre_etab);
            titreetabs.setText(getString(R.string.titre_etab)+" "+intent.getStringExtra("nomville"));
        }
        etabsTask.execute(idville);
    }

    @Override
    public void onStart() {
        super.onStart();
        UpdateEtabs();
    }

    public class FetchEtabsTask extends AsyncTask<String, Void, ArrayList<Map<String, String>>> {
        private final String LOG_TAG = EtabsFragment.FetchEtabsTask.class.getSimpleName();
        private JSONParserDonnees listeetabsparville = new JSONParserDonnees();
        @Override
        protected ArrayList<Map<String, String>> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String etabsJsonStr = null;
            try {
                final String QUERY_VILLE = params[0].toString();
                String baseUrl = "http://www.bouami.fr/gestionetabs/web/listeetabs/";
                URL url = new URL(baseUrl.concat(QUERY_VILLE));
                etabsJsonStr = listeetabsparville.parse(url,"GET");
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
            try {
                return listeetabsparville.getEtabsDataFromJson(etabsJsonStr,params[0].toString());
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Map<String, String>> result) {
            if (result != null) {
//                Log.v(LOG_TAG, "ville entry: "+result.size());
                mEtabsAdapter = new SimpleAdapter(getActivity(),result, R.layout.list_item_etabs, new String[] { "id", "nom" },new int[] { R.id.id_etab, R.id.nom_etab });
                listViewEtabs.setAdapter(mEtabsAdapter);
                mEtabsAdapter = new SimpleAdapter(getActivity(),result, R.layout.list_item_etabs, new String[] { "id", "nom" },new int[] { R.id.id_etab, R.id.nom_etab });
                // Get a reference to the ListView, and attach this adapter to it.
                listViewEtabs.setAdapter(mEtabsAdapter);
            }
        }
    }
}
