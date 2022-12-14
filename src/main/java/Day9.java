import java.util.HashSet;
import java.util.Set;

public class Day9 extends Day {

    public static void main(String[] args) {
        Day9 day = new Day9(); // https://adventofcode.com/2022/day/9

        String sample = readFile("Day9_sample.txt");
        String sample2 = readFile("Day9_sample2.txt");
        String full = readFile("Day9.txt");

        assertEquals(13, day.part1(sample));
        assertEquals(6522, day.part1(full));

        assertEquals(1, day.part2(sample));
        assertEquals(36, day.part2(sample2));
        assertEquals(2717, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        return partSolution(input, 2);
    }

    public String part2(String input) {
        return partSolution(input, 10);
    }

    public String partSolution(String input, int ropeSize) {
        int[][] rope = new int[ropeSize][2];
        int[] head = rope[0];
        int[] tail = rope[ropeSize - 1];

        Set<Point> visited = new HashSet<>();
        visited.add(Point.of(tail));
        for (String line : input.split("\n")) {
            char direction = line.charAt(0);
            int steps = Integer.parseInt(line.substring(2));

            for (int i = 0; i < steps; i++) {
                moveHead(head, direction);
                for (int j = 1; j < rope.length; j++) {
                    adjustKnots(rope[j - 1], rope[j]);
                }
                visited.add(Point.of(tail));
            }
        }

        return String.valueOf(visited.size());
    }

    private void moveHead(int[] head, char direction) {
        switch (direction) {
            case 'U' -> head[1]++;
            case 'R' -> head[0]++;
            case 'D' -> head[1]--;
            case 'L' -> head[0]--;
            default -> {

            }
        }
    }

    private void adjustKnots(int[] head, int[] tail) {
        int dx = head[0] - tail[0];
        int dxAbs = Math.abs(dx);
        int dy = head[1] - tail[1];
        int dyAbs = Math.abs(dy);

        if (dxAbs > 1 || dyAbs > 1) {
            tail[0] += Integer.signum(dx);
            tail[1] += Integer.signum(dy);
        }
    }

    record Point(int x, int y) {

        static Point of(int[] coords) {
            return new Point(coords[0], coords[1]);
        }
    }
}
