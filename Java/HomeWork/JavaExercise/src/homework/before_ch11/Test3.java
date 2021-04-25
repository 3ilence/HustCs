package homework.before_ch11;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Scanner;

public class Test3 {
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        System.out.println("请输入一个正整数: ");
        int L = in.nextInt();
        int[][] arr = createArray(L);
        printArray(arr);
        in.close();
        AbstractList<Integer> list = new ArrayList<>();
    }
    /**
     *  创建一个不规则二维数组
     *  第一行row列
     *  第二行row - 1列
     *  ...
     *  最后一行1列
     *	数组元素值都为默认值
     * @param row 行数
     * @return 创建好的不规则数组
     */
    public static  int[][] createArray(int row){
        if(row <= 0)
            return null;
        int[][] arr = new int[row][];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = new int[row - i];
        }
        return arr;
    }

    /**
     * 逐行打印出二维数组，数组元素之间以空格分开
     * @param a
     */
    public static  void printArray(int[][] a){
        if(a == null) {
            System.out.println("请不要输入负数 !");
            return;
        }
        for(int[] e1 : a) {
            for(int e : e1) {
                System.out.print(e + " ");
            }
            System.out.println();
        }
    }
}
