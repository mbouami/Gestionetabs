package com.example.android.gestionetabs.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Mohammed on 16/11/2016.
 */

public class DetailEtabFragment extends Fragment {
//    ListView listViewDetailEtabs=null;
    private ArrayAdapter<String> mDetailEtabsAdapter = null;
    private String idville=null;
    private String nomville= null;

    public DetailEtabFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        Intent intent = getActivity().getIntent();
        DetailEtabFragment.FetchDetaiEtabTask detailetabsTask = new DetailEtabFragment.FetchDetaiEtabTask();
        if (intent != null && intent.hasExtra("idville")) {
            idville = intent.getStringExtra("idville");
            nomville = intent.getStringExtra("nomville");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDetailEtabsAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_detail_etabs, // The name of the layout ID.
                        R.id.list_item_etab, // The ID of the textview to populate.
                        new ArrayList<String>());
        View rootView = inflater.inflate(R.layout.fragment_detail_etab, container, false);
        ListView listViewDetailEtabs = (ListView) rootView.findViewById(R.id.listview_detail_etab);
        listViewDetailEtabs.setAdapter(mDetailEtabsAdapter);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        UpdateDetailEtabs();
    }

    @Override
    public void onPause() {
        super.onPause();
        Intent intent = new Intent(getActivity(), EtabActivity.class)
                .putExtra("idville", idville).putExtra("nomville",nomville);
        startActivity(intent);
    }

    private void UpdateDetailEtabs() {
        Intent intent = getActivity().getIntent();
        DetailEtabFragment.FetchDetaiEtabTask detailetabsTask = new DetailEtabFragment.FetchDetaiEtabTask();
        if (intent != null && intent.hasExtra("idetab") && intent.hasExtra("nometab")) {
            TextView nometabs = (TextView) getActivity().findViewById(R.id.nom_etab);
            nometabs.setText(getString(R.string.detail_etab)+" "+intent.getStringExtra("nometab"));
//            Toast.makeText(getActivity(), intent.getStringExtra("idetab")+ "-"+intent.getStringExtra("nometab"), Toast.LENGTH_SHORT).show();
        }
        detailetabsTask.execute(intent.getStringExtra("idetab"));
    }

    public class FetchDetaiEtabTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = DetailEtabFragment.FetchDetaiEtabTask.class.getSimpleName();
        private JSONParserDonnees infosetab = new JSONParserDonnees();
        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String infosetabJsonStr = null;
            try {
                final String QUERY_ETAB = params[0].toString();
                String baseUrl = "http://www.bouami.fr/gestionetabs/web/detailetab/";
                URL url = new URL(baseUrl.concat(QUERY_ETAB));
                infosetabJsonStr = infosetab.parse(url,"GET");
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
            try {
                return infosetab.getDetailEtabsDataFromJson(infosetabJsonStr,params[0].toString());
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                List<String> resultStrs = new ArrayList<String>(Arrays.asList(result));
                mDetailEtabsAdapter.clear();
                for(String detailetabStr : resultStrs) {
                    mDetailEtabsAdapter.add(detailetabStr);
                }
            }
        }
    }

}
