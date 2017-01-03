package co.rapiddelivery.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import co.rapiddelivery.models.PickUpModel;
import co.rapiddelivery.src.R;
import co.rapiddelivery.views.CustomTextView;

/**
 * Created by Kunal on 20/12/16.
 */

public class PickUpAdapter extends RecyclerView.Adapter<PickUpAdapter.MyViewHolder> {
    private Context mContext;
    private List<PickUpModel> pickUpModelList;
    private final PickUpAdapter.OnItemClickListener listener;

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

        public void setClickListener(final PickUpModel pickUpModel, final PickUpAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(pickUpModel);
                }
            });
        }
    }

    public PickUpAdapter(Context mContext, List<PickUpModel> pickUpModelList, PickUpAdapter.OnItemClickListener listener) {
        this.mContext = mContext;
        this.pickUpModelList = pickUpModelList;
        this.listener = listener;
    }

    @Override
    public PickUpAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delivery_list, parent, false);

        return new PickUpAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PickUpAdapter.MyViewHolder holder, int position) {
        PickUpModel pickUpModel = pickUpModelList.get(position);
        holder.txtCustName.setText(pickUpModel.getName());
        holder.txtCustAddress.setText(pickUpModel.getAddress() + " " + pickUpModel.getPincode());
        holder.txtAmount.setText(pickUpModel.getCutOffTime() + " hrs");
        if (position % 2 == 0) {
            holder.relWholeContent.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
        }
        if (position % 2 == 1) {
            holder.relWholeContent.setBackgroundColor(mContext.getResources().getColor(R.color.red));
        }
        if (position == 0) {
            holder.relWholeContent.setBackgroundColor(mContext.getResources().getColor(R.color.green));
        }
        holder.setClickListener(pickUpModel, listener);
    }

    @Override
    public int getItemCount() {
        return pickUpModelList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(PickUpModel item);
    }
}
