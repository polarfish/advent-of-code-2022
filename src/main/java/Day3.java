import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day3 extends Day {

    Day3() {
        super(3, "Sum priorities misplaced types", "Sum priorities badge types");
    }

    public static void main(String[] args) {
        new Day3().solve();
    }

    @Override
    public long part1(String input) {
        return Arrays.stream(input.split("\n"))
            .map(this::findMisplacedItem)
            .mapToInt(this::calculatePriority)
            .sum();
    }


    public long part2(String input) {
        return Lists.partition(List.of(input.split("\n")), 3)
            .stream()
            .map(this::findBadgeItem)
            .mapToInt(this::calculatePriority)
            .sum();
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
