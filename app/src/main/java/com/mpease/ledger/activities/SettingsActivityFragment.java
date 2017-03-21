package com.mpease.ledger.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mpease.ledger.LedgerDatabaseHelper;
import com.mpease.ledger.R;
import com.mpease.ledger.model.Account;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragment {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        LedgerDatabaseHelper dbHelper = new LedgerDatabaseHelper(getContext());

        List<Account> accounts = dbHelper.getAccounts();

        if (accounts.size() < 2) {
            accounts.add(dbHelper.getOrCreateAccount("Expenses:Dummy1", "First Dummy Accoount", "Dummy1"));
            accounts.add(dbHelper.getOrCreateAccount("Expenses:Dummy2", "Second Dummy Account", "Dummy2"));
        }

        CharSequence[] accountNames = new CharSequence[accounts.size()];
        CharSequence[] accountValues = new CharSequence[accounts.size()];

        for (int i = 0; i < accounts.size(); i++) {
            Account a = accounts.get(i);
            accountNames[i] = a.getAliasOrName();
            accountValues[i] = String.valueOf(a.getId());
        }

        ListPreference defaultAccount1 = (ListPreference) findPreference("pref_default_account1");
        defaultAccount1.setDefaultValue("1");
        defaultAccount1.setEntries(accountNames);
        defaultAccount1.setEntryValues(accountValues);


        ListPreference defaultAccount2 = (ListPreference) findPreference("pref_default_account2");
        defaultAccount2.setDefaultValue("2");
        defaultAccount2.setEntries(accountNames);
        defaultAccount2.setEntryValues(accountValues);
    }
}
