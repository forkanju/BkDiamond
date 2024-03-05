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

public class ZoneAdapter extends BaseAdapter {

    private Context mContext;
    private List<Zone> zoneList;
    private LayoutInflater mLayoutInflater;

    public ZoneAdapter(Context context, List<Zone> zoneList) {
        this.mContext = context;
        this.zoneList = zoneList;
        mLayoutInflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return zoneList.size();
    }

    @Override
    public Object getItem(int i) {
        return zoneList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = mLayoutInflater.inflate(R.layout.recycler_zone_item, null);
        TextView txt_name = (TextView) view.findViewById(R.id.txt_name);
        txt_name.setText(zoneList.get(i).getName());
        return view;
    }
}
