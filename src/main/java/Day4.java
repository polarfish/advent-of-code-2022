import java.util.Arrays;

public class Day4 extends Day {

    Day4() {
        super(4, "Fully contains", "Overlaps");
    }

    public static void main(String[] args) {
        new Day4().solve();
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
