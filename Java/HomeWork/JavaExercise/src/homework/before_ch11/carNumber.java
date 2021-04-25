package homework.before_ch11;

import java.util.HashSet;
import java.util.Set;

public class carNumber {
    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        String tmp;
        for(int i = 0; i < 5;i++) {
            tmp = randomCar();
            while(set.contains(tmp)) {
                tmp = randomCar();
            }
            System.out.println(tmp);
        }
    }

    //返回0-9之中的一个数字
    public static int randomNum() {
        return (int) Math.floor(Math.random() * 10.0);
    }
    //返回随机一个大写字母
    public static char randomChar() {
        return (char) ('A'+ (int) Math.floor(Math.random() * 26.0));
    }
    public static String randomCar() {
        String carNum = "" + randomChar() +randomChar()
                + randomChar() + randomNum()
                + randomNum() + randomNum()
                + randomNum();
        return carNum;
    }
}
