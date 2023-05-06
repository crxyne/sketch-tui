package org.crayne.sketch.text;

import java.util.Random;

public class RandomString {

    public static String randomString(final int length, final int rightbound, final int leftbound) {
        final Random random = new Random();
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) result.append((char) (leftbound + random.nextInt(rightbound)));
        return result.toString();
    }


}
