package com.mpease.ledger.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.Preference;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mpease on 2/23/17.
 */

public class LedgerEntry {

    private Integer id;
    private Date date;
    private String name;
    private List<Balance> balances;
    private DateFormat dateFormat;
    private Context context;


    public LedgerEntry(Context context, Date date, String name, List<Balance> balances, Integer id ) {
        this.context = context;
        this.id = id;
        this.date = date;
        this.name = name;
        this.balances = balances;
        setDateFormat();
    }

    private void setDateFormat() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        dateFormat = new SimpleDateFormat(sharedPreferences.getString("pref_date_format", ""));
    }

    public Integer getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public double getValue() {
        double value = 0;

        if (!balances.isEmpty()) {
            value = balances.get(0).getValue();
        }

        return value;
    }

    public String getValueString() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        DecimalFormat df = new DecimalFormat(sharedPreferences.getString("pref_number_format", ""));

        return df.format(this.getValue());
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("date", dateFormat.format(date));
        values.put("name", name);


        return values;
    }

    public String getExportString() {
        StringBuilder builder = new StringBuilder();
        builder.append(dateFormat.format(date));
        builder.append(" ");
        builder.append(name);
        builder.append("\n");

        for (Balance b : balances) {
            builder.append(b.getExportString());
            builder.append("\n");
        }
        builder.append("\n");

        return builder.toString();
    }
}
