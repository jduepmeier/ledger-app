package com.mpease.ledger.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.mpease.ledger.R;
import com.mpease.ledger.activities.SettingsActivity;
import com.mpease.ledger.model.Balance;
import com.mpease.ledger.model.LedgerEntry;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by mpease on 2/23/17.
 */

public class BalanceAdapter
        extends BaseAdapter {

    private List<Balance> balances;
    private DateFormat dateFormat;
    private Context context;


    public BalanceAdapter(Context context, List<Balance> balances) {
        this.context = context;
        this.balances = balances;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        dateFormat = new SimpleDateFormat(sharedPreferences.getString("pref_date_format", ""));
    }

    public void setEntries(List<Balance> balances) {
        this.balances = balances;
    }

    public void addEmpty() {
        Balance b = new Balance(context, null, 0.00);
        balances.add(b);
    }

    @Override
    public int getCount() {
        return balances.size();
    }

    @Override
    public Object getItem(int position) {
        return balances.get(position);
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Balance balance = balances.get(position);

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.edit_balances, null);
        }
        DecimalFormat df = new DecimalFormat(sharedPreferences.getString("pref_number_format", ""));

        AutoCompleteTextView account_name = (AutoCompleteTextView) convertView.findViewById(R.id.balance_name);
        if (account_name != null && account_name.getText().length() < 1) {
            account_name.setText(balance.getNameOrAlias());
        }

        EditText value = (EditText) convertView.findViewById(R.id.balance_value);
        if (value != null && value.getText().length() < 1) {
            value.setText(df.format(balance.getValue()));
        }

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
        return balances.isEmpty();
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
