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
        extends BaseAdapter {

    private List<LedgerEntry> entries;
    private DateFormat dateFormat;
    private Context context;
    private LedgerDatabaseHelper dbHelper;
    private Map<Integer, Boolean> selected;


    public LedgerAdapter(Context context, LedgerDatabaseHelper dbHelper) {
        super();
        this.context = context;
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        this.dbHelper = dbHelper;
        this.entries = dbHelper.getLedgerEntries();
        selected = new TreeMap<>();
        dateFormat = new SimpleDateFormat(sharedPreferences
                .getString("pref_date_format", ""));
    }

    public void setEntries(List<LedgerEntry> entries) {
        this.entries = entries;
    }

    public void setCheckboxes(Map<Integer, Boolean> map) {
        this.selected = map;

    }

    public void setAll() {
        Boolean select = !hasSelection();

        for (int i = 0; i < entries.size(); i++) {
            selected.put(i, select);
        }

        notifyDataSetChanged();
    }

    public void setSelected(Integer position) {
        this.selected.put(position, true);
    }

    public void unsetSelected(Integer position) {
        this.selected.remove(position);
    }

    public Map<Integer, Boolean> getSelected() {
        return selected;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        entries = dbHelper.getLedgerEntries();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        dateFormat = new SimpleDateFormat(sharedPreferences.getString(
                "pref_date_format", ""
        ));
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public LedgerEntry getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public Boolean isChecked(Integer position) {
        if (selected.containsKey(position)) {
            return selected.get(position);
        } else {
            return false;
        }
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
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    public boolean hasSelection() {
        return selected.containsValue(true);
    }
}
