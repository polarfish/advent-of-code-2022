import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Day {

    String name() {
        return getClass().getSimpleName();
    }

    long run() {
        return run(readFile("%s.txt".formatted(name())));
    }

    long run(String input) {
        System.out.printf("Running %s%n", getClass().getSimpleName());
        long time1 = run(input, this::part1, "Part 1 result");
        long time2 = run(input, this::part2, "Part 2 result");
        return time1 + time2;
    }

    long run(String input, Function<String, String> function, String label) {
        long start = System.currentTimeMillis();
        String res = function.apply(input);
        long time = System.currentTimeMillis() - start;
        System.out.printf("[%d ms] %s: %s%n", time, label, res);
        return time;
    }

    abstract String part1(String input);

    abstract String part2(String input);

    public static String readFile(String fileName) {

        try (InputStream is = Day.class.getResourceAsStream(fileName);
            BufferedInputStream bis = new BufferedInputStream(
                Objects.requireNonNull(is, () -> "File %s not found".formatted(fileName)))) {

            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            for (int result = bis.read(); result != -1; result = bis.read()) {
                buf.write((byte) result);
            }
            return buf.toString(StandardCharsets.UTF_8);
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
