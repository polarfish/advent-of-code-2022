import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class Day1 extends Day {

    public static void main(String[] args) {
        Day1 day = new Day1(); // https://adventofcode.com/2022/day/1

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(24000, day.part1(sample));
        assertEquals(69883, day.part1(full));

        assertEquals(45000, day.part2(sample));
        assertEquals(207576, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
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

    @Override
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
