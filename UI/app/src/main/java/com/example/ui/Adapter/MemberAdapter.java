package com.example.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ui.Activity.ProfileActivity;
import com.example.ui.Model.Member;
import com.example.ui.R;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Arrays;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    private List<Member> memberList;
    private Context context;

    public MemberAdapter(Context context, List<Member> memberList) {
        this.context = context;
        this.memberList = memberList;
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

        Glide.with(holder.imageViewAvatar.getContext())
                .load(member.getAvatar())
                .error(R.drawable.ic_user) // ảnh mặc định khi load lỗi
                .into(holder.imageViewAvatar);


        // Bind email
        holder.tvEmail.setText(member.getEmail());

        // Bind role to Spinner
        List<String> roles = Arrays.asList("Admin", "Member", "Viewer");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, roles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerRole.setAdapter(spinnerAdapter);
        holder.spinnerRole.setSelection(roles.indexOf(member.getRole()));

        holder.btnMore.setOnClickListener(v -> {
            android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(context, holder.btnMore);
            popupMenu.getMenu().add("Cập nhật");
            popupMenu.getMenu().add("Xóa");

            popupMenu.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();
                if (title.equals("Cập nhật")) {
                    // TODO: Mở form cập nhật hoặc xử lý cập nhật
                } else if (title.equals("Xóa")) {
                    memberList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, memberList.size());
                }
                return true;
            });

            popupMenu.show();
        });

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAvatar;
        TextView tvEmail;
        Spinner spinnerRole;
        TextView btnMore; // Thay vì ImageButton btnDelete

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAvatar = itemView.findViewById(R.id.imgMemberAvatar);
            tvEmail = itemView.findViewById(R.id.tvMemberEmail);
            spinnerRole = itemView.findViewById(R.id.spinnerMemberRole);
            btnMore = itemView.findViewById(R.id.btnMore); // Dùng TextView
        }
    }

}