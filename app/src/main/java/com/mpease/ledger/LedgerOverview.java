package com.mpease.ledger;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mpease.ledger.activities.SettingsActivity;
import com.mpease.ledger.adapter.LedgerAdapter;
import com.mpease.ledger.model.Account;
import com.mpease.ledger.model.Balance;
import com.mpease.ledger.model.LedgerEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LedgerOverview extends AppCompatActivity implements ShareActionProvider.OnShareTargetSelectedListener {

    private LedgerDatabaseHelper dbHelper;
    private int lastIndex = 0;
    private LedgerAdapter adapter;
    private ShareActionProvider shareProvider;

    public void gotoAddView(View view) {
        Intent intent = new Intent(this, EditEntryActivity.class);
        startActivityForResult(intent, 1);
    }

    public void selectCheckbox(View view) {
        CheckBox box = (CheckBox) view;
        int size = 0;

        if (box.isChecked()) {
            size = adapter.getSelected().size();
            adapter.setSelected((Integer) box.getTag());
        } else {
            adapter.unsetSelected((Integer) box.getTag());
            size = adapter.getSelected().size();
        }

        if (size < 1) {
            invalidateOptionsMenu();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        this.adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.activity_ledger_overview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new LedgerDatabaseHelper(this);
        adapter = new LedgerAdapter(this, dbHelper);

        final ListView listView = (ListView) findViewById(R.id.ledger_entries);

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ledger_overview, menu);
        return true;
    }

    private void setShareIntent(Intent intent) {
        if (shareProvider != null) {
            shareProvider.setShareIntent(intent);
        }
    }

    private Uri generateExport() {
        StringBuilder builder = new StringBuilder();

        Map<Integer, Boolean> map = adapter.getSelected();
        for (int pos : map.keySet()) {
            if (map.get(pos)) {
                builder.append(adapter.getItem(pos).getExportString());
            }
        }

        builder.append("\n");

        DateFormat df = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");

        String filename = "export_" + df.format(new Date()) + ".ledger";
        FileOutputStream outputStream;

        File file = new File(getFilesDir(), "exports");
        if (file == null) {
            Toast toast = Toast.makeText(this, "Cannot open exports dir. No such directory.", Toast.LENGTH_LONG);
            toast.show();
            return null;
        }

        if (!file.exists()) {
            file.mkdir();
        }

        file = new File(file, filename);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Toast toast = Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
                toast.show();
                return null;
            }
        }

        if (!file.canWrite()) {
            Toast toast = Toast.makeText(this, "Cannot write file " + filename, Toast.LENGTH_LONG);
            toast.show();
            return null;
        }

        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
            toast.show();
            return null;
        }

        try {
            outputStream.write(builder.toString().getBytes());
            outputStream.close();
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
            toast.show();
            return null;
        }

        return FileProvider.getUriForFile(LedgerOverview.this, "de.mpease.ledger.fileprovider", file);
    }

    private void exportAndSend() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        Uri uri = generateExport();

        if (uri == null) {
            return;
        }
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType(getContentResolver().getType(uri));
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        invalidateOptionsMenu();
    }

    public void processItems() {
        Map<Integer, Boolean> map = adapter.getSelected();
        for (int key : map.keySet()) {
            if (map.get(key)) {
                dbHelper.setProcessed((LedgerEntry) adapter.getItem(key));
            }
        }
        adapter.getSelected().clear();
        adapter.notifyDataSetChanged();

        Toast toast = Toast.makeText(this, "Items marked as processed.", Toast.LENGTH_LONG);
        invalidateOptionsMenu();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, 1);
                return true;
            case R.id.menu_item_share:
                exportAndSend();
                return true;
            case R.id.toggle_all:
                adapter.setAll();
                invalidateOptionsMenu();
                return true;
            case R.id.process_item:
                processItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!adapter.hasSelection()) {
            menu.findItem(R.id.process_item).setEnabled(false);
            menu.findItem(R.id.menu_item_share).setEnabled(false);
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
        exportAndSend();
        return true;
    }
}
