package com.bechakeena.bkdiamond.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.bechakeena.bkdiamond.adapters.NotificationAdapter;
import com.bechakeena.bkdiamond.databinding.ActivityNotificationBinding;
import com.bechakeena.bkdiamond.dbhelper.AppDatabase;
import com.bechakeena.bkdiamond.models.Notification;
import io.realm.RealmResults;

public class NotificationActivity extends AppCompatActivity {
    
    private ActivityNotificationBinding binding = null;



    private NotificationAdapter mAdapter;
    private RealmResults<Notification> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Notification");

        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        notifications = AppDatabase.getNotifications();
        if (notifications != null || notifications.size() > 0){
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.layoutEmpty.setVisibility(View.GONE);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            binding.recyclerView.setLayoutManager(mLayoutManager);
            binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new NotificationAdapter(this, notifications);
            binding.recyclerView.setAdapter(mAdapter);
        }else {
            binding.recyclerView.setVisibility(View.GONE);
            binding.layoutEmpty.setVisibility(View.VISIBLE);
        }

    }
}
