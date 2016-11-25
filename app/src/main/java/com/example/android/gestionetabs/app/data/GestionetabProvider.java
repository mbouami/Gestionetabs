package com.example.android.gestionetabs.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.gestionetabs.app.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohammed on 17/11/2016.
 */

public class GestionetabProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private String parseresultat = null;
    private String baseUrl = "http://www.bouami.fr/gestionetabs/web/";

    static final int VILLE = 100;
    static final int VILLES_PAR_DEPARTEMENT = 101;
    static final int ETABLISSEMENT = 300;

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = VillesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, VillesContract.PATH_VILLE, VILLE);
        matcher.addURI(authority, VillesContract.PATH_VILLE + "/*", VILLES_PAR_DEPARTEMENT);
        matcher.addURI(authority, VillesContract.PATH_ETABLISSEMENT, ETABLISSEMENT);
        return matcher;
    }
    // The URI Matcher used by this content provider.
    public String parse(URL url, String method) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String line = null;
        urlConnection.setRequestMethod(method);
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }
        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
            return null;
        }
        try {
            return buffer.toString();
        }
        finally {
            urlConnection.disconnect();
            reader.close();
        }
    }

    public ArrayList<Map<String, String>> getVilleDataFromJson(URL url, String method, String depart) throws JSONException, IOException {

        final String OWM_DEPART = depart;
        final String OWM_ID= "id";
        final String OWM_NOM= "nom";
        final String OWM_DISTRICT= "district";
        final String OWM_CP= "cp";
        JSONObject villeJson = new JSONObject(parse(url,method));
        JSONArray villeArray = villeJson.getJSONArray(OWM_DEPART);
        ArrayList<Map<String, String>> resultStrs = new ArrayList<Map<String, String>>();
        for(int i = 0; i < villeArray.length(); i++) {
            JSONObject laville = villeArray.getJSONObject(i);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", laville.getString(OWM_ID));
            map.put("nom", laville.getString(OWM_NOM));
            resultStrs.add(map);
        }
        return resultStrs;
    }


    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case VILLE:
                return VillesContract.VillesEntry.CONTENT_TYPE;
            case VILLES_PAR_DEPARTEMENT:
                return VillesContract.VillesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
