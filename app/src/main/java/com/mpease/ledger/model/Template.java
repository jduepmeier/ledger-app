package com.mpease.ledger.model;

import android.content.ContentValues;

/**
 * Created by mpease on 3/21/17.
 */

public class Template {

    private Integer id;
    private String name;
    private Integer account1;
    private Integer account2;

    public Template(Integer id, String name, Integer account1, Integer account2) {
        this.id = id;
        this.name = name;
        this.account1 = account1;
        this.account2 = account2;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAccount1() {
        return account1;
    }

    public Integer getAccount2() {
        return account2;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (id > 0) {
            values.put("id", id);
        }
        values.put("name", name);
        values.put("account1", account1);
        values.put("account2", account2);

        return values;
    }

    public String toString() {
        return name;
    }
}
