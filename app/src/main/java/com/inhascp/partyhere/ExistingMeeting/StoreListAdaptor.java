package com.inhascp.partyhere.ExistingMeeting;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.inhascp.partyhere.R;

import java.util.ArrayList;

public class StoreListAdaptor extends RecyclerView.Adapter<StoreListAdaptor.ViewHolder> {

    private ArrayList<StoreInf> mData = null ;

    private Context context;


    /* 생성자에서 데이터 리스트 객체를 전달받음.
    public StoreListAdaptor(Context context, ArrayList<String> textList){

        this.context = context;
        this.mData = textList;
    }

     */

    public StoreListAdaptor(Context context,  ArrayList<StoreInf> stores){

        this.context = context;
        this.mData = stores;
    }


    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(StoreListAdaptor.ViewHolder holder, int position) {

        String text;
        if (mData.size() > position) {

            holder.storeInf = mData.get(position);
            holder.storeAddress.setText(holder.storeInf.getVicinity());
            holder.storeName.setText(holder.storeInf.getName());
            //holder.storeName.setText(mData.get(position).getName()) ;
            //holder.storeAddress.setText(mData.get(position).getVicinity());
        }


    }



    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public StoreListAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Context context = parent.getContext() ;
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.storelist_view,parent,false);

        StoreListAdaptor.ViewHolder vh = new StoreListAdaptor.ViewHolder(view) ;

        return vh ;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView storeName;
        TextView storeAddress;
        StoreInf storeInf;

        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            storeName = itemView.findViewById(R.id.store_name);
            storeAddress = itemView.findViewById(R.id.store_address);
            storeInf = new StoreInf();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, StoreDetailActivity.class);
                    //intent.putExtra(("storeInf"),storeInf);
                    intent.putExtra("storeName", storeInf.getName());
                    intent.putExtra("storePlaceId", storeInf.getPlaceId());
                    intent.putExtra("storeType", storeInf.getType());
                    intent.putExtra("storeVicinity", storeInf.getVicinity());


                    ((Activity) context).startActivityForResult(intent, 1);


                }
            });
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {


        return mData.size() ;
    }

    public void setNewList(ArrayList<StoreInf> stores){

        mData = stores;
        notifyDataSetChanged();
    }
}