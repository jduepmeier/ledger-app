package com.mpease.ledger.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mpease.ledger.LedgerDatabaseHelper;
import com.mpease.ledger.R;
import com.mpease.ledger.model.Account;
import com.mpease.ledger.model.LedgerEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by mpease on 2/23/17.
 */

public class AccountsAdapter
        extends GenericAdapter<Account> {


    public AccountsAdapter(Context context, LedgerDatabaseHelper dbHelper) {
        super(context, dbHelper);
    }

    @Override
    public List<Account> getEntries() {
        return dbHelper.getAccounts();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.accounts_item, null);
        }

        Account entry = entries.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.list_account_name);
        name.setText(entry.getName());
        TextView alias = (TextView) convertView.findViewById(R.id.list_account_alias);
        alias.setText(entry.getAlias());

        CheckBox box = (CheckBox) convertView.findViewById(R.id.list_account_checkBox);
        box.setChecked(isChecked(position));
        box.setTag(position);

        return convertView;
    }
}