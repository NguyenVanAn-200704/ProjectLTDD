package com.example.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ui.Request.Member;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<Member> memberList;

    public MemberAdapter(List<Member> memberList) {
        this.memberList = memberList;
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail;
        ImageView imvAvatar;


        public MemberViewHolder(View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvMemberEmail);
            imvAvatar = itemView.findViewById(R.id.imgMemberAvatar);
        }
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = memberList.get(position);

        holder.tvEmail.setText(member.getEmail());
        Glide.with(holder.itemView.getContext())
                .load(member.getAvatar()) // member.getAvatar() trả về URL
                .into(holder.imvAvatar);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}
