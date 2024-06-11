package com.example.easyattend.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyattend.R;
import com.example.easyattend.models.NotificationModel;
import com.example.easyattend.models.OnItemClick;
import com.example.easyattend.models.UserModel;

import java.util.List;

public class AdapterNotifications extends RecyclerView.Adapter<AdapterNotifications.ViewHolder> {
    Context context;
    List<NotificationModel> list;
    OnItemClick onItemClick;

    public AdapterNotifications(Context context, List<NotificationModel> list, OnItemClick click) {
        this.context = context;
        this.list = list;
        this.onItemClick = click;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notifications, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int p) {
        NotificationModel data = list.get(p);

        holder.label.setText(data.getType());
        holder.dateTime.setText(data.getDate());
        holder.status.setText(data.getStatus());

        holder.delete.setOnClickListener(new View.OnClickListener() {
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
        TextView label, dateTime, status;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            label = itemView.findViewById(R.id.label);
            dateTime = itemView.findViewById(R.id.dateTime);
            status = itemView.findViewById(R.id.status);
            delete = itemView.findViewById(R.id.delete);

        }
    }

}