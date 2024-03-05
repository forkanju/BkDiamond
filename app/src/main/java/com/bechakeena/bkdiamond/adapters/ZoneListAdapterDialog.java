package com.bechakeena.bkdiamond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.models.Zone;

import java.util.List;

public class ZoneListAdapterDialog extends BaseAdapter {

    private List<Zone> zoneList;

    private LayoutInflater layoutInflater;

    public ZoneListAdapterDialog(Context context, List<Zone> zoneList) {
        this.zoneList = zoneList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return zoneList.size();
    }

    @Override
    public Object getItem(int position) {
        return zoneList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CustomListAdapterDialog.ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_dialog, null);
            holder = new CustomListAdapterDialog.ViewHolder();
            holder.unitView = (TextView) convertView.findViewById(R.id.txt_title);
            convertView.setTag(holder);
        } else {
            holder = (CustomListAdapterDialog.ViewHolder) convertView.getTag();
        }

        holder.unitView.setText(zoneList.get(position).getName());

        return convertView;
    }

    static class ViewHolder {
        TextView unitView;
        TextView quantityView;
    }

}
