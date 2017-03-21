package com.mpease.ledger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mpease.ledger.model.Account;
import com.mpease.ledger.model.Balance;
import com.mpease.ledger.model.LedgerEntry;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mpease on 2/23/17.
 */

public class LedgerDatabaseHelper extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 2;
    private final static String DATABASE_NAME = "entries.db3";

    private final static String SQL_CREATE_ENTRIES = "CREATE TABLE entries(" +
            " id INTEGER PRIMARY KEY," +
            " date TEXT NOT NULL," +
            " processed BOOLEAN DEFAULT(0)" +
            " name TEXT )";
    private final static String SQL_CREATE_ACCOUNTS = "CREATE TABLE accounts(" +
            " id INTEGER PRIMARY KEY," +
            " name TEXT UNIQUE NOT NULL," +
            " alias TEXT UNIQUE NOT NULL," +
            " description TEXT)";

    private final static String SQL_CREATE_BALANCES = "CREATE TABLE balances(" +
            " entry_id INTEGER REFERENCES entries(id) ON DELETE CASCADE," +
            " account_id INTEGER REFERENCES accounts(id)," +
            " value REAL)";

    private final static String SQL_UPDATE_ENTRIES_V2 = "ALTER TABLE entries ADD COLUMN processed BOOLEAN DEFAULT(0)";

    private DateFormat dateFormat;
    private Context context;

    public LedgerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ACCOUNTS);
        db.execSQL(SQL_CREATE_BALANCES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL(SQL_UPDATE_ENTRIES_V2);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    public List<Balance> getBalances(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM balances JOIN accounts ON (account_id = accounts.id) WHERE entry_id = ?";
        String[] types = {String.valueOf(id)};

        Cursor cursor = db.rawQuery(sql, types);

        int accountIdColumn = cursor.getColumnIndex("account_id");
        int nameColumn = cursor.getColumnIndex("name");
        int aliasColumn = cursor.getColumnIndex("alias");
        int descriptionColumn = cursor.getColumnIndex("description");
        int valueColumn = cursor.getColumnIndex("value");

        List<Balance> balances = new ArrayList<>();

        while (cursor.moveToNext()) {
            int accountId = cursor.getInt(accountIdColumn);
            String name = cursor.getString(nameColumn);
            String alias = cursor.getString(aliasColumn);
            String description = cursor.getString(descriptionColumn);
            double value = cursor.getDouble(valueColumn);

            balances.add(new Balance(context, new Account(accountId, name, description, alias), value));
        }
        cursor.close();

        return balances;

    }

    public List<LedgerEntry> getLedgerEntries() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM entries WHERE NOT processed ORDER BY date ASC";
        List<LedgerEntry> entries = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, null);
        int idColumn = cursor.getColumnIndex("id");
        int nameColumn = cursor.getColumnIndex("name");
        int dateColumn = cursor.getColumnIndex("date");

        while(cursor.moveToNext()) {
            int id = cursor.getInt(idColumn);
            String dateString = cursor.getString(dateColumn);
            String name = cursor.getString(nameColumn);
            List<Balance> balances = getBalances(id);

            Date date;
            try {
                date = dateFormat.parse(dateString);
            } catch(ParseException e) {
                date = new Date();
            }

            entries.add(new LedgerEntry(context, date, name, balances, id));
        }
        cursor.close();

        return entries;
    }

    public void deleteEntry(LedgerEntry entry) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        String[] args = {String.valueOf(entry.getId())};
        db.delete("entries", "id = ?", args);

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public Account getAccount(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM accounts WHERE id = ?";
        String[] args = {String.valueOf(id)};

        Cursor cursor = db.rawQuery(sql, args);
        return getAccount(cursor);
    }

    private Account getAccount(Cursor cursor) {
        if (!cursor.moveToNext()) {
            return null;
        }
        int idColumn = cursor.getColumnIndex("id");
        int nameColumn = cursor.getColumnIndex("name");
        int aliasColumn = cursor.getColumnIndex("alias");
        int descriptionColumn = cursor.getColumnIndex("description");

        int id = cursor.getInt(idColumn);
        String name = cursor.getString(nameColumn);
        String description = cursor.getString(descriptionColumn);
        String alias = cursor.getString(aliasColumn);

        return new Account(id, name, description, alias);
    }

    public Account getAccount(String name, String alias) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM accounts WHERE name = ? OR alias = ?";
        String[] args = {name, alias};

        Cursor cursor = db.rawQuery(sql, args);
        Account a = getAccount(cursor);
        cursor.close();
        return a;
    }

    private void addBalance(SQLiteDatabase db, Balance b) {
        ContentValues values = b.getContentValues();

        db.insert("balances", null, values);
    }

    public void addEntry(LedgerEntry entry) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = entry.getContentValues();

        db.beginTransaction();
        long id = db.insert("entries", null, values);
        System.out.println("Add entry with " + entry.getBalances().size() + " balances.");

        for (Balance b : entry.getBalances()) {
            b.setEntryId((int) id);
            addBalance(db, b);
        }
        System.out.println("finish");

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public long createAccount(String name, String description, String alias) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("alias", alias);

        db.beginTransaction();
        long id = db.insert("accounts", null, values);
        db.setTransactionSuccessful();
        db.endTransaction();

        return id;
    }

    public Account getOrCreateAccount(String name, String description, String alias) {

        Account account = getAccount(name, alias);
        if (account != null) {
            return account;
        }

        long id = createAccount(name, description, alias);

        return new Account((int) id, name, description, alias);
    }

    public String[] getEntryNames() {

        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT DISTINCT name FROM entries";

        Cursor cursor = db.rawQuery(sql, new String[] {});

        int nameColumn = cursor.getColumnIndex("name");

        ArrayList<String> names = new ArrayList<>(cursor.getColumnCount());

        while (cursor.moveToNext()) {
            names.add(cursor.getString(nameColumn));
        }

        return names.toArray(new String[0]);
    }

    public Boolean setProcessed(SQLiteDatabase db, LedgerEntry item) {
        ContentValues values = new ContentValues(1);
        values.put("processed", true);
        try {
            db.update("entries", values, "id = ?", new String[]{"" + item.getId()});
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Boolean setProcessed(LedgerEntry item) {
        SQLiteDatabase db = getWritableDatabase();
        return setProcessed(db, item);
    }

    public Boolean setProcessed(List<LedgerEntry> items) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        for (LedgerEntry item : items) {
            if (!setProcessed(db, item)) {
                db.endTransaction();
                return false;
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    public List<Account> getAccounts() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM accounts";

        Cursor cursor = db.rawQuery(sql, null);
        int idColumn = cursor.getColumnIndex("id");
        int nameColumn = cursor.getColumnIndex("name");
        int aliasColumn = cursor.getColumnIndex("alias");
        int descriptionColumn = cursor.getColumnIndex("description");

        List<Account> accounts = new ArrayList<>();

        while(cursor.moveToNext()) {
            int id = cursor.getInt(idColumn);
            String alias = cursor.getString(aliasColumn);
            String name = cursor.getString(nameColumn);
            String description = cursor.getString(descriptionColumn);

            accounts.add(new Account(id, name, description, alias));
        }
        cursor.close();

        return accounts;
    }
}
