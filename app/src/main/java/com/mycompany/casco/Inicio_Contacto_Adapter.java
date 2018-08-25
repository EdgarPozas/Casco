package com.mycompany.casco;
import android.widget.*;
import android.view.*;
import android.app.*;
import java.util.*;
import android.content.*;
import android.view.View.*;

public class Inicio_Contacto_Adapter extends BaseAdapter
{
    private Inicio activity;
	private ArrayList<Inicio_Contacto_Item> items= new  ArrayList<Inicio_Contacto_Item>();
	private int pos;
	public Inicio_Contacto_Adapter(Inicio activity,ArrayList<Inicio_Contacto_Item> items){
		this.activity=activity;
		this.items=items;
	}
	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		View v= p2;
		if(p2==null){
			LayoutInflater inf=(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v=inf.inflate(R.layout.inicio_contacto_item,null);
		}
		Inicio_Contacto_Item it=items.get(p1);
		TextView t_mom=(TextView) v.findViewById(R.id.txt_contacto_nombre);
		if(t_mom!=null)
			t_mom.setText(it.nombre);

		TextView t_mac=(TextView) v.findViewById(R.id.txt_contacto_numero);
		if(t_mac!=null)
			t_mac.setText(it.telefono);
	    
	    ImageButton b_llamar=(ImageButton) v.findViewById(R.id.btn_contacto_llamar);
		b_llamar.setTag(p1);
		
		ImageButton b_actualizar=(ImageButton) v.findViewById(R.id.btn_contacto_actualizar);
		b_actualizar.setTag(p1);
		
		ImageButton b_eliminar=(ImageButton) v.findViewById(R.id.btn_contacto_eliminar);
		b_eliminar.setTag(p1);
		//if(b_llamar!=null)
		//	b_llamar.setOnClickListener(this);

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
