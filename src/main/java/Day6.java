import java.util.HashSet;
import java.util.Set;

public class Day6 extends Day {

    public static void main(String[] args) {
        Day6 day = new Day6(); // https://adventofcode.com/2022/day/6

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String sample2 = readFile("%s_sample2.txt".formatted(day.name()));
        String sample3 = readFile("%s_sample3.txt".formatted(day.name()));
        String sample4 = readFile("%s_sample4.txt".formatted(day.name()));
        String sample5 = readFile("%s_sample5.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(7, day.part1(sample));
        assertEquals(5, day.part1(sample2));
        assertEquals(6, day.part1(sample3));
        assertEquals(10, day.part1(sample4));
        assertEquals(11, day.part1(sample5));
        assertEquals(1833, day.part1(full));

        assertEquals(19, day.part2(sample));
        assertEquals(23, day.part2(sample2));
        assertEquals(23, day.part2(sample3));
        assertEquals(29, day.part2(sample4));
        assertEquals(26, day.part2(sample5));
        assertEquals(3425, day.part2(full));

        day.run(full, input -> day.partSolution1(input, 4), "Part 1 result (substring and create set)");
        day.run(full, input -> day.partSolution2(input, 4), "Part 1 result (registry array and update set)");
        day.run(full, input -> day.partSolution3(input, 4), "Part 1 result (registry array and update counter)");
        day.run(full, input -> day.partSolution1(input, 14), "Part 2 result (substring and create set)");
        day.run(full, input -> day.partSolution2(input, 14), "Part 2 result (registry array and update set)");
        day.run(full, input -> day.partSolution3(input, 14), "Part 2 result (registry array and update counter)");
    }


    @Override
    public String part1(String input) {
        return partSolution3(input, 4);
    }

    @Override
    public String part2(String input) {
        return partSolution3(input, 14);
    }

    // iterate > substring > create-uniqueness-set > compare-length
    private String partSolution1(String input, int limit) {
        for (int i = 0; i < input.length(); i++) {
            if (i >= limit) {
                Set<Character> set = new HashSet<>();
                for (char c : input.substring(i - limit, i).toCharArray()) {
                    set.add(c);
                }
                if (set.size() == limit) {
                    return String.valueOf(i);
                }
            }

        }
        return "";
    }

    // iterate > update-registry-array-and-uniqueness-set > compare-length
    private String partSolution2(String input, int limit) {
        int[] reg = new int[26];
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < input.length(); i++) {
            int headPos = input.charAt(i) - 97;
            reg[headPos] += 1;

            if (reg[headPos] == 1) {
                set.add(headPos);
            } else {
                set.remove(headPos);
            }

            if (i >= limit - 1) {
                if (set.size() == limit) {
                    return String.valueOf(i + 1);
                }

                int tailPos = input.charAt(i - limit + 1) - 97;
                reg[tailPos] -= 1;

                if (reg[tailPos] == 1) {
                    set.add(tailPos);
                } else {
                    set.remove(tailPos);
                }
            }

        }
        return "";
    }

    // iterate > update-registry-array-and-uniqueness-counter > compare-counter
    private String partSolution3(String input, int limit) {
        int[] reg = new int[26];
        int uniqueCount = 0;
        for (int i = 0; i < input.length(); i++) {
            int headPos = input.charAt(i) - 97;
            reg[headPos] += 1;

            if (reg[headPos] == 1) {
                uniqueCount++;
            } else if (reg[headPos] == 2) {
                uniqueCount--;
            }

            if (i >= limit - 1) {
                if (uniqueCount == limit) {
                    return String.valueOf(i + 1);
                }

                int tailPos = input.charAt(i - limit + 1) - 97;
                reg[tailPos] -= 1;

                if (reg[tailPos] == 1) {
                    uniqueCount++;
                } else if (reg[tailPos] == 0) {
                    uniqueCount--;
                }
            }

        }
        return "";
    }
}
