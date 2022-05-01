package Business_Logic;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;

public class Decryptor {
    public static BigInteger decrypt(BigInteger inputMessage, BigInteger[] key, BigInteger q, BigInteger p){
        BigInteger mp = MyMath.modPow(inputMessage.mod(p),key[0].mod(p.subtract(ONE)),p);
        BigInteger mq = MyMath.modPow(inputMessage.mod(q),key[0].mod(q.subtract(ONE)),q);
        return (mp.multiply(q.multiply(EuclidInversion.inversion(q,p)[0])).add(mq.multiply(p.multiply(EuclidInversion.inversion(p,q)[0])))).mod(q.multiply(p));
    }
}
