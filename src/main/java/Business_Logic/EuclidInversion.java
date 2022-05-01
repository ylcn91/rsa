package Business_Logic;

import java.math.BigInteger;

import static java.math.BigInteger.*;


//r1 = a + b(-q0)
//r2 = b + r1(-q1) = a(-q0) + b(1 + q0q1)
//r3 = r1 + r2(-q2) = a(1 + q2q1) + b(-q0 - q2 - q0q1q2)
//...

public class EuclidInversion {
    public static BigInteger[] inversion(BigInteger e, BigInteger fn) {
        if (fn.equals(ZERO)) return new BigInteger[]{ONE, ZERO, e};

        BigInteger a = fn;

        BigInteger x1 = ZERO;
        BigInteger x2 = ONE;
        BigInteger y1 = ONE;
        BigInteger y2 = ZERO;
        BigInteger x;
        BigInteger y;

        while (fn.compareTo(ZERO) > 0) {
            BigInteger q = e.divide(fn);
            BigInteger r = e.subtract(q.multiply(fn));
            x = x2.subtract(q.multiply(x1));
            y = y2.subtract(q.multiply(y1));
            e = fn;
            fn = r;
            x2 = x1;
            x1 = x;
            y2 = y1;
            y1 = y;
        }
        x = x2;
        y = y2;

        if (x.compareTo(ZERO) < 0) return new BigInteger[]{x.mod(a),y,e};
        return new BigInteger[]{x,y,e};
    }
}
