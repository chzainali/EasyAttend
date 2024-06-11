package com.example.easyattend.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyattend.R;
import com.example.easyattend.models.AttendanceModel;
import com.example.easyattend.models.OnItemClick;
import com.example.easyattend.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdapterAttendance extends RecyclerView.Adapter<AdapterAttendance.ViewHolder> {
    Context context;
    List<AttendanceModel> list;
    String checkFrom;

    public AdapterAttendance(Context context, List<AttendanceModel> list, String checkFrom) {
        this.context = context;
        this.list = list;
        this.checkFrom = checkFrom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int p) {
        AttendanceModel data = list.get(p);

        holder.tvStatus.setText(data.getStatus());
        if (checkFrom.contentEquals("Lecturer")){
            getUserName(data.getUserId(), holder.tvDate);
        }else{
            holder.tvDate.setText(data.getDate());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);

        }
    }

    private void getUserName(String userId, TextView textView){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("AllUsers");
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel model = snapshot.getValue(UserModel.class);
                    textView.setText(model.getUserName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

}