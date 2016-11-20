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

    public DetailEtabFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
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

    private void UpdateDetailEtabs() {
        Intent intent = getActivity().getIntent();
        DetailEtabFragment.FetchDetaiEtabTask detailetabsTask = new DetailEtabFragment.FetchDetaiEtabTask();
        String etabStr = null;
        if (intent != null && intent.hasExtra("idetab") && intent.hasExtra("nometab")) {
//            TextView titreetabs = (TextView) getActivity().findViewById(R.id.titre_etab);
//            titreetabs.setText(getString(R.string.titre_etab)+" "+intent.getStringExtra("nomville"));
            Toast.makeText(getActivity(), intent.getStringExtra("idetab")+ "-"+intent.getStringExtra("nometab"), Toast.LENGTH_SHORT).show();
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
//                String[] data = {
//                        "Mon 6/23 - Sunny - 31/17",
//                        "Tue 6/24 - Foggy - 21/8",
//                        "Wed 6/25 - Cloudy - 22/17",
//                        "Thurs 6/26 - Rainy - 18/11",
//                        "Fri 6/27 - Foggy - 21/10",
//                        "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
//                        "Sun 6/29 - Sunny - 20/7"
//                };
                List<String> resultStrs = new ArrayList<String>(Arrays.asList(result));
                mDetailEtabsAdapter.clear();
                for(String detailetabStr : resultStrs) {
                    mDetailEtabsAdapter.add(detailetabStr);
                }
            }
        }
    }

}
