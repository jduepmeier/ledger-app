package com.mpease.ledger;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mpease.ledger.adapter.LedgerAdapter;
import com.mpease.ledger.model.Account;
import com.mpease.ledger.model.Balance;
import com.mpease.ledger.model.LedgerEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LedgerOverview extends AppCompatActivity {

    private LedgerDatabaseHelper dbHelper;
    private int lastIndex = 0;
    private LedgerAdapter adapter;
    private Map<Integer, Boolean> checked;

    public List<Balance> dummyBalances(int i) {
        Account account = dbHelper.getOrCreateAccount("Expenses:Account " + i, "Account für Nummer " + i, "Account" + i);
        Account account2 = dbHelper.getOrCreateAccount("Expenses:Account " + (i + 1), "Account für Nummer " + (i + 1), "Account" + (i + 1));
        double value = i * 10.52;

        List<Balance> balances = new ArrayList<>();

        balances.add(new Balance(account, value));
        balances.add(new Balance(account2, -value));

        return balances;
    }

    public void gotoAddView(View view) {
        Intent intent = new Intent(this, EditEntryActivity.class);
        startActivityForResult(intent, 1);

    }

    public void selectCheckbox(View view) {
        CheckBox box = (CheckBox) view;
        int size = 0;

        if (box.isChecked()) {
            size = checked.size();
            checked.put((int) box.getTag(), true);
        } else {
            checked.remove(box.getTag());
            size = checked.size();
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
        checked = new HashMap<>();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.delete_item:

                for (int key : checked.keySet()) {
                    dbHelper.deleteEntry((LedgerEntry) adapter.getItem(key));
                }
                checked.clear();
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (checked.isEmpty()) {
            menu.findItem(R.id.delete_item).setEnabled(false);
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
}
