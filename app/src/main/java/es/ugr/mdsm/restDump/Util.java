package es.ugr.mdsm.restDump;

import java.math.BigInteger;
import java.util.BitSet;

public class Util {

    public static BigInteger bitSetToInteger(BitSet bitSet){
        byte[] bytes = bitSet.toByteArray();
        reverseByteArray(bytes);
        return new BigInteger(bytes);
    }

    public static void reverseByteArray(byte[] bytes){
        for(int i=0; i< bytes.length/2; i++){
            byte temp = bytes[i];
            bytes[i] = bytes[bytes.length -i -1];
            bytes[bytes.length -i -1] = temp;
        }
    }
}
