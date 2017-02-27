package com.mpease.ledger.model;

import android.content.ContentValues;
import android.content.Intent;

import java.text.DateFormat;
import java.text.DecimalFormat;
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

    public LedgerEntry() {
        id = -1;
        name = "";
        date = new Date();
        balances = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public LedgerEntry(Date date, String name) {
        id = -1;
        this.date = date;
        this.name = name;
        balances = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public LedgerEntry(Date date, String name, List<Balance> balances, Integer id ) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.balances = balances;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public LedgerEntry(Date date, String name, List<Balance> balances, Integer id, DateFormat dateFormat) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.balances = balances;
        this.dateFormat = dateFormat;
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
        DecimalFormat df = new DecimalFormat("#.00");

        return df.format(this.getValue());
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("date", dateFormat.format(date));
        values.put("name", name);


        return values;
    }
}
