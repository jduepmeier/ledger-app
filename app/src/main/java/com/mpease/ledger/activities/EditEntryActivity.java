package com.mpease.ledger.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mpease.ledger.LedgerDatabaseHelper;
import com.mpease.ledger.R;
import com.mpease.ledger.adapter.BalanceAdapter;
import com.mpease.ledger.model.Account;
import com.mpease.ledger.model.Balance;
import com.mpease.ledger.model.LedgerEntry;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditEntryActivity extends AppCompatActivity {

    private List<Account> accounts;
    private LedgerEntry entry;
    private DateFormat df;
    private LedgerDatabaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        dbhelper = new LedgerDatabaseHelper(this);
        setContentView(R.layout.activity_edit_entry);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        df = new SimpleDateFormat(sharedPreferences.getString("pref_date_format", ""));

        EditText view = (EditText) findViewById(R.id.edit_date);
        view.setText(df.format(new Date()));

        AutoCompleteTextView nameEdit = (AutoCompleteTextView) findViewById(R.id.edit_name);

        String[] names = dbhelper.getEntryNames();

        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        nameEdit.setAdapter(autoCompleteAdapter);

        EditText valueView = (EditText) findViewById(R.id.edit_value);
        valueView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    saveItem();
                    return true;
                }
                return false;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    public void onGroupItemClick(MenuItem item) {
        // One of the group items (using the onClick attribute) was clicked
        // The item parameter passed here indicates which item it is
        // All other menu item clicks are handled by onOptionsItemSelected()
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save_item) {
            saveItem();
        } else {
            System.out.println("unkown menu item...");
            return false;
        }

        return true;
    }

    public void saveItem() {

        Date date;
        String name;
        double value;

        EditText dateBox = (EditText) this.findViewById(R.id.edit_date);
        try {
            date = df.parse(dateBox.getText().toString());
        } catch (ParseException e) {
            Toast toast = Toast.makeText(this, "Cannot parse Date.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        EditText nameBox = (EditText) this.findViewById(R.id.edit_name);
        name = nameBox.getText().toString();

        EditText valueBox = (EditText) this.findViewById(R.id.edit_value);
        try {
            value = Double.parseDouble(valueBox.getText().toString());
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(this, "Cannot parse value.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Account one = dbhelper.getAccount(
                Integer.parseInt(sharedPreferences.getString("pref_default_account1", "")));
        Account two = dbhelper.getAccount(
                Integer.parseInt(sharedPreferences.getString("pref_default_account2", "")));

        Balance a = new Balance(this, one, value);
        Balance b = new Balance(this, two, -value);

        List<Balance> balances = new ArrayList<>();
        balances.add(a);
        balances.add(b);

        LedgerEntry entry = new LedgerEntry(this, date, name, balances, -1);
        dbhelper.addEntry(entry);

        Toast toast = Toast.makeText(this, "Saved successfully.", Toast.LENGTH_LONG);
        toast.show();

        finish();
    }
}
