package com.mpease.ledger.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.mpease.ledger.LedgerDatabaseHelper;
import com.mpease.ledger.R;
import com.mpease.ledger.model.Account;
import com.mpease.ledger.model.Template;

import java.util.List;

/**
 * Created by mpease on 3/21/17.
 */

public class TemplateSpinnerAdapter extends GenericAdapter<Template> implements SpinnerAdapter {

    public TemplateSpinnerAdapter(Context context, LedgerDatabaseHelper dbHelper) {
        super(context, dbHelper);
    }

    @Override
    public List<Template> getEntries() {
        return dbHelper.getTemplates();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.template_spinner_item, null);
        }

        Template entry = entries.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.spinner_template_name);
        name.setText(entry.getName());

        return convertView;
    }
}
