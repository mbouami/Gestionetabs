package com.example.android.gestionetabs.app;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mbouami on 12/11/2016.
 */

public class ChargerDonnees extends InputStreamReader {
    StringBuffer buffer = null;
    BufferedReader reader = null;
    InputStream inp = null;

    public ChargerDonnees(String url, String method,InputStream in) throws IOException {
        super(in);
        try {
            URL urlString = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlString.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.connect();
            inp = conn.getInputStream();
            if (inp != null) {
                reader = new BufferedReader(new InputStreamReader(inp));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
            }
        } catch (IOException e){
            Log.e("Erreur de Chargement", "Erreur de chargement des données " + e.toString());
        }
    }
}
