package homework.before_ch11;

import java.util.Scanner;

public class exercise {
    public static void main(String[] args) {
        System.out.println("Please enter a number between 0 and 1000: ");
        Scanner in = new Scanner(System.in);
        //int i1 = in.nextInt();
        //Two_Six(i1);
        char ch = 'a';
        double d = 0.1;
        long l = 12L;
        System.out.println("ch=" + ch);
        System.out.println("d=" + d);
        System.out.println("l=" + l + "L");
        in.close();
    }



    public static void Two_Six(int x) {
        int sum = 0;
        while( x > 0) {
            sum += x % 10;
            x = x / 10;
        }
        System.out.println("The sum of the digits is " + sum);
    }
}
