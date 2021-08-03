package com.tuneerkhargonkar.studios.kaizen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class KaizenApprovalViewAdapter extends RecyclerView.Adapter<KaizenApprovalViewAdapter.KaizenApprovalHolder> {

    private Context context;
    private List<Kaizen> kaizenApprovalList;

    public KaizenApprovalViewAdapter(Context context,List<Kaizen> kaizenApprovalList){
        this.context=context;
        this.kaizenApprovalList=kaizenApprovalList;

    }

    @NonNull
    @Override
    public KaizenApprovalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kaizen_to_be_approved_view, parent, false);

        return new KaizenApprovalHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull KaizenApprovalHolder holder, int position) {
        holder.kaizen_idea.setText(kaizenApprovalList.get(position).getPillar());

        Kaizen kaizen = kaizenApprovalList.get(position);

        String KID = kaizenApprovalList.get(position).getKaizenid();
        String UID = kaizenApprovalList.get(position).getUid();
        String dept = kaizenApprovalList.get(position).getDepartment();



        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Kaizen").child(dept).child(UID).child(KID);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long children = snapshot.getChildrenCount();
                Log.d("check", "onDataChange: " + Long.toString(children));
                Kaizen k_temp = new Kaizen();
                k_temp = snapshot.getValue(Kaizen.class);
                if (k_temp != null) {
                    Log.d("checkKiazen", "onDataChange: " + k_temp.toString());
                    boolean madeBy_test_boolean = k_temp.isMADEBY_boolean();
                    boolean approvedBy_test_boolean = k_temp.isAPPROVEDBY_boolean();
                    boolean checkedBy_test_boolean = k_temp.isCHECKEDBY_boolean();


                    if (madeBy_test_boolean == true) {
                        holder.madeby_indicator.setImageResource(R.drawable.res_circle_green);
                    }

                    if (approvedBy_test_boolean == true) {
                        holder.approvedBY_indicator.setImageResource(R.drawable.res_circle_green);
                    }

                    if (checkedBy_test_boolean == true) {
                        holder.checkedByIndicator.setImageResource(R.drawable.res_circle_green);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return kaizenApprovalList.size();
    }


    public class KaizenApprovalHolder extends RecyclerView.ViewHolder {
        TextView kaizen_idea;
        ImageView madeby_indicator,approvedBY_indicator,checkedByIndicator;

        public KaizenApprovalHolder(@NonNull View itemView) {
            super(itemView);

            kaizen_idea = itemView.findViewById(R.id.kaizenIdea_Approved);
            madeby_indicator = itemView.findViewById(R.id.madeByIndicator_Approved);
            approvedBY_indicator=itemView.findViewById(R.id.madeByIndicator_Approved);
            checkedByIndicator = itemView.findViewById(R.id.CheckdByIndicator_Approved);
        }
    }


}
