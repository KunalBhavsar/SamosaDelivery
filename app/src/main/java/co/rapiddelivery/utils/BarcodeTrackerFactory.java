package co.rapiddelivery.utils;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

import co.rapiddelivery.views.GraphicOverlay;

/**
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 */
public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private GraphicOverlay mGraphicOverlay;

    public BarcodeTrackerFactory(GraphicOverlay graphicOverlay) {
        mGraphicOverlay = graphicOverlay;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay);
        return new GraphicTracker<>(mGraphicOverlay, graphic);
    }
}