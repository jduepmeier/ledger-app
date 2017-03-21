package com.mpease.ledger.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.mpease.ledger.LedgerDatabaseHelper;
import com.mpease.ledger.R;
import com.mpease.ledger.model.Account;

public class EditAccountActivity extends AppCompatActivity {

    private LedgerDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        dbHelper = new LedgerDatabaseHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_accounts_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.save_item:
                saveItem();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void saveItem() {
        String name;
        String alias;

        EditText nameBox = (EditText) this.findViewById(R.id.edit_account_name);
        EditText aliasBox = (EditText) this.findViewById(R.id.edit_account_alias);

        dbHelper.getOrCreateAccount(nameBox.getText().toString(), "", aliasBox.getText().toString());

        Toast toast = Toast.makeText(this, "Saved successfully.", Toast.LENGTH_LONG);
        toast.show();

        finish();
    }
}
