

package com.example.app_maulisa;
import android.content.Context;
import androidx.recyclerview.widget.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class GithubAdapter extends RecyclerView.Adapter<GithubAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<Githubuser> userlist ;
    private List<Githubuser> userListFiltered;
    private UserAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, nameurl;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.username);
            nameurl = view.findViewById(R.id.userlink);
            thumbnail = view.findViewById(R.id.userphoto);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onUserSelected(userListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }



    public GithubAdapter(Context context, List<Githubuser> userList, UserAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.userlist = userList;
        this.userListFiltered = userList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row_item, parent, false);
        return new MyViewHolder(itemView);


    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Githubuser githubuser = userListFiltered.get(position);
        holder.name.setText(githubuser.getName());
        holder.nameurl.setText(githubuser.getUrlname());
        Glide.with(context).load(githubuser.getImage()).apply(RequestOptions.circleCropTransform()).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return userListFiltered.size();
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    userListFiltered = userlist;
                } else {
                    List<Githubuser> filteredList = new ArrayList<>();
                    for (Githubuser row : userlist) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    userListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = userListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userListFiltered = (ArrayList<Githubuser>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface UserAdapterListener {
        void onUserSelected(Githubuser githubuser);
    }
}