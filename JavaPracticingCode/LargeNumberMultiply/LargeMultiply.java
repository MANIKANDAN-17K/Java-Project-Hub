import java.math.BigInteger;

class LargeMultiply{
    public static void main(String arg[]){
        BigInteger a = new BigInteger("99999999999999999998");
        BigInteger b  = new BigInteger("99999999999999999998");
        BigInteger c = a.multiply(b);
        System.out.println(" result "+c);
    }
}