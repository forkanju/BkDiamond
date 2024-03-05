package com.bechakeena.bkdiamond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.databinding.CartListItemBinding;
import com.bechakeena.bkdiamond.databinding.CategoryChildItemBinding;
import com.bechakeena.bkdiamond.dbhelper.CartItem;
import com.bechakeena.bkdiamond.globals.Constants;
import com.bechakeena.bkdiamond.models.Product;
import com.bechakeena.bkdiamond.utils.GlideApp;
import java.util.Collections;
import java.util.List;


public class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.MyViewHolder> {

    private Context context;
    private List<CartItem> cartItems = Collections.emptyList();
    private CartProductsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CartListItemBinding binding;

        public MyViewHolder(CartListItemBinding mainBinding) {
            super(mainBinding.getRoot());
            binding = mainBinding;
        }
    }


    public CartProductsAdapter(Context context, CartProductsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setData(List<CartItem> cartItems) {
        if (cartItems == null) {
            this.cartItems = Collections.emptyList();
        }

        this.cartItems = cartItems;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartProductsAdapter.MyViewHolder(CartListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.product;
        holder.binding.name.setText(product.getProductName());
        holder.binding.price.setText(holder.binding.name.getContext().getString(R.string.lbl_item_price_quantity, context.getString(R.string.price_with_currency, product.getSellPrice()), cartItem.quantity));
        GlideApp.with(context).load(Constants.PRODUCT_THUMBNAIL_BASE_URL +product.getProductPhoto()).into(holder.binding.thumbnail);

        if (listener != null)
            holder.binding.btnRemove.setOnClickListener(view -> listener.onCartItemRemoved(position, cartItem));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public interface CartProductsAdapterListener {
        void onCartItemRemoved(int index, CartItem cartItem);
    }
}
