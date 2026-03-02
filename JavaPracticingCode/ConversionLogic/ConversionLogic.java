import java.util.Scanner;

class ConversionLogic{

    private static String toBinary(int n){
        if(n == 0) return "0";
        int val = Math.abs(n);
        String res = "";
        while(val > 0){
            int rem = val % 2;
            res = rem + res;
            val = val / 2;
        }
        return (n < 0 ? " - " : "")+res;
    }
    private static String toOct(int n){
        if(n == 0) return "0";
        int val = Math.abs(n);
        String res = "";
        while(val > 0){
            int rem = val % 8;
            res = rem + res;
            val = val / 8;
        }
        return (n < 0 ? " - " : "")+res;
    }
    private static String toHexa(int n){
        if(n == 0) return "0";
        int val = Math.abs(n);
        char[] ch = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        String res = "";
        while(val > 0){
            int rem = val % 16;
            res = ch[rem] + res;
            val = val / 16;
        }
        return (n < 0 ? " - " : "")+res;
    }
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter you value of N :");
        int N = sc.nextInt();
        System.out.println("binary"+toBinary(N));
        System.out.println("octal"+toOct(N));
        System.out.println("hexa"+toHexa(N));
    }
}