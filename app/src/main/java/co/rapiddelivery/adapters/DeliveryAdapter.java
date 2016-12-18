package co.rapiddelivery.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import co.rapiddelivery.models.DeliveryModel;
import co.rapiddelivery.src.R;
import co.rapiddelivery.views.CustomTextView;

/**
 * Created by Kunal on 19/12/16.
 */

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.MyViewHolder> {
    private Context mContext;
    private List<DeliveryModel> deliveryList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CustomTextView txtCustName, txtCustAddress, txtAmount;
        public LinearLayout relWholeContent;

        public MyViewHolder(View view) {
            super(view);
            txtCustName = (CustomTextView) view.findViewById(R.id.txt_customer_name);
            txtCustAddress = (CustomTextView) view.findViewById(R.id.txt_customer_address);
            txtAmount = (CustomTextView) view.findViewById(R.id.txt_amount);
            relWholeContent = (LinearLayout) view.findViewById(R.id.rel_whole_content);
        }
    }

    public DeliveryAdapter(Context mContext, List<DeliveryModel> deliveryList) {
        this.mContext = mContext;
        this.deliveryList = deliveryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delivery_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        DeliveryModel deliveryModel = deliveryList.get(position);
        holder.txtCustName.setText(deliveryModel.getName());
        holder.txtCustAddress.setText(deliveryModel.getAddress() + " " + deliveryModel.getPincode());
        holder.txtAmount.setText(deliveryModel.getCodAmount() + " Rs.");
        if (position % 2 == 0) {
            holder.relWholeContent.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
        }
        if (position % 2 == 1) {
            holder.relWholeContent.setBackgroundColor(mContext.getResources().getColor(R.color.red));
        }
        if (position == 0) {
            holder.relWholeContent.setBackgroundColor(mContext.getResources().getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }
}
