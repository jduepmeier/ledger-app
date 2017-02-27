package com.mpease.ledger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

        df = new SimpleDateFormat("yyyy-MM-dd");

        TextView view = (TextView) findViewById(R.id.edit_date);
        view.setText(df.format(new Date()));
    }


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

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save_item) {
            saveItem();
        } else {
            System.out.println("unkown menu item...");
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

        Account one = dbhelper.getOrCreateAccount("Expenses:Dummy1", "first Dummy Account", "Dummy1");
        Account two = dbhelper.getOrCreateAccount("Expenses:Dummy2", "first Dummy Account", "Dummy2");

        Balance a = new Balance(one, value);
        Balance b = new Balance(two, -value);

        List<Balance> balances = new ArrayList<>();
        balances.add(a);
        balances.add(b);

        LedgerEntry entry = new LedgerEntry(date, name, balances, -1);
        dbhelper.addEntry(entry);

        Toast toast = Toast.makeText(this, "Saved successfully.", Toast.LENGTH_LONG);
        toast.show();

        finish();
    }
}
