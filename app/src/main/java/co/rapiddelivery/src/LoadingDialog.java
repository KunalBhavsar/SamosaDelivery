package co.rapiddelivery.src;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * This class is used to show loading dialog.
 */
public class LoadingDialog {

    /**
     * Instance of loading dialog.
     */
    private static LoadingDialog loadingDialog;

    /**
     * Instance of progress dialog.
     */
    private ProgressDialog progressDialog;

    /**
     * This method is used to get instance of loading dialog.
     *
     * @return instance of loading dialog.
     */
    public static LoadingDialog getInstance() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog();
        }
        return loadingDialog;
    }

    /**
     * This method is used to show loading dialog.
     * @param context {@link Context}
     */
    public void showLoadingDialog(Context context, String msg) {
        progressDialog = ProgressDialog.show(context, "Processing...", msg, true);
    }

    /**
     * This method is used to dismiss loading dialog.
     */
    public void dismissLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
