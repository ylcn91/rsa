package Business_Logic;

import java.math.BigInteger;

public class MyMath {
    public static BigInteger modPow(BigInteger base, BigInteger exponent, BigInteger m){
        BigInteger result = BigInteger.ONE;
        char[] expBytes = exponent.toString(2).toCharArray();

        for (char expByte : expBytes) {
            int number;
            if (expByte == '1') number = 1;
            else number = 0;
            result = result.pow(2).multiply(base.pow(number));
            result = modPowHelper(result,BigInteger.ONE,m);
        }
        return result;
    }

    private static BigInteger modPowHelper(BigInteger base, BigInteger exponent, BigInteger m){
        BigInteger result = BigInteger.ONE;
        for (int i = 1; i < exponent.intValue()+1; i++) {
            result = (result.multiply(base)).mod(m);
        }
        return result;
    }
}
