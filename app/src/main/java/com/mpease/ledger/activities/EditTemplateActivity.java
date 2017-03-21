package com.mpease.ledger.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mpease.ledger.LedgerDatabaseHelper;
import com.mpease.ledger.R;
import com.mpease.ledger.adapter.TemplateSpinnerAdapter;
import com.mpease.ledger.model.Account;
import com.mpease.ledger.model.Template;

import java.util.List;

public class EditTemplateActivity extends AppCompatActivity {

    private LedgerDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_template);

        dbHelper = new LedgerDatabaseHelper(this);
        List<Account> templates = dbHelper.getAccounts();

        EditText valueView = (EditText) findViewById(R.id.edit_template_name);
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

        Spinner spinner1 = (Spinner) findViewById(R.id.edit_template_account1);
        ArrayAdapter<Account> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, templates);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        Spinner spinner2 = (Spinner) findViewById(R.id.edit_template_account2);
        ArrayAdapter<Account> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, templates);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_templates_toolbar);
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

    private void saveItem() {

        EditText nameBox = (EditText) this.findViewById(R.id.edit_template_name);

        Spinner spinner1 = (Spinner) this.findViewById(R.id.edit_template_account1);
        Account account1 = (Account) spinner1.getSelectedItem();
        Spinner spinner2 = (Spinner) this.findViewById(R.id.edit_template_account2);
        Account account2 = (Account) spinner2.getSelectedItem();

        Template template = new Template(-1, nameBox.getText().toString(),
                account1.getId(), account2.getId());

        dbHelper.createTemplate(template);

        Toast toast = Toast.makeText(this, "Successfully created.", Toast.LENGTH_LONG);

        finish();
    }
}
