package com.example.popstudios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    String[] nameData, descriptionData, importanceData, difficultyData;
    Context context;

    public MyAdapter(Context ct, String[] s1, String[] s2, String[] s3, String[] s4){
        context = ct;
        nameData = s1;
        importanceData = s2;
        difficultyData = s3;
        descriptionData = s4;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_goal_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.listName.setText(nameData[position]);
        holder.listImportance.setText(importanceData[position]);
        holder.listDifficulty.setText(difficultyData[position]);
        holder.listDescription.setText(descriptionData[position]);
    }

    @Override
    public int getItemCount() {
        return nameData.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView listName,listImportance,listDifficulty,listDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            listName = itemView.findViewById(R.id.listName);
            listImportance = itemView.findViewById(R.id.listImportance);
            listDifficulty = itemView.findViewById(R.id.listDifficulty);
            listDescription = itemView.findViewById(R.id.listDescription);
        }
    }
}
