package com.bechakeena.bkdiamond.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.callbacks.QuantityChangeListener;
import com.bechakeena.bkdiamond.databinding.RecyclerProductItemBinding;
import com.bechakeena.bkdiamond.models.Product;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private Context mContext;
    private List<Product> productList;
    private List<Product> productListFiltered;
    private QuantityChangeListener mListener;

    public ProductAdapter(Context context, List<Product> productList, QuantityChangeListener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.productList = productList;
        this.productListFiltered = productList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductAdapter.ViewHolder(RecyclerProductItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        final Product product = productListFiltered.get(position);
        final ViewHolder vh = (ViewHolder) holder;
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        //Glide.with(mContext).load(product.getImage()).apply(options).into(vh.imageView);

        vh.binding.txtProductName.setText(product.getProductName() != null ? product.getProductName() : "N/A");
        vh.binding.txtPrice.setText(product.getSellPrice() != null ? "TK " + product.getSellPrice() + " " + product.getUnitName() : "N/A");
        vh.binding.txtUnit.setText(product.getUnitName());
        vh.binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 1;
                String quantity = vh.binding.edtCount.getText().toString();
                if (quantity.equalsIgnoreCase("0") || TextUtils.isEmpty(quantity)) count = 1;
                else count = Integer.parseInt(quantity);
                mListener.onQuantityChange(String.valueOf(product.getProductId()), product.getProductName(), "", product.getUnitName(), count, product.getSellPrice(), vh.binding.cardView);
                int e[] = new int[2];
                vh.binding.cardView.getLocationInWindow(e);
            }
        });


    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {

        return productListFiltered != null ? productListFiltered.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    productListFiltered = productList;
                } else {
                    List<Product> filteredList = new ArrayList<>();
                    for (Product row : productList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
//                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getStock().getSellPrice().contains(charSequence)) {
//                            filteredList.add(row);
//                        }
                    }

                    productListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productListFiltered = (ArrayList<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerProductItemBinding binding;

        public ViewHolder(RecyclerProductItemBinding mainBinding) {
            super(mainBinding.getRoot());
            binding = mainBinding;
        }

    }
}
