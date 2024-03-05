package com.bechakeena.bkdiamond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bechakeena.bkdiamond.callbacks.ItemClickListener;
import com.bechakeena.bkdiamond.databinding.CategoryParentItemBinding;
import com.bechakeena.bkdiamond.models.Parent;
import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder> {

    private Context mContext;
    private List<Parent> parents;
    private ItemClickListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CategoryParentItemBinding binding;

        public ViewHolder(CategoryParentItemBinding mainBinding) {
            super(mainBinding.getRoot());
            binding = mainBinding;
            binding.catLayout.setOnClickListener(view -> {
                if (listener != null) listener.onClick(parents.get(getAdapterPosition()).getId());
            });
        }
    }

    public ParentAdapter(Context mContext, List<Parent> parents, ItemClickListener listener) {
        this.mContext = mContext;
        this.parents = parents;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParentAdapter.ViewHolder(CategoryParentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Parent parent = parents.get(position);
        holder.binding.txtTitle.setText(parent.getName());
    }

    @Override
    public int getItemCount() {

        return parents != null ? parents.size() : 0;
    }
}
