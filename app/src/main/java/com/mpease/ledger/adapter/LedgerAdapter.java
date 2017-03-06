package com.mpease.ledger.adapter;

import android.content.Context;
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

/**
 * Created by mpease on 2/23/17.
 */

public class LedgerAdapter
        extends BaseAdapter {

    private List<LedgerEntry> entries;
    private DateFormat dateFormat;
    private Context context;
    private LedgerDatabaseHelper dbHelper;


    public LedgerAdapter(Context context, LedgerDatabaseHelper dbHelper) {
        super();
        this.context = context;
        this.dbHelper = dbHelper;
        this.entries = dbHelper.getLedgerEntries();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void setEntries(List<LedgerEntry> entries) {
        this.entries = entries;
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        entries = dbHelper.getLedgerEntries();
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LedgerEntry entry = entries.get(position);

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.ledger_entry, null);
        }

        TextView date = (TextView) convertView.findViewById(R.id.list_date);
        date.setText(dateFormat.format(entry.getDate()));
        TextView name = (TextView) convertView.findViewById(R.id.list_name);
        name.setText(entry.getName());
        TextView value = (TextView) convertView.findViewById(R.id.list_value);
        value.setText(entry.getValueString() + " €");

        CheckBox box = (CheckBox) convertView.findViewById(R.id.checkBox);
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
}
