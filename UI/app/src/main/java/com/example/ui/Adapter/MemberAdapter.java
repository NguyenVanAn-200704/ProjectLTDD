package com.example.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ui.Model.Member;
import com.example.ui.R;

import java.util.Arrays;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    private List<Member> memberList;
    private Context context;
    private int userId;
    private int projectCreatorId; // Thêm projectCreatorId
    private OnMemberActionListener actionListener;

    public interface OnMemberActionListener {
        void onUpdateMember(Member member, String newRole);
        void onDeleteMember(Member member);
        void onTaskUploadFailure(String errorMessage);
    }

    public MemberAdapter(Context context, List<Member> memberList, int userId, int projectCreatorId, OnMemberActionListener actionListener) {
        this.context = context;
        this.memberList = memberList;
        this.userId = userId;
        this.projectCreatorId = projectCreatorId;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = memberList.get(position);

        // Bind avatar
        Glide.with(holder.imageViewAvatar.getContext())
                .load(member.getAvatar())
                .error(R.drawable.ic_user)
                .into(holder.imageViewAvatar);

        // Bind email
        holder.tvEmail.setText(member.getEmail());

        // Bind role to Spinner
        List<String> roles = Arrays.asList("ADMIN", "MEMBER", "VIEWER");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, roles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerRole.setAdapter(spinnerAdapter);
        holder.spinnerRole.setSelection(roles.indexOf(member.getRole()));

        // Kiểm tra quyền chỉnh sửa
        if (member.getUserId() == userId) {
            // Vô hiệu hóa cho chính người dùng hiện tại
            holder.spinnerRole.setEnabled(false);
            holder.btnMore.setVisibility(View.GONE);
        } else if (member.getRole().equals("ADMIN") && userId != projectCreatorId) {
            // Vô hiệu hóa cho Admin khác nếu không phải createBy
            holder.spinnerRole.setEnabled(false);
            holder.btnMore.setVisibility(View.GONE);
        } else {
            // Kích hoạt cho các trường hợp khác
            holder.spinnerRole.setEnabled(true);
            holder.btnMore.setVisibility(View.VISIBLE);
            // Handle PopupMenu
            holder.btnMore.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, holder.btnMore);
                popupMenu.getMenu().add("Cập nhật");
                popupMenu.getMenu().add("Xóa");

                popupMenu.setOnMenuItemClickListener(item -> {
                    String title = item.getTitle().toString();
                    if (title.equals("Cập nhật")) {
                        String newRole = holder.spinnerRole.getSelectedItem().toString();
                        actionListener.onUpdateMember(member, newRole);
                    } else if (title.equals("Xóa")) {
                        actionListener.onDeleteMember(member);
                    }
                    return true;
                });

                popupMenu.show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAvatar;
        TextView tvEmail;
        Spinner spinnerRole;
        TextView btnMore;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAvatar = itemView.findViewById(R.id.imgMemberAvatar);
            tvEmail = itemView.findViewById(R.id.tvMemberEmail);
            spinnerRole = itemView.findViewById(R.id.spinnerMemberRole);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}