package com.example.android.gestionetabs.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by Mohammed on 20/11/2016.
 */

public class DetailEtabActivity extends AppCompatActivity {
    String idetab=null;
    String nometab=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent intent = this.getIntent();
//        if (intent != null && intent.hasExtra("idetab")) {
//            idetab = intent.getStringExtra("idetab");
//            nometab = intent.getStringExtra("nometab");
//        }
        setContentView(R.layout.activity_detail_etab);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailEtabFragment())
                    .commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.etab, menu);
//        getMenuInflater().inflate(R.menu.detailetab, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_map) {
////            startActivity(new Intent(this, SettingsActivity.class));
//            Toast.makeText(this, "action_map : "+nometab, Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        if (id == R.id.action_share) {
////            startActivity(new Intent(this, SettingsActivity.class));
//            Toast.makeText(this, "action_share : "+nometab, Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
