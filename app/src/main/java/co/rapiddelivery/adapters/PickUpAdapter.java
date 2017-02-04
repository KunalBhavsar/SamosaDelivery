package co.rapiddelivery.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import co.rapiddelivery.models.PickUpModel;
import co.rapiddelivery.src.R;
import co.rapiddelivery.views.CustomTextView;

/**
 * Created by Kunal on 20/12/16.
 */

public class PickUpAdapter extends RecyclerView.Adapter<PickUpAdapter.MyViewHolder> {

    private static final String TAG = PickUpAdapter.class.getSimpleName();

    private Context mContext;
    private List<PickUpModel> pickUpModelList;
    private final PickUpAdapter.OnItemClickListener listener;

    public void setPickUpModelList(List<PickUpModel> pickUpModelList) {
        if (pickUpModelList == null) {
            pickUpModelList = new ArrayList<>();
        }
        this.pickUpModelList = pickUpModelList;
        Log.i(TAG, "PICKUP LIST SIZE : " + this.pickUpModelList.size());
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CustomTextView txtCustName, txtPickupNumber, txtCustAddress, txtExpectedCount, txtMode;
        public LinearLayout relWholeContent;

        public MyViewHolder(View view) {
            super(view);
            txtCustName = (CustomTextView) view.findViewById(R.id.txt_customer_name);
            txtCustAddress = (CustomTextView) view.findViewById(R.id.txt_customer_address);
            txtExpectedCount = (CustomTextView) view.findViewById(R.id.txt_expected_count);
            txtPickupNumber = (CustomTextView) view.findViewById(R.id.txt_pickup_number);
            txtMode = (CustomTextView) view.findViewById(R.id.txt_mode);
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
                .inflate(R.layout.item_pickup_list, parent, false);

        return new PickUpAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PickUpAdapter.MyViewHolder holder, int position) {
        PickUpModel pickUpModel = pickUpModelList.get(position);
        holder.txtCustName.setText(pickUpModel.getName() + " ( " + pickUpModel.getPhoneNumber() +" )");
        holder.txtPickupNumber.setText(pickUpModel.getPickupNumber());
        holder.txtCustAddress.setText(pickUpModel.getAddress() + " " + pickUpModel.getPincode());
        holder.txtExpectedCount.setText("Count : " + pickUpModel.getExpectedCount());
        holder.txtMode.setText(pickUpModel.getMode() + "");
        if (!pickUpModel.getStatus().equalsIgnoreCase("manifested")) {
            holder.relWholeContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_blue));
        }
        else if (!pickUpModel.getStatus().equalsIgnoreCase("dispatched")) {
            holder.relWholeContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue));
        }
        else if (!pickUpModel.getStatus().equalsIgnoreCase("picked")) {
            holder.relWholeContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
        }
        else if (!pickUpModel.getStatus().equalsIgnoreCase("no pickup")) {
            holder.relWholeContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
        }
        else {
            holder.relWholeContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));
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
