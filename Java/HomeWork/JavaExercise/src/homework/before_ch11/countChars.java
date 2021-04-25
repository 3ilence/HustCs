package homework.before_ch11;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class countChars {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        input = input.trim();
        input = input.toLowerCase();
        Map<Character, Integer> map = new TreeMap<>();
        for(int i = 0;i < input.length(); i++) {
            if((input.charAt(i) >= 'a' && input.charAt(i) <= 'z') ||  (input.charAt(i) >= 'A' && input.charAt(i) <= 'Z')) {
                if(map.containsKey(input.charAt(i))) {
                    map.replace(input.charAt(i), map.get(input.charAt(i)) + 1);
                } else {
                    map.put(input.charAt(i),1);
                }
            }
        }
        System.out.println("统计结果如下： \n");
        for(Map.Entry<Character, Integer> entry : map.entrySet()) {
            System.out.println("" + entry.getKey() + "出现次数为: " + entry.getValue());
        }
    }
}
