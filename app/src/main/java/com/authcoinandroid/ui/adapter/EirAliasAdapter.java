package com.authcoinandroid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.authcoinandroid.R;

import java.util.List;

public class EirAliasAdapter extends ArrayAdapter<String> {

    public EirAliasAdapter(Context context, List<String> eirAliases) {
        super(context, 0, eirAliases);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String eirAlias = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_eir, parent, false);
        }
        TextView eirAliasTextView = (TextView) convertView.findViewById(R.id.eir_alias);
        eirAliasTextView.setTag(position);
        eirAliasTextView.setText(eirAlias);
        return convertView;
    }
}
