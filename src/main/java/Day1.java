import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class Day1 extends Day {

    Day1() {
        super(1, "Elf carrying the most calories", "Top 3 Elves carrying the most calories");
    }

    public static void main(String[] args) {
        Day1 day = new Day1();

        assertEquals(24000, day.part1(readFile("Day1_sample.txt")));

        assertEquals(45000, day.part2(readFile("Day1_sample.txt")));

        day.run();
    }


    @Override
    public String part1(String input) {
        int buf = 0;
        int max = 0;
        for (String l : input.split("\n")) {
            if (l.length() == 0) {
                max = Math.max(max, buf);
                buf = 0;
            } else {
                buf += Integer.parseInt(l);
            }
        }
        return String.valueOf(Math.max(max, buf));
    }

    public String part2(String input) {
        int buf = 0;
        SortedSet<Integer> set = new TreeSet<>(Comparator.comparingInt(i -> (int) i).reversed());
        for (String l : input.split("\n")) {
            if (l.length() == 0) {
                set.add(buf);
                buf = 0;
            } else {
                buf += Integer.parseInt(l);
            }
        }
        set.add(buf);
        return String.valueOf(set.stream().limit(3).mapToInt(value -> value).sum());
    }
}
