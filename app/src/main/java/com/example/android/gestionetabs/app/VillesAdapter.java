package com.example.android.gestionetabs.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mohammed on 23/11/2016.
 */

public class VillesAdapter extends CursorAdapter {
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_ENCOURS = 0;
    private static final int VIEW_TYPE_LES_AUTRES = 1;

    public static class ViewHolder {
        public final TextView nomView;
        public final TextView idView;
        public final ImageView iconView;

        public ViewHolder(View view) {
            nomView = (TextView) view.findViewById(R.id.nom);
            idView = (TextView) view.findViewById(R.id.id);
            iconView = (ImageView) view.findViewById(R.id.ville_icone);
        }
    }
    public VillesAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_ENCOURS : VIEW_TYPE_LES_AUTRES;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_ENCOURS: {
                layoutId = R.layout.list_item_villes_avance;
                break;
            }
            case VIEW_TYPE_LES_AUTRES: {
                layoutId = R.layout.list_item_villes;
                break;
            }
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Use placeholder image for now
        viewHolder.iconView.setImageResource(R.mipmap.ic_launcher);
        String id = cursor.getString(VillesFragment.COL_VILLE_ID);
        viewHolder.idView.setText(id);
        String nom = cursor.getString(VillesFragment.COL_VILLE_NOM);
        viewHolder.idView.setText(nom);
    }
}
