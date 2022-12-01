import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class Day {

    private final int n;
    private final String part1Label;
    private final String part2Label;

    Day(int n) {
        this(n, "Part 1 result", "Part 2 result");
    }

    Day(int n, String part1Label, String part2Label) {
        this.n = n;
        this.part1Label = part1Label;
        this.part2Label = part2Label;
    }

    void test() {
        System.out.printf("Running Day %d (sample)%n", n);
        run(readFile("Day%d_sample.txt".formatted(n)));
    }

    void solve() {
        System.out.printf("Running Day %d%n", n);
        run(readFile("Day%d.txt".formatted(n)));
    }

    private void run(String input) {
        long start1 = System.currentTimeMillis();
        long res1 = part1(input);
        long time1 = System.currentTimeMillis() - start1;
        System.out.printf("%s: %d%n", part1Label, res1);
        System.out.printf("Part 1 took %d ms%n", time1);

        long start2 = System.currentTimeMillis();
        long res2 = part2(input);
        long time2 = System.currentTimeMillis() - start2;
        System.out.printf("%s: %d%n", part2Label, res2);
        System.out.printf("Part 2 took %d ms%n", time2);
    }

    abstract long part1(String input);

    abstract long part2(String input);

    private String readFile(String fileName) {
        try {
            return Files.readString(Path.of("src/main/resources/" + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
