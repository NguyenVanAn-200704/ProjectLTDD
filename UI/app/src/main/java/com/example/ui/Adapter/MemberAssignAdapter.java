package com.example.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ui.Model.Member;
import com.example.ui.R;

import java.util.List;

public class MemberAssignAdapter extends RecyclerView.Adapter<MemberAssignAdapter.MemberViewHolder> {

    private Context context;
    private List<Member> memberList;
    private OnMemberAssignListener listener;

    public interface OnMemberAssignListener {
        void onMemberAssigned(String email);
    }

    public MemberAssignAdapter(Context context, List<Member> memberList, OnMemberAssignListener listener) {
        this.context = context;
        this.memberList = memberList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member_assign, parent, false);
        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = memberList.get(position);
        holder.tvEmail.setText(member.getEmail());
        holder.tvRole.setText(member.getRole());

        if (member.getAvatar() != null && !member.getAvatar().isEmpty()) {
            Glide.with(context)
                    .load(member.getAvatar())
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.ic_user);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMemberAssigned(member.getEmail());
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEmail, tvRole;
        public ImageView imgAvatar;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}