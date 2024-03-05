package com.bechakeena.bkdiamond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.databinding.RecyclerSearchProductBinding;
import com.bechakeena.bkdiamond.dbhelper.CartItem;
import com.bechakeena.bkdiamond.globals.Constants;
import com.bechakeena.bkdiamond.models.Product;
import com.bechakeena.bkdiamond.utils.GlideApp;

import java.util.List;

import io.realm.RealmResults;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {


    private Context mContext;
    private List<Product> products;
    private ProductsAdapterListener listener;
    private RealmResults<CartItem> cartItems;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private RecyclerSearchProductBinding binding;

        public MyViewHolder(RecyclerSearchProductBinding binding2) {
            super(binding2.getRoot());
            binding = binding2;
        }
    }


    public SearchAdapter(Context context, List<Product> products, ProductsAdapterListener listener) {
        this.mContext = context;
        this.products = products;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchAdapter.MyViewHolder(RecyclerSearchProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product product = products.get(position);
        holder.binding.txtName.setText(product.getProductName());
        holder.binding.txtUnit.setText(product.getUnitName());
        holder.binding.txtPrice.setText(mContext.getString(R.string.price_with_currency, product.getSellPrice()));
        GlideApp.with(mContext).load(Constants.PRODUCT_THUMBNAIL_BASE_URL + product.getProductPhoto()).into(holder.binding.thumbnail);

        holder.binding.icAdd.setOnClickListener(view -> {
            listener.onProductAddedCart(position, product);
        });

        holder.binding.icRemove.setOnClickListener(view -> {
            listener.onProductRemovedFromCart(position, product);
        });

        if (cartItems != null) {
            CartItem cartItem = cartItems.where().equalTo("product.productId", product.getProductId()).findFirst();
            if (cartItem != null) {

                holder.binding.productCount.setText(String.valueOf(cartItem.quantity));
                holder.binding.icRemove.setVisibility(View.VISIBLE);
                holder.binding.productCount.setVisibility(View.VISIBLE);
            } else {
                holder.binding.productCount.setText(String.valueOf(0));
                holder.binding.icRemove.setVisibility(View.GONE);
                holder.binding.productCount.setVisibility(View.GONE);
            }
        }

    }

    public void setCartItems(RealmResults<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public void updateItem(int position, RealmResults<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyItemChanged(position);
    }

    public interface ProductsAdapterListener {
        void onProductAddedCart(int index, Product product);

        void onProductRemovedFromCart(int index, Product product);
    }
}
