package com.mpease.ledger.model;

import android.content.ContentValues;

/**
 * Created by mpease on 2/23/17.
 */

public class Account {

    private int id;
    private String name;
    private String description;
    private String alias;

    public Account(int id, String name, String description, String alias) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.alias = alias;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (id != account.id) return false;
        if (!name.equals(account.name)) return false;
        if (description != null ? !description.equals(account.description) : account.description != null)
            return false;
        return alias != null ? alias.equals(account.alias) : account.alias == null;

    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("alias", alias);
        values.put("id", id);
        values.put("description", description);

        return values;
    }
}
