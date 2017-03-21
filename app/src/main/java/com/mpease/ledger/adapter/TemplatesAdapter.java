package com.mpease.ledger.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mpease.ledger.LedgerDatabaseHelper;
import com.mpease.ledger.R;
import com.mpease.ledger.model.Account;
import com.mpease.ledger.model.Template;

import java.util.List;

/**
 * Created by mpease on 3/21/17.
 */

public class TemplatesAdapter extends GenericAdapter<Template> {


    public TemplatesAdapter(Context context, LedgerDatabaseHelper dbHelper) {
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
            convertView = View.inflate(context, R.layout.templates_item, null);
        }

        Template entry = entries.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.list_template_name);
        name.setText(entry.getName());

        CheckBox box = (CheckBox) convertView.findViewById(R.id.list_template_checkBox);
        box.setChecked(isChecked(position));
        box.setTag(position);

        return convertView;
    }
}
