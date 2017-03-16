package com.mpease.ledger.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DecimalFormat;

/**
 * Created by mpease on 2/23/17.
 */

public class Balance {

    private Account account;
    private double value;
    private int entryId;
    private Context ctx;

    public Balance(Context ctx, Account account, double value) {
        this.account = account;
        this.value = value;
        this.entryId = -1;
        this.ctx = ctx;
    }

    public void setEntryId(int id) {
        entryId = id;
    }

    public String getNameOrAlias() {
        if (account == null) {
            return "";
        }

        String alias = account.getAlias();
        if (alias == null || alias.isEmpty()) {
            return account.getName();
        } else {
            return alias;
        }
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setName(String name) {
        this.account.setName(name);
    }

    public String getAlias() {
        return account.getAlias();
    }

    public String getDescription() {
        return account.getDescription();
    }

    public Account getAccount() {
        return account;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Balance balance = (Balance) o;

        if (Double.compare(balance.value, value) != 0) return false;
        return account.equals(balance.account);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = account.hashCode();
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("account_id", account.getId());
        values.put("entry_id", entryId);
        values.put("value", value);

        return values;
    }

    public String getExportString() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        StringBuilder builder = new StringBuilder();
        builder.append("\t");
        builder.append(account.getAliasOrName());
        builder.append("\t");
        DecimalFormat df = new DecimalFormat(sharedPreferences.getString("pref_number_format", ""));
        builder.append(df.format(value));
        builder.append(" ");
        builder.append(sharedPreferences.getString("pref_currency_symbol", ""));

        return builder.toString();
    }
}
