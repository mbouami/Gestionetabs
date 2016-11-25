package com.example.android.gestionetabs.app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String GESTIONETAB_TAG = "GCRETEIL";
    private String mdepart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mdepart = Utility.getPreferredDepart(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new VillesFragment(), GESTIONETAB_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String departement = Utility.getPreferredDepart( this );

        // update the location in our second pane using the fragment manager
        if (departement != null && !departement.equals(mdepart)) {
            VillesFragment ff = (VillesFragment)getSupportFragmentManager().findFragmentByTag(GESTIONETAB_TAG);
            if ( null != ff ) {
                ff.onDepartementChanged();
            }
            mdepart = departement;
        }
    }
}
