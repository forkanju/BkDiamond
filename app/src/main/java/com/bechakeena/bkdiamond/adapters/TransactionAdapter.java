package com.bechakeena.bkdiamond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.databinding.TransactionProductItemBinding;
import com.bechakeena.bkdiamond.models.Order;
import com.bechakeena.bkdiamond.utils.Utils;

import java.util.List;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context mContext;
    private List<Order> orderedList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TransactionProductItemBinding binding;

        public ViewHolder(TransactionProductItemBinding binding2) {
            super(binding2.getRoot());
            binding = binding2;

            binding.recyclerList.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            binding.recyclerList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
            binding.recyclerList.setLayoutManager(mLayoutManager);
        }
    }


    public TransactionAdapter(Context context, List<Order> orderedList) {
        this.mContext = context;
        this.orderedList = orderedList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TransactionAdapter.ViewHolder(TransactionProductItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order ordered = orderedList.get(position);

        holder.binding.txtOrder.setText(ordered.getOrderNo());
        holder.binding.txtDate.setText(Utils.getOrderTimestamp(ordered.getOrderDate()));
        holder.binding.txtAmount.setText(holder.binding.txtAmount.getContext().getString(R.string.lbl_amount_with_currency, mContext.getString(R.string.lbl_total), ordered.getTotal()));
        holder.binding.txtDue.setText(holder.binding.txtDue.getContext().getString(R.string.lbl_amount_with_currency, mContext.getString(R.string.lbl_total_due), ordered.getAmountDue()));
        holder.binding.txtPaid.setText(holder.binding.txtPaid.getContext().getString(R.string.lbl_amount_with_currency, mContext.getString(R.string.lbl_total_paid), ordered.getAmountPaid()));
        OrderedProductAdapter mAdapter = new OrderedProductAdapter(mContext, ordered.getOrderProducts());
        holder.binding.recyclerList.setAdapter(mAdapter);

        String orderStatus = ordered.getOrderStatusName().toLowerCase();
        switch (orderStatus) {
            case "pending":
                holder.binding.txtStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_pending, 0, 0, 0);
                holder.binding.txtStatus.setText(ordered.getOrderStatusName());
                holder.binding.txtStatus.setTextColor(ContextCompat.getColor(mContext, R.color.pending));
                break;
            case "approved":
                holder.binding.txtStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_approved, 0, 0, 0);
                holder.binding.txtStatus.setText(ordered.getOrderStatusName());
                holder.binding.txtStatus.setTextColor(ContextCompat.getColor(mContext, R.color.approved));
                break;
            case "received":
                holder.binding.txtStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_received, 0, 0, 0);
                holder.binding.txtStatus.setText(ordered.getOrderStatusName());
                holder.binding.txtStatus.setTextColor(ContextCompat.getColor(mContext, R.color.received));
                break;
            case "rejected":
                holder.binding.txtStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_rejected, 0, 0, 0);
                holder.binding.txtStatus.setText(ordered.getOrderStatusName());
                holder.binding.txtStatus.setTextColor(ContextCompat.getColor(mContext, R.color.rejected));
                break;
            default:
                holder.binding.txtStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_pending, 0, 0, 0);
                holder.binding.txtStatus.setText("Unknown");
                holder.binding.txtStatus.setTextColor(ContextCompat.getColor(mContext, R.color.pending));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return orderedList != null ? orderedList.size() : 0;
    }
}
