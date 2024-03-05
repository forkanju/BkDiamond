package com.bechakeena.bkdiamond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bechakeena.bkdiamond.databinding.OrderedProductItemBinding;
import com.bechakeena.bkdiamond.globals.Constants;
import com.bechakeena.bkdiamond.models.OrderProduct;
import com.bechakeena.bkdiamond.utils.Utils;

import java.util.List;

public class OrderedProductAdapter extends RecyclerView.Adapter<OrderedProductAdapter.ViewHolder> {

    private Context mContext;
    private List<OrderProduct> orderedProducts;

    public class ViewHolder extends RecyclerView.ViewHolder {
        OrderedProductItemBinding binding;

        public ViewHolder(OrderedProductItemBinding mainBinding) {
            super(mainBinding.getRoot());
            binding = mainBinding;
        }
    }


    public OrderedProductAdapter(Context context, List<OrderProduct> orderedProducts) {
        this.mContext = context;
        this.orderedProducts = orderedProducts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderedProductAdapter.ViewHolder(OrderedProductItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderProduct product = orderedProducts.get(position);
        holder.binding.txtName.setText(product.getProductName());
        holder.binding.txtQuantity.setText("TK " + product.getUnitPrice() + " X " + product.getQuantity());
        holder.binding.txtAmount.setText(Constants.PRICE_UNIT + Utils.getDoubleFormat(product.getUnitPrice() * product.getQuantity()));

    }

    @Override
    public int getItemCount() {
        return orderedProducts != null ? orderedProducts.size() : 0;
    }
}
