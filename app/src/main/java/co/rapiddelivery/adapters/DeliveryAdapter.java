package co.rapiddelivery.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.rapiddelivery.models.DeliveryModel;
import co.rapiddelivery.src.R;
import co.rapiddelivery.views.CustomTextView;

/**
 * Created by Kunal on 19/12/16.
 */

public class DeliveryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<DeliveryModel> deliveryList;
    private final OnItemClickListener listener;

    public void setDeliveryList(List<DeliveryModel> deliveryList) {
        if (deliveryList == null) {
            deliveryList = new ArrayList<>();
        }
        this.deliveryList = deliveryList;
        notifyDataSetChanged();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextView txtCustName, txtDeliveryNumber, txtCustAddress, txtAmount, txtMode;
        LinearLayout relWholeContent;

        MyViewHolder(View view) {
            super(view);
            txtCustName = (CustomTextView) view.findViewById(R.id.txt_customer_name);
            txtDeliveryNumber = (CustomTextView) view.findViewById(R.id.txt_delivery_number);
            txtCustAddress = (CustomTextView) view.findViewById(R.id.txt_customer_address);
            txtAmount = (CustomTextView) view.findViewById(R.id.txt_amount);
            relWholeContent = (LinearLayout) view.findViewById(R.id.rel_whole_content);
            txtMode = (CustomTextView) view.findViewById(R.id.txt_mode);
        }

        void setClickListener(final DeliveryModel deliveryModel, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(deliveryModel);
                }
            });
        }
    }

    public DeliveryAdapter(Context mContext, List<DeliveryModel> deliveryList, OnItemClickListener listener) {
        this.mContext = mContext;
        this.deliveryList = deliveryList;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate your layout and pass it to view holder
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delivery_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        DeliveryModel deliveryModel = deliveryList.get(position);
        MyViewHolder holder = (MyViewHolder) viewHolder;
        holder.txtCustName.setText(deliveryModel.getName() + " - " + deliveryModel.getFlow());
        holder.txtDeliveryNumber.setText("(" + deliveryModel.getAwb() + ") \n Status : " + deliveryModel.getStatus());
        holder.txtCustAddress.setText(deliveryModel.getAddress1() + ", " + deliveryModel.getAddress2() + " - " + deliveryModel.getPincode());
        holder.txtAmount.setText(deliveryModel.getValue());
        holder.txtMode.setText(deliveryModel.getMode().toUpperCase());

        if (deliveryModel.getFlow().toLowerCase().contains("deliver")) {
            if (!deliveryModel.getStatus().equalsIgnoreCase("dispatched")) {
                holder.relWholeContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green_status));
            }
            else if (deliveryModel.getMode().equalsIgnoreCase("cod")) {
                holder.relWholeContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
            }
            else if (deliveryModel.getMode().equalsIgnoreCase("prepaid")) {
                holder.relWholeContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue));
            }
            else if (deliveryModel.getMode().equalsIgnoreCase("reverse")) {
                holder.relWholeContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
            }
            else {
                holder.relWholeContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));
            }
        }
        else if (deliveryModel.getFlow().toLowerCase().contains("return")) {
            if (!deliveryModel.getStatus().equalsIgnoreCase("dispatched")) {
                holder.relWholeContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green_status));
            }
            else {
                holder.relWholeContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue));
            }
        }
        else {
            holder.relWholeContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));
        }

        holder.setClickListener(deliveryModel, listener);
    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(DeliveryModel item);
    }
}

