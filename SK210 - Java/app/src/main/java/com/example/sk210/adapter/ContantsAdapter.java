package com.example.sk210.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sk210.R;
import com.example.sk210.util.Constants;

import java.util.List;

public class ContantsAdapter extends ArrayAdapter<Constants> {
    private List<Constants> constantsList;

    public ContantsAdapter(Context context, int resource, List<Constants> constants) {
        super(context, resource, constants);
        this.constantsList = constants;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            Context ctx = getContext();
            LayoutInflater vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listconstants, null);
        }
        Constants constants = constantsList.get(position);

        if (constants != null) {
            ((TextView) v.findViewById(R.id.txtProjeto)).setText(constants.getNome());
            ((ImageView) v.findViewById(R.id.imgIcon)).setImageResource(constants.getImg());
        }
        return v;
    }
}
