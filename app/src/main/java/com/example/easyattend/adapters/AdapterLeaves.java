package com.example.easyattend.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyattend.R;
import com.example.easyattend.models.LeaveModel;
import com.example.easyattend.models.OnItemClick;
import com.example.easyattend.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdapterLeaves extends RecyclerView.Adapter<AdapterLeaves.ViewHolder> {
    Context context;
    List<LeaveModel> list;
    OnItemClick onItemClick;
    DatabaseReference databaseReference;

    public AdapterLeaves(Context context, List<LeaveModel> list, DatabaseReference databaseReference, OnItemClick click) {
        this.context = context;
        this.list = list;
        this.databaseReference = databaseReference;
        this.onItemClick = click;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_leave, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int p) {
        LeaveModel data = list.get(p);
        holder.tvSubject.setText("Subject Name: "+data.getSubject());
        holder.tvLeaveType.setText("Leave Type: "+data.getType());
        holder.tvLeaveDate.setText("Leave Date: "+data.getDate());
        holder.tvLeaveReason.setText("Leave Reason: "+data.getReason());
        holder.tvStatus.setText(data.getStatus());
        if (data.getStatus().contentEquals("Pending")){
            holder.tvStatus.setTextColor(context.getColor(R.color.yellow));
        }else if (data.getStatus().contentEquals("Approved")){
            holder.tvStatus.setTextColor(context.getColor(R.color.green));
        }else if (data.getStatus().contentEquals("Rejected")){
            holder.tvStatus.setTextColor(context.getColor(R.color.red));
        }
        if (data.getUserId().contentEquals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.cvImage.setVisibility(View.VISIBLE);
            Glide.with(holder.ivImage)
                    .load(data.getImage())
                    .centerCrop()
                    .into(holder.ivImage);
        }
        databaseReference.child(data.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel model = snapshot.getValue(UserModel.class);
                    holder.tvUserName.setText("Student Name: "+model.getUserName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onClick(p);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvSubject, tvLeaveType, tvLeaveDate, tvLeaveReason, tvStatus;
        CardView cvImage;
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvLeaveType = itemView.findViewById(R.id.tvLeaveType);
            tvLeaveDate = itemView.findViewById(R.id.tvLeaveDate);
            tvLeaveReason = itemView.findViewById(R.id.tvLeaveReason);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            cvImage = itemView.findViewById(R.id.cvImage);
            ivImage = itemView.findViewById(R.id.ivImage);

        }
    }

}