package com.mpease.ledger.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.BoolRes;
import android.support.annotation.RequiresApi;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mpease.ledger.LedgerDatabaseHelper;
import com.mpease.ledger.R;
import com.mpease.ledger.model.LedgerEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by mpease on 2/23/17.
 */

public class LedgerAdapter
        extends GenericAdapter<LedgerEntry> {

    public LedgerAdapter(Context context, LedgerDatabaseHelper dbHelper) {
        super(context, dbHelper);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        LedgerEntry entry = entries.get(position);

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.ledger_entry, null);
        }

        TextView date = (TextView) convertView.findViewById(R.id.list_date);
        date.setText(dateFormat.format(entry.getDate()));
        TextView name = (TextView) convertView.findViewById(R.id.list_name);
        name.setText(entry.getName());
        TextView value = (TextView) convertView.findViewById(R.id.list_value);
        value.setText(entry.getValueString() + " "
                + sharedPreferences.getString("pref_currency_symbol", ""));

        CheckBox box = (CheckBox) convertView.findViewById(R.id.checkBox);
        box.setChecked(isChecked(position));
        box.setTag(position);

        return convertView;
    }

    @Override
    public List<LedgerEntry> getEntries() {
        return dbHelper.getLedgerEntries();
    }
}