package com.example.BlockchainPHR.Blockchain;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BlockchainPHR.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView txtIndex, txtPreviousHash, txtTimestamp, txtData, txtHash;

    public RecyclerViewHolder(@NonNull final View itemView) {
        super(itemView);

        txtIndex = itemView.findViewById(R.id.txt_index);
        txtPreviousHash = itemView.findViewById(R.id.txt_previous_hash);
        txtTimestamp = itemView.findViewById(R.id.txt_timestamp);
        txtData = itemView.findViewById(R.id.txt_data);
        txtHash = itemView.findViewById(R.id.txt_hash);
    }
}