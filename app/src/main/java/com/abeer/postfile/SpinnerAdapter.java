package com.abeer.postfile;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



/**
 * Created by Pawan on 05-01-2017.
 */

public class SpinnerAdapter extends ArrayAdapter<SpinnerModelView> {

    private Context context;
    private SpinnerModelView[] myObjs;

    public SpinnerAdapter(Context context, int textViewResourceId,
                          SpinnerModelView[] myObjs) {
        super(context, textViewResourceId, myObjs);
        this.context = context;
        this.myObjs = myObjs;
    }

    public int getCount() {
        return myObjs.length;
    }

    public SpinnerModelView getItem(int position) {
        return myObjs[position];
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setText(myObjs[position].getName());
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setText(myObjs[position].getName());
        return label;
    }
}
