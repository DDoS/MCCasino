package me.DDoS.MCCasino.slotmachine;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author DDos
 */
public class Reel {

    private static final Random random = new SecureRandom();
    //
    private final List<ReelValue> values;

    public Reel(List<ReelValue> values) {

        this.values = values;

    }

    public ItemStack getRandomItem() {

        int totalWeights = 0;

        for (ReelValue value : values) {

            totalWeights += value.getProb();

        }

        int i = 0;
        final int rand = random.nextInt(totalWeights);

        for (ReelValue value : values) {

            i += value.getProb();

            if (rand < i) {

                return value.getItem();

            }
        }

        return null;

    }

    public static void main(String[] args) {

        try {

            System.out.println("Testing probabilities.");

            if (args.length < 1) {

                System.out.println("You need to provide IDs and their associated weights");
                return;

            }

            final List<ReelValue> values = new ArrayList<ReelValue>();

            for (String value : args[0].split("-")) {

                String[] pair = value.split(":");

                if (pair.length < 2) {

                    System.out.println("Invalid ID and weight pair: missing ID or weight.");
                    return;

                }

                try {

                    values.add(new ReelValue(new ItemStack(Integer.parseInt(pair[0]), 1), Integer.parseInt(pair[1])));

                } catch (NumberFormatException nfe) {

                    System.out.println("Invalid ID and weight pair: ID or weight is not a number.");
                    System.out.println("Cause: " + value);
                    return;

                }
            }

            final Reel reel = new Reel(values);
            int repeats;

            try {

                repeats = args.length > 1 ? Integer.parseInt(args[1]) : 10000;

            } catch (NumberFormatException nfe) {

                System.out.println("Provided number of repetitions is invalid: not a number.");
                return;

            }

            System.out.println("Running test...");

            final Map<Integer, Integer> results = new HashMap<Integer, Integer>();

            for (int i = 0; i < repeats; i++) {

                int result = reel.getRandomItem().getTypeId();

                if (results.containsKey(result)) {

                    results.put(result, results.get(result) + 1);

                } else {

                    results.put(result, 1);

                }
            }

            System.out.println("Results for " + repeats + " attempts:");

            for (Entry<Integer, Integer> entry : results.entrySet()) {

                System.out.println("ID: " + entry.getKey() + ", hits: " + entry.getValue());

            }

            System.out.println("Done.");

        } catch (NoClassDefFoundError cnfe) {
            
            System.out.println("Error: please add the Bukkit.jar library to a lib folder, in the same directory as this jar.");
            
        }
    }
}
