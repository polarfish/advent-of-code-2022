import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Day {

    private final int dayNumber;
    private final String part1Label;
    private final String part2Label;

    Day(int dayNumber) {
        this(dayNumber, "Part 1 result", "Part 2 result");
    }

    Day(int dayNumber, String part1Label, String part2Label) {
        this.dayNumber = dayNumber;
        this.part1Label = part1Label;
        this.part2Label = part2Label;
    }

    void run() {
        System.out.printf("Running %s%n", getClass().getSimpleName());
        run(readFile("Day%d.txt".formatted(dayNumber)));
    }

    void run(String input) {
        long start1 = System.currentTimeMillis();
        String res1 = part1(input);
        long time1 = System.currentTimeMillis() - start1;
        System.out.printf("%s: %s%n", part1Label, res1);
        System.out.printf("Part 1 took %d ms%n", time1);

        long start2 = System.currentTimeMillis();
        String res2 = part2(input);
        long time2 = System.currentTimeMillis() - start2;
        System.out.printf("%s: %s%n", part2Label, res2);
        System.out.printf("Part 2 took %d ms%n", time2);
    }

    abstract String part1(String input);

    abstract String part2(String input);

    public static String readFile(String fileName) {
        try {
            return Files.readString(Path.of("src/main/resources/" + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        return Stream.iterate(0, i -> i <= list.size(), i -> i + size)
            .map(i -> list.subList(i, Math.min(i + size, list.size())))
            .filter(Predicate.not(List::isEmpty))
            .toList();
    }

    public static void assertEquals(long expected, String actual) {
        assertEquals(String.valueOf(expected), actual);
    }

    public static void assertEquals(String expected, String actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("expected: %s, actual: %s".formatted(expected, actual));
        }
    }
}
