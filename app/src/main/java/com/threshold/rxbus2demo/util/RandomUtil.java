package com.threshold.rxbus2demo.util;

import java.util.Random;

/**
 * Created by threshold on 2017/1/19.
 */

public class RandomUtil {

    private static Random sRandom;

    public static int random(int range) {
        if (sRandom == null) {
            sRandom = new Random();
        }
        return sRandom.nextInt(range);
    }

}
