package me.tahacheji.mafananetwork.util;

import java.util.Random;

public class RandomUtil {

    public int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

}
