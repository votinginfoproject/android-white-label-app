package com.votinginfoproject.VotingInformationProject.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;

/**
 * Created by marcvandehey on 4/4/16.
 */
public class HomePickerAdapter extends ArrayAdapter<String> {
    private int highlightedItem = 0;

    public HomePickerAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void highlightItemAtIndex(int index) {
        highlightedItem = index;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(android.R.layout.simple_selectable_list_item, parent, false);


            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(android.R.id.text1);

            convertView.setTag(viewHolder);

            if (position == highlightedItem) {
                convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.list_selected));
            } else {
                convertView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            }

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String state = getItem(position);
        if (state != null) {
            viewHolder.itemView.setText(state);
        }

        return convertView;
    }

    private static class ViewHolder {
        private TextView itemView;
    }
}
