import java.util.HashSet;
import java.util.Set;

public class Day6 extends Day {

    Day6() {
        super(6, "start-of-packet marker after", "start-of-message marker after");
    }

    public static void main(String[] args) {
        Day6 day = new Day6();

        assertEquals(7, day.part1(readFile("Day6_sample.txt")));
        assertEquals(5, day.part1(readFile("Day6_sample2.txt")));
        assertEquals(6, day.part1(readFile("Day6_sample3.txt")));
        assertEquals(10, day.part1(readFile("Day6_sample4.txt")));
        assertEquals(11, day.part1(readFile("Day6_sample5.txt")));

        assertEquals(19, day.part2(readFile("Day6_sample.txt")));
        assertEquals(23, day.part2(readFile("Day6_sample2.txt")));
        assertEquals(23, day.part2(readFile("Day6_sample3.txt")));
        assertEquals(29, day.part2(readFile("Day6_sample4.txt")));
        assertEquals(26, day.part2(readFile("Day6_sample5.txt")));

        day.run();
    }


    @Override
    public String part1(String input) {
        return partSolution(input, 4);
    }

    public String part2(String input) {
        return partSolution(input, 14);
    }

    private String partSolution(String input, int limit) {
        return partSolution2(input, limit);
    }

    // iterate > substring > create-set > compare-length
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

    // iterate > update-registry-array-and-set > compare-length
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
}
