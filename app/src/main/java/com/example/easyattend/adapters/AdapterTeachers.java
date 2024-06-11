package com.example.easyattend.adapters;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyattend.R;
import com.example.easyattend.models.UserModel;

import java.util.List;

public class AdapterTeachers extends RecyclerView.Adapter<AdapterTeachers.ViewHolder> {
    Context context;
    List<UserModel> list;

    public AdapterTeachers(Context context, List<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_teachers, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int p) {
        UserModel data = list.get(p);

        holder.teacherName.setText(data.getUserName()+"("+data.getTeacherSubject()+")");
        holder.teacherEmail.setText(data.getUserEmail());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView teacherName, teacherEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            teacherName = itemView.findViewById(R.id.teacherName);
            teacherEmail = itemView.findViewById(R.id.teacherEmail);

        }
    }

}