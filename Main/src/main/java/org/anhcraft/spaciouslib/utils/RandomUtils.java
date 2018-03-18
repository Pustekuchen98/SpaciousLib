package org.anhcraft.spaciouslib.utils;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    public static <X> X pickRandom(ArrayList<X> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    public static <X> X pickRandom(X[] list) {
        return list[new Random().nextInt(list.length)];
    }

    public static int randomInt(int min, int max){
        max++;
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static double randomDouble(int min, int max){
        max++;
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static int unique(){
        double a = System.currentTimeMillis() + randomInt(0, 100);
        if(Integer.MAX_VALUE < a){
            a = Integer.MAX_VALUE;
        }
        return (int) a;
    }
}