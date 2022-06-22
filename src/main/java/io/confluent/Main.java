package io.confluent;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length > 0) {
            Producer.main(Arrays.copyOfRange(args, 0, 1));
        } else {
            System.out.println("Producer properties file is not provided.");
        }

        if (args.length > 1) {
            Consumer.main(Arrays.copyOfRange(args, 1, 2));
        } else {
            System.out.println("Consumer properties file is not provided.");
        }
    }
}
