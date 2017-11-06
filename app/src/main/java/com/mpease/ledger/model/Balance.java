package com.mpease.ledger.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DecimalFormat;

/**
 * This represents a balance.
 * Each balance has a account and a value.
 */

public class Balance {

    private Account account;
    private double value;
    private int entryId;
    private Context ctx;

    /**
     * Constructs a balance.
     * @param ctx Context object.
     * @param account Account of this balance.
     * @param value Value of this balance.
     */
    public Balance(Context ctx, Account account, double value) {
        this.account = account;
        this.value = value;
        this.entryId = -1;
        this.ctx = ctx;
    }

    /**
     * Sets the entry id of this balance.
     * This is only important for database entries.
     * @param id Id of the corresponding ledger entry.
     */
    public void setEntryId(int id) {

        entryId = id;
    }

    /**
     * Returns the alias of the account in the balance.
     * If the alias is empty the account name will returned.
     * @return Alias or name of the account.
     */
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

    /**
     * Sets the name of the account.
     * @param name Name of the account to set.
     */
    public void setName(String name) {
        this.account.setName(name);
    }


    /**
     * Return the account of this balance.
     * @return Account of this balance.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Returns the value of the balance.
     * @return value of the balance.
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the value of this balance.
     * @param value value to set.
     */
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Balance balance = (Balance) o;

        if (Double.compare(balance.value, value) != 0) {
            return false;
        }
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

    /**
     * Returns the content values for database actions.
     * @return Content values.
     */
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("account_id", account.getId());
        values.put("entry_id", entryId);
        values.put("value", value);

        return values;
    }

    /**
     * Returns the balance as ledger string for export.
     * @return Ledger string format.
     */
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
