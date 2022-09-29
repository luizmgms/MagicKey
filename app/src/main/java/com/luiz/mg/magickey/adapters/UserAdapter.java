package com.luiz.mg.magickey.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.luiz.mg.magickey.MainActivity;
import com.luiz.mg.magickey.R;
import com.luiz.mg.magickey.TakeOrBackKeyActivity;
import com.luiz.mg.magickey.dao.UserDAO;
import com.luiz.mg.magickey.models.User;
import com.luiz.mg.magickey.utils.Utils;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final ArrayList<User> listUsers;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mat;
        private final TextView name;
        private final TextView dept;
        private final ConstraintLayout constraintLayout;

        public ViewHolder(View view) {
            super(view);
            mat = view.findViewById(R.id.matId);
            name = view.findViewById(R.id.nameId);
            dept = view.findViewById(R.id.deptId);
            constraintLayout =view.findViewById(R.id.layoutItemListUsersId);
        }

    }


    public UserAdapter(ArrayList<User> users) {
        listUsers = users;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_item_list_users, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        User user = listUsers.get(position);

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.mat.setText(user.getMat());
        viewHolder.name.setText(user.getName());
        viewHolder.dept.setText(user.getDept());
        viewHolder.constraintLayout.setOnClickListener(view -> {
            Intent i = new Intent(view.getContext(), TakeOrBackKeyActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(Utils.NAME_USER, user.getName());
            i.putExtra(Utils.MAT_USER, user.getMat());
            i.putExtra(Utils.DEPT_USER, user.getDept());
            view.getContext().getApplicationContext().startActivity(i);
        });

        viewHolder.constraintLayout.setOnLongClickListener(view -> {

            view.setOnCreateContextMenuListener((contextMenu, view1, contextMenuInfo) ->
                    contextMenu.add(Utils.DEL).setOnMenuItemClickListener(menuItem -> {

                        deleteUser(view1.getContext(), position);

                        return true;
                    }));

            return false;
        });

    }

    private void deleteUser(Context context, int pos) {

        UserDAO userDAO = new UserDAO(MainActivity.dbHelper);

        int status = userDAO.deleteUser(listUsers.get(pos).getMat());

        if (status == -1) {
            Toast.makeText(context, R.string.erro_delete_user, Toast.LENGTH_SHORT).show();
        } else {
            listUsers.remove(pos);
            notifyItemRemoved(pos);
            Toast.makeText(context, R.string.user_deleted, Toast.LENGTH_SHORT).show();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listUsers.size();
    }


}

