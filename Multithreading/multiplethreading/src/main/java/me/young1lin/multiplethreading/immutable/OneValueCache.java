package me.young1lin.multiplethreading.immutable;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/11/18 9:20 下午
 */
public class OneValueCache {

    private final BigInteger lastNumber;

    private final BigInteger[] lastFactors;

    public OneValueCache(BigInteger i,BigInteger[] factors){
        this.lastNumber = i;
        this.lastFactors = Arrays.copyOf(factors,factors.length);
    }

    public BigInteger[] getLastFactors(BigInteger i){
        if(lastNumber == null || !lastNumber.equals(i)){
            return null;
        }
        return Arrays.copyOf(lastFactors,lastFactors.length);
    }

}
