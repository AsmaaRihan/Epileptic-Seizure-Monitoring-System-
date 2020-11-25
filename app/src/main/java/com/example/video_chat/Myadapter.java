package com.example.video_chat;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Myadapter extends RecyclerView.Adapter<Myadapter.MyViewHolder> {
    Context context;
    String CurrentUser;
    FirebaseAuth auth;
    ArrayList<Messages> messages;

    public Myadapter(Context context, ArrayList<Messages> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.message_single_layout,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        auth= FirebaseAuth.getInstance();
        CurrentUser=auth.getCurrentUser().getUid();
        Messages m = messages.get(position);
        String User_from= m.getFrom();
        String Msg_Type= m.getType();

        if(Msg_Type.equals("text")){
            holder.text.setVisibility(View.VISIBLE);
            holder.textTime.setVisibility(View.VISIBLE);
            holder.ImageTime.setVisibility(View.INVISIBLE);
            holder.image.setVisibility(View.INVISIBLE);
        if (User_from.equals(CurrentUser)){
            holder.text.setBackgroundResource(R.drawable.text_design_2);
            holder.text.setTextColor(Color.BLACK);
        }else {
            holder.text.setBackgroundResource(R.drawable.text_design);
            holder.text.setTextColor(Color.WHITE);
        }

        holder.text.setText(m.getMessage());
        holder.textTime.setText(m.getTime());

        }else if(Msg_Type.equals("image")){
            holder.text.setVisibility(View.INVISIBLE);
            holder.textTime.setVisibility(View.INVISIBLE);
            holder.ImageTime.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.VISIBLE);
            Picasso.get().load(m.getMessage()).into(holder.image);
            holder.ImageTime.setText(m.getTime());
        }
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView text,textTime,ImageTime;
        ImageView image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text =(TextView) itemView.findViewById(R.id.ShowMessagesText);
            textTime =(TextView) itemView.findViewById(R.id.MessageTime);
            ImageTime =(TextView) itemView.findViewById(R.id.ImageTime);
            image=(ImageView)itemView.findViewById(R.id.ShowMessageImage);
        }
    }
}
