package com.example.android.gestionetabs.app;

import android.util.JsonReader;
import android.util.Log;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Mohammed on 13/11/2016.
 */

public class JSONParserDonnees {

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

    public ArrayList<Map<String, String>> getVilleDataFromJson(String villeJsonStr,String depart) throws JSONException {

        final String OWM_DEPART = depart;
        final String OWM_ID= "id";
        final String OWM_NOM= "nom";
        final String OWM_DISTRICT= "district";
        final String OWM_CP= "cp";
        JSONObject villeJson = new JSONObject(villeJsonStr);
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

    public ArrayList<Map<String, String>> getEtabsDataFromJson(String etabJsonStr,String ville) throws JSONException {

        final String OWM_VILLE = ville;
        final String OWM_TYPE = "COLLEGE";
        final String OWM_ID= "id";
        final String OWM_NOM= "nom";
        final String OWM_RNE= "rne";
        JSONObject etabJson = new JSONObject(etabJsonStr);
        Iterator<String> typesetab = etabJson.keys();
        ArrayList<Map<String, String>> resultStrs = new ArrayList<Map<String, String>>();
        while( typesetab.hasNext() ) {
            String type = (String)typesetab.next();
//            if ( etabJson.get(type) instanceof JSONObject ) {
                JSONArray etabArray = etabJson.getJSONArray(type);
                for(int i = 0; i < etabArray.length(); i++) {
                    JSONObject etab = etabArray.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("id", etab.getString(OWM_ID));
                    map.put("nom", etab.getString(OWM_NOM));
                    map.put("rne", etab.getString(OWM_RNE));
                    resultStrs.add(map);
                }
//            }
        }
        return resultStrs;
    }

    public String[] getDetailEtabsDataFromJson(String detailetabJsonStr, String etab) throws JSONException {

        final String OWM_ETAB = etab;
        final String OWM_ID= "id";
        final String OWM_NOM= "nom";
        final String OWM_RNE= "rne";
        final String OWM_STATUT= "statut";
        final String OWM_TEL= "tel";
        final String OWM_FAX= "fax";
        final String OWM_EMAIL= "email";
        final String OWM_ADRESSE= "adresse";
        final String OWM_CP= "cp";
        final String OWM_VILLE= "ville";
        final String OWM_PERSONNEL= "personnel";
        final String OWM_ETABLISSEMENT= "etablissement";
        JSONObject detailetabJson = new JSONObject(detailetabJsonStr);
        Iterator<String> keyetab = detailetabJson.keys();
        int taille = detailetabJson.getJSONArray(OWM_PERSONNEL).length() + 5;
        String[] resultStrs = new String[taille];
        JSONArray detailetablissementArray = detailetabJson.getJSONArray(OWM_ETABLISSEMENT);
        JSONArray detailpersonnelArray = detailetabJson.getJSONArray(OWM_PERSONNEL);
//        resultStrs[detailpersonnelArray.length()] = detailetablissementArray.getJSONObject(0).getString(OWM_NOM);
        resultStrs[0] = "RNE : "+detailetablissementArray.getJSONObject(0).getString(OWM_RNE);
        resultStrs[1] = "TEL : "+detailetablissementArray.getJSONObject(0).getString(OWM_TEL);
        resultStrs[2] = "FAX : "+detailetablissementArray.getJSONObject(0).getString(OWM_FAX);
        resultStrs[3] = "EMAIL : "+detailetablissementArray.getJSONObject(0).getString(OWM_EMAIL);
        resultStrs[4] = "ADRESSE : "+detailetablissementArray.getJSONObject(0).getString(OWM_ADRESSE) + " "+ detailetablissementArray.getJSONObject(0).getString(OWM_CP)+" "+detailetablissementArray.getJSONObject(0).getString(OWM_VILLE);
        for(int i = 0; i < detailpersonnelArray.length(); i++) {
            JSONObject detailpersonnel = detailpersonnelArray.getJSONObject(i);
            resultStrs[i+5] = detailpersonnel.getString(OWM_STATUT) + " : " + detailpersonnel.getString(OWM_NOM);
        }
//        for (String s : resultStrs) {
//                Log.v("test", "ville entry: " + s);
//        }
        return resultStrs;
    }
}
