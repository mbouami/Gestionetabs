package com.example.android.gestionetabs.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class DetailEtabFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
//    ListView listViewDetailEtabs=null;
    private static final String LOG_TAG = DetailEtabFragment.class.getSimpleName();
    private ArrayAdapter<String> mDetailEtabsAdapter = null;
    private String idville=null;
    private String nomville= null;
    private static final int DETAIL_LOADER = 0;

    public DetailEtabFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        Intent intent = getActivity().getIntent();
        DetailEtabFragment.FetchDetaiEtabTask detailetabsTask = new DetailEtabFragment.FetchDetaiEtabTask();
        if (intent != null && intent.hasExtra("idville")) {
            idville = intent.getStringExtra("idville");
            nomville = intent.getStringExtra("nomville");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailetab, menu);
    }

    private void openPreferredLocationInMap(String adresse){
        Uri location = Uri.parse("geo:0,0?q=+"+adresse);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(location);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }
    private Intent ItineraireIntent(String adresse) {
        Uri location = Uri.parse("geo:0,0?q=+"+adresse);
        Intent itineraireIntent = new Intent(Intent.ACTION_VIEW, location);
        return itineraireIntent;
    }
    private Intent envoyerMailIntent(String message) {
        Intent mailIntent = new Intent();
        mailIntent.setAction(Intent.ACTION_SEND);
        mailIntent.setType("text/plain");
//        mailIntent.putExtra(Intent.EXTRA_TEXT, "Ceci est un message de test.");
        mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {message}); // recipients
//        mailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        return mailIntent;
    }

    private Intent telephonerIntent(String numero) {
        Intent callIntent = new Intent();
        callIntent.setAction(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+numero));
//        if (ActivityCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
        return callIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_map) {
            openPreferredLocationInMap(mDetailEtabsAdapter.getItem(4).substring(10));
            return true;
//            startActivity(ItineraireIntent(mDetailEtabsAdapter.getItem(4).substring(10)));
//            Toast.makeText(getActivity(), "Adresse : "+mDetailEtabsAdapter.getItem(4).substring(10), Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.action_share) {
            startActivity(telephonerIntent(mDetailEtabsAdapter.getItem(2).substring(6)));
//            Toast.makeText(getActivity(), "Téléphone : "+mDetailEtabsAdapter.getItem(2).substring(6), Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.action_mail) {
            startActivity(Intent.createChooser(envoyerMailIntent("Ceci est un test"), mDetailEtabsAdapter.getItem(3).substring(8)));
//            Toast.makeText(getActivity(), "Email : "+mDetailEtabsAdapter.getItem(3).substring(8), Toast.LENGTH_SHORT).show();
        }
//        return super.onOptionsItemSelected(item);
        return true;
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
//            nometabs.setText(getString(R.string.detail_etab)+" "+intent.getStringExtra("nometab"));
            nometabs.setText(Utility.formatDetailEtabs(getContext(),intent.getStringExtra("nometab")));
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "IO onActivityCreated");
//        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "IO onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
