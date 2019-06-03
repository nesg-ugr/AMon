package es.ugr.mdsm.restDump;

import android.util.Base64;

import java.math.BigInteger;
import java.util.BitSet;

public class Util {

    public static BigInteger bitSetToInteger(BitSet bitSet){
        byte[] bytes = bitSet.toByteArray();
        reverseByteArray(bytes);
        if (bytes.length > 0) {
            return new BigInteger(1, bytes);
        }else{
            return new BigInteger("0");
        }
    }

    public static String bitSetToBase64(BitSet bitSet){
        byte[] bytes = bitSet.toByteArray();
        reverseByteArray(bytes);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static void reverseByteArray(byte[] bytes){
        for(int i=0; i< bytes.length/2; i++){
            byte temp = bytes[i];
            bytes[i] = bytes[bytes.length -i -1];
            bytes[bytes.length -i -1] = temp;
        }
    }
}
