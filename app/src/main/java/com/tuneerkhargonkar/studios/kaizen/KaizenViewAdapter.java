
package com.tuneerkhargonkar.studios.kaizen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.data.DataHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class KaizenViewAdapter extends RecyclerView.Adapter<KaizenViewAdapter.KaizenViewHolder> {

    private Context context;
    private List<Kaizen> kaizen_list;
    private  PlaceInfoListener infoListener;

    public KaizenViewAdapter(Context context,List<Kaizen> kaizen, PlaceInfoListener infoListener){
        this.context=context;
        this.kaizen_list=kaizen;
        this.infoListener = infoListener;

    }

    @NonNull
    @Override
    public KaizenViewAdapter.KaizenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_kaizen_view, parent,false);
        return new KaizenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KaizenViewAdapter.KaizenViewHolder holder, int position) {

        Kaizen kaizen = kaizen_list.get(position);

        //binding the data with the viewHolder views
        holder.tvd_title.setText(kaizen.getIdea_of_kaizen());
        holder.tvd_category.setText(kaizen.getCategory().toString());
        holder.tvd_pillar.setText(String.valueOf(kaizen.getPillar()));
        holder.tvd_machine.setText(String.valueOf(kaizen.getMachine_name()));

        if (kaizen.getMachine_name().length()!= 0)
            holder.tvd_machine.setText(kaizen.getMachine_name());
        else {
            holder.tvd_machine.setText("Analysis");
            holder.tvd_machine.setTypeface(holder.tvd_machine.getTypeface(), Typeface.ITALIC);
        }

        if (kaizen.getIdea_of_kaizen().length() != 0)
            holder.tvd_title.setText(kaizen.getIdea_of_kaizen());
        else {
            holder.tvd_title.setText("Idea");
            holder.tvd_title.setTypeface(holder.tvd_title.getTypeface(), Typeface.ITALIC);
        }

        if (kaizen.getPillar().length() != 0)
            holder.tvd_pillar.setText(kaizen.getPillar());
        else {
            holder.tvd_pillar.setText("Pillar");
            holder.tvd_pillar.setTypeface(holder.tvd_pillar.getTypeface(), Typeface.ITALIC);
        }

        if (kaizen.getCategory().toString().length() != 0)
            holder.tvd_category.setText(kaizen.getCategory().toString());
        else {
            holder.tvd_category.setText("Category");
            holder.tvd_category.setTypeface(holder.tvd_category.getTypeface(), Typeface.ITALIC);
        }

        String KID = kaizen_list.get(position).getKaizenid();
        Log.d("check",KID);
        String UID = kaizen_list.get(position).getUid();
        String dept = kaizen_list.get(position).getDepartment();

        holder.editKaizen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("checkEDBTN", "onMenuItemClick: pressed");
                onPopupMenuClick(holder.editKaizen,position,kaizen);
            }
        });

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Kaizen Final").child(dept).child(UID).child(KID);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Kaizen k_temp = new Kaizen();
                k_temp = snapshot.getValue(Kaizen.class);
                if(k_temp!=null) {
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
                        holder.checkedBy_indicator.setImageResource(R.drawable.res_circle_green);
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
        return kaizen_list.size();
    }

    public class KaizenViewHolder extends RecyclerView.ViewHolder {

        TextView tvd_title, tvd_category, tvd_pillar, tvd_machine;
        ImageView madeby_indicator,approvedBY_indicator,checkedBy_indicator,editKaizen;


        public KaizenViewHolder(@NonNull View itemView) {
            super(itemView);

            tvd_title = itemView.findViewById(R.id.tvd_title);
            tvd_category = itemView.findViewById(R.id.tvd_category);
            tvd_pillar = itemView.findViewById(R.id.tvd_pillar);
            tvd_machine = itemView.findViewById(R.id.tvd_machine);
            madeby_indicator = itemView.findViewById(R.id.madeByIndicator);
            approvedBY_indicator=itemView.findViewById(R.id.ApprovedByIndicator);
            checkedBy_indicator=itemView.findViewById(R.id.CheckdByIndicator);
            editKaizen = itemView.findViewById(R.id.editKaizen);
        }
    }
    private void editKaizen(Kaizen kaizen){
        Log.d("checkEDBTN", "onMenuItemClick: function called");
        Intent intent = new Intent(context,editkaizen.class);
        intent.putExtra("sampleObject", kaizen);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }


    public void onPopupMenuClick(View view,int position, Kaizen kaizen) {
        Log.d("checkEDBTN", "onMenuItemClick: onPopupButtonClick called");
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_edit, popup.getMenu());



        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.edit:
                        Log.d("checkEDBTN", "onMenuItemClick: Case called");
                        editKaizen(kaizen);
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }


}
