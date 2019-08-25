package com.example.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.Class.Define;
import com.example.Class.ThoiTiet;
import com.example.weatherforme.R;

import java.util.ArrayList;

public class CustomAdapter  extends BaseAdapter {
    Context context;
    ArrayList<ThoiTiet> arrayList;
    public CustomAdapter(Context context, ArrayList<ThoiTiet> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.list_thoitiet,null);
        //goi class thời tiết
        ThoiTiet thoiTiet = arrayList.get(i);
        final TextView tvdate=(TextView) view.findViewById(R.id.tvdate);
        ImageView imgicon=(ImageView) view.findViewById(R.id.imicon);
        TextView tvmax=(TextView) view.findViewById(R.id.tvmax);
        TextView tvmin=(TextView) view.findViewById(R.id.tvmin);
        TextView tvstatus=(TextView) view.findViewById(R.id.tvstatus);
        tvdate.setText(thoiTiet.Day);
//        // thư viện load ảnh Glide
        Glide.with(context).load(Define.picture+thoiTiet.Image+".png").apply(new RequestOptions()
                .override(140,100).fitCenter()).into(imgicon);
        tvmax.setText(thoiTiet.TempMax+"°C");
        tvmin.setText(thoiTiet.TempMin+"°C");
        tvstatus.setText(thoiTiet.Status);
        return view;
    }
}
