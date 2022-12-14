import java.util.Arrays;

public class Day4 extends Day {

    public static void main(String[] args) {
        Day4 day = new Day4(); // https://adventofcode.com/2022/day/4

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(2, day.part1(sample));
        assertEquals(556, day.part1(full));

        assertEquals(4, day.part2(sample));
        assertEquals(876, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        return String.valueOf(
            Arrays.stream(input.split("\n"))
                .map(l -> l.split("[,\\-]"))
                .map(a -> new int[]{
                    Integer.parseInt(a[0]),
                    Integer.parseInt(a[1]),
                    Integer.parseInt(a[2]),
                    Integer.parseInt(a[3])})
                .filter(a -> isFullyContained(a[0], a[1], a[2], a[3]))
                .count());
    }

    @Override
    public String part2(String input) {
        return String.valueOf(
            Arrays.stream(input.split("\n"))
                .map(l -> l.split("[,\\-]"))
                .map(a -> new int[]{
                    Integer.parseInt(a[0]),
                    Integer.parseInt(a[1]),
                    Integer.parseInt(a[2]),
                    Integer.parseInt(a[3])})
                .filter(a -> isOverlap(a[0], a[1], a[2], a[3]))
                .count());
    }


    private boolean isFullyContained(int l1, int r1, int l2, int r2) {
        return l1 <= l2 && r1 >= r2 || l2 <= l1 && r2 >= r1;
    }

    private boolean isOverlap(int l1, int r1, int l2, int r2) {
        return l1 >= l2 && l1 <= r2 || r1 >= l2 && r1 <= r2 || isFullyContained(l1, r1, l2, r2);
    }
}
