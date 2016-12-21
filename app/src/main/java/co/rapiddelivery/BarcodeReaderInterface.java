package co.rapiddelivery;

import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by Kunal on 21/12/16.
 */

public interface BarcodeReaderInterface {
    void onBarcodeRead(Barcode barcode);
}
