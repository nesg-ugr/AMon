package es.ugr.mdsm.connectivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;

public class NFC {

    private final static String TAG = "Connectivity.Nfc";

    public static boolean isEnabled(Context context) {
        NfcManager nfcManager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter nfcAdapter = nfcManager.getDefaultAdapter();
        return (nfcAdapter != null && nfcAdapter.isEnabled());
    }

    public static boolean isPresent(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);
    }
}
