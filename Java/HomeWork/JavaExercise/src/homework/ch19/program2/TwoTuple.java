package homework.ch19.program2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TwoTuple<T1 extends Comparable, T2 extends Comparable> implements Comparable{

    private T1 first;
    private T2 second;

    public TwoTuple() {

    }

    public TwoTuple(T1 t1, T2 t2) {
        this.first = t1;
        this.second = t2;
    }

    public T1 getFirst() {
        return first;
    }

    public void setFirst(T1 first) {
        this.first = first;
    }

    public T2 getSecond() {
        return second;
    }

    public void setSecond(T2 second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TwoTuple) {
            if (((TwoTuple) o).getFirst().equals(getFirst())) {
                if (((TwoTuple) o).getSecond().equals(getSecond())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "( " + first + " , "+ second + ')';
    }

    @Override
    public int compareTo(Object o) {
        if (!((TwoTuple) o).getFirst().equals(getFirst())) {
            return getFirst().compareTo(((TwoTuple) o).getFirst());
        } else {
            return getSecond().compareTo(((TwoTuple) o).getSecond());
        }
    }
    public static void main(String[] args){

        TwoTuple<Integer,String> twoTuple1 =new TwoTuple<>(1, "ccc");
        TwoTuple<Integer,String> twoTuple2 =new TwoTuple<>(1, "bbb");
        TwoTuple<Integer,String> twoTuple3 =new TwoTuple<>(1, "aaa");
        TwoTuple<Integer,String> twoTuple4 =new TwoTuple<>(2, "ccc");
        TwoTuple<Integer,String> twoTuple5 =new TwoTuple<>(2, "bbb");
        TwoTuple<Integer,String> twoTuple6 =new TwoTuple<>(2, "aaa");
        List<TwoTuple<Integer,String>> list = new ArrayList<>();
        list.add(twoTuple1);
        list.add(twoTuple2);
        list.add(twoTuple3);
        list.add(twoTuple4);
        list.add(twoTuple5);
        list.add(twoTuple6);

        //??????equals???contains???????????????equals?????????????????????
        TwoTuple<Integer,String> twoTuple10 =new TwoTuple<>(1, "ccc"); //??????=twoTuple1
        System.out.println(twoTuple1.equals(twoTuple10)); //?????????true
        if(!list.contains(twoTuple10)){
            list.add(twoTuple10);  //???????????????????????????
        }

        //sort????????????????????????compareTo????????????????????????????????????compareTo????????????????????????
        Collections.sort(list);


        for (TwoTuple<Integer, String> t: list) {
            System.out.println(t);
        }

        TwoTuple<TwoTuple<Integer,String >,TwoTuple<Integer,String >> tt1 =
                new TwoTuple<>(new TwoTuple<>(1,"aaa"),new TwoTuple<>(1,"bbb"));
        TwoTuple<TwoTuple<Integer,String >,TwoTuple<Integer,String >> tt2 =
                new TwoTuple<>(new TwoTuple<>(1,"aaa"),new TwoTuple<>(2,"bbb"));
        System.out.println(tt1.compareTo(tt2)); //??????-1
        System.out.println(tt1);

    }

}
