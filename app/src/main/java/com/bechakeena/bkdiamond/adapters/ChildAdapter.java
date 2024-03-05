package com.bechakeena.bkdiamond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.databinding.CategoryChildItemBinding;
import com.bechakeena.bkdiamond.databinding.ProductListItemBinding;
import com.bechakeena.bkdiamond.models.Child;

import java.util.List;


public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {

    private Context mContext;
    private List<Child> childes;
    private CategoryItemClickListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {


        private CategoryChildItemBinding binding;



        public ViewHolder(CategoryChildItemBinding binding2) {
            super(binding2.getRoot());
            binding = binding2;


            binding.catLayout.setOnClickListener(view -> {
                if (listener != null) listener.onItemClick(childes.get(getAdapterPosition()).getId(), childes.get(getAdapterPosition()).getName());
            });
        }
    }

    public ChildAdapter(Context mContext, List<Child> childes, CategoryItemClickListener listener) {
        this.mContext = mContext;
        this.childes = childes;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChildAdapter.ViewHolder(CategoryChildItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Child child = childes.get(position);
        holder.binding.txtTitle.setText(child.getName());

    }

    @Override
    public int getItemCount() {

        return childes != null ? childes.size() : 0;
    }

    public interface CategoryItemClickListener{
        public void onItemClick(int id, String name);
    }
}
