package com.example.android.gestionetabs.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mohammed on 23/11/2016.
 */

public class VillesContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.gestionetabs.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
//    public static final Uri BASE_CONTENT_URI = Uri.parse("http://www.bouami.fr/gestionetabs/web/");

    public static final String PATH_VILLE = "listevilles";
    public static final String PATH_ETABLISSEMENT = "listeetabs";

    public static final class VillesEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VILLE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VILLE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VILLE;

        public static final String TABLE_NAME = "villes";
        public static final String COLUMN_VILLE_ID = "id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_VILLE_NOM = "nom";

//        public static Uri buildVilleUri(String id) {
//            return ContentUris.withAppendedId(BASE_CONTENT_URI, id);
//        }
//        public static Uri buildVilleUri(String id) {
//            return BASE_CONTENT_URI.buildUpon().appendPath("listevilles/"+id).build();
//        }

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getVilleFromUri(Uri uri) {

            return uri.getPathSegments().get(1);
        }

    }
}
