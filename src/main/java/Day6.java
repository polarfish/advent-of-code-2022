import java.util.HashSet;
import java.util.Set;

public class Day6 extends Day {

    Day6() {
        super(6);
    }

    public static void main(String[] args) {
        new Day6().solve();
    }


    @Override
    public String part1(String input) {
        return partSolution(input, 4);
    }

    public String part2(String input) {
        return partSolution(input, 14);
    }

    private String partSolution(String input, int limit) {
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
}
