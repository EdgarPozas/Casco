package com.mycompany.casco;
import android.widget.*;
import android.view.*;
import android.app.*;
import java.util.*;
import android.content.*;

public class Seleccion_Dispositivo_Adapter extends BaseAdapter
{
    private Activity activity;
	private ArrayList<Seleccion_Dispositivo_Item> items;
	public Seleccion_Dispositivo_Adapter(Activity activity,ArrayList<Seleccion_Dispositivo_Item> items){
		this.activity=activity;
		this.items=items;
	}
	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		View v= p2;
		if(p2==null){
			LayoutInflater inf=(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v=inf.inflate(R.layout.seleccion_dispositivo_item,null);
		}
		
		Seleccion_Dispositivo_Item it=items.get(p1);
		TextView t_mom=(TextView) v.findViewById(R.id.txt_nombre);
		t_mom.setText(it.nombre);
		
		TextView t_mac=(TextView) v.findViewById(R.id.txt_mac);
		t_mac.setText(it.mac);
		
		return v;
	}

	@Override
	public long getItemId(int p1)
	{
		return p1;
	}

	@Override
	public Object getItem(int p1)
	{
		return items.get(p1);
	}
	
	@Override
	public int getCount()
	{
		return items.size();
	}

	
}
