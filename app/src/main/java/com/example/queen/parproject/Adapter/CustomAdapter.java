package com.example.queen.parproject.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.queen.parproject.R;
import com.example.queen.parproject.model.User;

import java.util.List;

/**
 * Created by queen on 9/30/17.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyCustomViewHolder> {
    List<User> userList;

    public CustomAdapter(List<User> userList){
        this.userList = userList;
    }

    @Override
    public MyCustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_display_users, parent, false);
        return new MyCustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyCustomViewHolder holder, int position) {
        User userPosition = userList.get(position);
        holder.name.setText(userPosition.getPassword());

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class MyCustomViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public MyCustomViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_name);
        }
    }
}



