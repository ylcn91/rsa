package Business_Logic;

import java.math.BigInteger;
import java.util.Random;

public class KeyCreator {
    private BigInteger helper;
    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger fn;
    private BigInteger d;
    private BigInteger e;
    private BigInteger[] fermNumbers = {BigInteger.valueOf(3), BigInteger.valueOf(5), BigInteger.valueOf(17), BigInteger.valueOf(257), BigInteger.valueOf(65537)};


    public void generateKeys(int countOfBits){
        p = BigInteger.probablePrime(countOfBits,new Random());
        q = BigInteger.probablePrime(countOfBits,new Random());
        n = q.multiply(p);
        fn = (p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)));
        for (int i = 4; i >= 0; i--) {
            if (fermNumbers[i].compareTo(fn)<0){
                e = fermNumbers[i];
                break;
            }
        }
        BigInteger[] resultOfInversion = EuclidInversion.inversion(e,fn);
        d = resultOfInversion[0];
        helper = resultOfInversion[1];
//        System.out.println("q  = " + q);
//        System.out.println("p  = " + p);
//        System.out.println("n  = " + n);
//        System.out.println("fn = " + fn);
//        System.out.println("e  = " + e);
//        System.out.println("d  = " + d);
    }

    public BigInteger[] getOpenKey(){
        if (e == null){
            generateKeys(512);
            System.out.println("The keys were generated with the default bit value: 512");
        }
        return new BigInteger[]{e, n};
    }

    public BigInteger[] getSecretKey(){
        if (d == null){
            generateKeys(512);
            System.out.println("The keys were generated with the default bit value: 512");
        }
        return new BigInteger[]{d, n};
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getHelper() {
        return helper;
    }
}
