package com.example.despensadesonia.DataClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.despensadesonia.R;

import java.util.List;

public class ArticuloAdapter extends BaseAdapter {
    //ADAPTADOR SIMPLE PARA CONVERTIR LISTAS DE ARTÍCULOS EN COMPATIBLES CON LISTVIEW
    private LayoutInflater inflater;
    private List<Articulo> itemList;
    public ArticuloAdapter(Context pcontext, List<Articulo> pitemList) {
        this.itemList = pitemList;
        inflater = (LayoutInflater) pcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.custom_card, null);
        TextView nombreProd = view.findViewById(R.id.nombreProd);
        TextView cantAct = view.findViewById(R.id.cantAct);
        TextView cantMin = view.findViewById(R.id.cantMin);
        TextView fechaUltCompra = view.findViewById(R.id.fechaUltCompra);
        TextView fechaCaducidadTop = view.findViewById(R.id.fechaCaducidadTop);

        nombreProd.setText(itemList.get(position).nombreProducto);
        cantAct.setText(itemList.get(position).cantAct);
        cantMin.setText(itemList.get(position).cantMin);
        fechaUltCompra.setText(itemList.get(position).fechaUltCompra);
        fechaCaducidadTop.setText(itemList.get(position).fechaCaducidadTop);
        return view;
    }
}
