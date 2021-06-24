package hust.cs.javacourse.search.run;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        list1.add(2);
        list1.add(5);
        list1.add(200);
        list1.add(400);
        list1.add(400);
        list1.add(2);
        list1.add(5);
        list1.add(200);
        System.out.println(list1.equals(list2));
    }
}