package com.example.tatsbytatspos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private OnUserEditListener editListener;
    private OnUserDeleteListener deleteListener;

    public interface OnUserEditListener {
        void onEdit(User user);
    }

    public interface OnUserDeleteListener {
        void onDelete(User user);
    }

    public UserAdapter(List<User> userList, OnUserEditListener editListener, OnUserDeleteListener deleteListener) {
        this.userList = userList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUsername.setText(user.getUsername());
        holder.tvRole.setText(user.getRole());

        holder.btnEdit.setOnClickListener(v -> editListener.onEdit(user));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        TextView tvRole;
        ImageButton btnEdit;
        ImageButton btnDelete;

        UserViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvRole = itemView.findViewById(R.id.tvRole);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}