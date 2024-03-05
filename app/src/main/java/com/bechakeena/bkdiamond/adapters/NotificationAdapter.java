package com.bechakeena.bkdiamond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bechakeena.bkdiamond.databinding.RecyclerNotificationItemBinding;
import com.bechakeena.bkdiamond.dbhelper.AppDatabase;
import com.bechakeena.bkdiamond.models.Notification;
import com.bechakeena.bkdiamond.utils.TimeAgo;

import io.realm.RealmResults;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private Context mContext;
    private RealmResults<Notification> notifications;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private RecyclerNotificationItemBinding binding;

        public MyViewHolder(RecyclerNotificationItemBinding mainBinding) {
            super(mainBinding.getRoot());
            binding = mainBinding;
        }
    }


    public NotificationAdapter(Context mContext, RealmResults<Notification> notifications) {
        this.mContext = mContext;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationAdapter.MyViewHolder(RecyclerNotificationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        String timeAgo = TimeAgo.getTimeAgo(notification.getTimestamp());
        holder.binding.txtTime.setText(timeAgo);
        holder.binding.txtMessage.setText(notification.getMessage());
        long current = System.currentTimeMillis();
        long input = notification.getTimestamp();
        long milliseconds = current - input;
        int seconds = (int) milliseconds / 1000;
        int hours = seconds / 3600;
        if (hours > 72) {
            AppDatabase.removeNotification(notification);
        }

    }

    @Override
    public int getItemCount() {
        return (notifications != null & notifications.size() > 0) ? notifications.size() : 0;
    }

}
