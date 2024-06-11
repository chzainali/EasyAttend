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
import com.example.easyattend.models.OnItemClick;
import com.example.easyattend.models.UserModel;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.ViewHolder> {
    Context context;
    List<UserModel> list;
    OnItemClick onItemClick;

    public AdapterUsers(Context context, List<UserModel> list, OnItemClick click) {
        this.context = context;
        this.list = list;
        this.onItemClick = click;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subjects, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int p) {
        UserModel data = list.get(p);

        holder.subjectName.setText(data.getUserName());
        holder.teacherName.setVisibility(View.GONE);


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
        TextView subjectName, teacherName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            subjectName = itemView.findViewById(R.id.subjectName);
            teacherName = itemView.findViewById(R.id.teacherName);

        }
    }

}