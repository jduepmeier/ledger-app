package com.mpease.ledger.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.mpease.ledger.LedgerDatabaseHelper;
import com.mpease.ledger.R;
import com.mpease.ledger.adapter.AccountsAdapter;

public class AccountsOverview extends AppCompatActivity {

    private AccountsAdapter adapter;
    private LedgerDatabaseHelper dbHelper;

    public void gotoAddView(View view) {
        Intent intent = new Intent(this, EditAccountActivity.class);
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.adapter.notifyDataSetChanged();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_overview);


        Toolbar toolbar = (Toolbar) findViewById(R.id.accounts_toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new LedgerDatabaseHelper(this);
        adapter = new AccountsAdapter(this, dbHelper);

        final ListView listView = (ListView) findViewById(R.id.list_accounts);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accounts_overview, menu);
        return true;
    }

    public void deleteItems() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.accounts_delete_item:
                deleteItems();
                break;
            case R.id.accounts_toggle_all:
                adapter.setAll();
                invalidateOptionsMenu();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.accounts_delete_item).setEnabled(adapter.hasSelection());

        return true;
    }
}
