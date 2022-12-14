import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day3 extends Day {

    public static void main(String[] args) {
        Day3 day = new Day3(); // https://adventofcode.com/2022/day/3

        String sample = readFile("Day3_sample.txt");
        String full = readFile("Day3.txt");

        assertEquals(157, day.part1(sample));
        assertEquals(8018, day.part1(full));

        assertEquals(70, day.part2(sample));
        assertEquals(2518, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    @Override
    public String part1(String input) {
        return String.valueOf(
            Arrays.stream(input.split("\n"))
                .map(this::findMisplacedItem)
                .mapToInt(this::calculatePriority)
                .sum());
    }

    @Override
    public String part2(String input) {
        return String.valueOf(
            partition(List.of(input.split("\n")), 3)
                .stream()
                .map(this::findBadgeItem)
                .mapToInt(this::calculatePriority)
                .sum());
    }

    private Character findMisplacedItem(String itemsList) {
        int len = itemsList.length();
        var left = new HashSet<Character>();
        var right = new HashSet<Character>();
        for (int i = 0; i < len / 2; i++) {
            left.add(itemsList.charAt(i));
        }
        for (int i = len / 2; i < len; i++) {
            right.add(itemsList.charAt(i));
        }

        left.retainAll(right);

        return left.iterator().next();
    }

    private Character findBadgeItem(List<String> itemsLists) {
        return itemsLists.stream()
            .map(String::toCharArray)
            .map(itemsArray -> {
                Set<Character> itemsSet = new HashSet<>();
                for (Character item : itemsArray) {
                    itemsSet.add(item);
                }
                return itemsSet;
            })
            .reduce((s1, s2) -> {
                s1.retainAll(s2);
                return s1;
            }).orElseThrow().iterator().next();
    }

    int calculatePriority(Character item) {
        if (Character.isUpperCase(item)) {
            return item - 38;
        } else {
            return item - 96;
        }
    }
}
