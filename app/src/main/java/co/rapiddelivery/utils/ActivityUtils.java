package co.rapiddelivery.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import co.rapiddelivery.intf.OnDialogClickListener;
import co.rapiddelivery.src.R;

public class ActivityUtils {

    private static int REQ_CODE = 200;

    public static Dialog showAlertDialog(final Activity context, String text){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_message);
        dialog.setCancelable(true);
        Button button = (Button) dialog.findViewById(R.id.btn_ok);
        TextView textView = (TextView)dialog.findViewById(R.id.txt_msg);
        textView.setText(text);
        dialog.getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivityForResult(intent, REQ_CODE);
            }
        });
        dialog.show();
        return dialog;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getWindow().getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // Check network connection
    public static boolean isNetworkConnected(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static void showFilterDialog(Context context, List<String> filterList, final OnDialogClickListener intf) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filter_selection_dialog);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recycle_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        SimpleDialogAdapter simpleDialogAdapter = new SimpleDialogAdapter(new OnDialogClickListener() {
            @Override
            public void onClick(String filterType) {
                dialog.dismiss();
                intf.onClick(filterType);
            }
        });
        recyclerView.setAdapter(simpleDialogAdapter);
        simpleDialogAdapter.setData(filterList);
        dialog.show();
    }

}
