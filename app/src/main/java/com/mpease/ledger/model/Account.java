package com.mpease.ledger.model;

import android.content.ContentValues;

/**
 * This implements an ledger account.
 */

public class Account {

    private int id;
    private String name;
    private String description;
    private String alias;

    /**
     * Constructs an account.
     * @param id Id of the account or 0 if not in database.
     * @param name Name of the account.
     * @param description Description of the account.
     * @param alias Alias of the account.
     */
    public Account(int id, String name, String description, String alias) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.alias = alias;
    }

    /**
     * Returns the id of the account.
     * @return Id of this account.
     */
    public int getId() {

        return id;
    }

    /**
     * Returns the name of this account.
     * @return Name of the account.
     */
    public String getName() {

        return name;
    }

    /**
     * Returns the description of this account.
     * @return Description of the account.
     */
    public String getDescription() {

        return description;
    }

    /**
     * Returns the alias of this account.
     * @return Alias of this account.
     */
    public String getAlias() {

        return alias;
    }

    /**
     * Returns the alias of this account.
     * If the alias is empty the name will be returned.
     * @return Alias or name of this account.
     */
    public String getAliasOrName() {
        if (alias == null || alias.isEmpty()) {
            return name;
        } else {
            return alias;
        }
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

    /**
     * Sets the name of this account.
     * @param name Name to set.
     */
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

    /**
     * Returns the content values of this account.
     * This is needed for database inserts.
     * @return Content values of this account.
     */
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("alias", alias);
        values.put("id", id);
        values.put("description", description);

        return values;
    }

    public String toString() {

        return getAliasOrName();
    }
}
