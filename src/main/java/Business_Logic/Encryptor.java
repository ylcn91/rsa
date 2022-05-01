package Business_Logic;

import java.math.BigInteger;

public class Encryptor {
    public static BigInteger encrypt(BigInteger inputMessage, BigInteger[] key){
        return MyMath.modPow(inputMessage,key[0],key[1]);
    }
}
