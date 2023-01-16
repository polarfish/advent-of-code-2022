import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Day24 extends Day {

    public static void main(String[] args) {
        Day24 day = new Day24();  // https://adventofcode.com/2022/day/24

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(18, day.part1(sample));
        assertEquals(225, day.part1(full));

        assertEquals(54, day.part2(sample));
        assertEquals(711, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    private static final int X = 0;
    private static final int Y = 1;
    private static final int MIN = 2;

    @Override
    public String part1(String input) {
        Basin basin = parseBasin(input);
        Point src = new Point(1, 0);
        Point dest = new Point(basin.width() - 2, basin.height() - 1);
        int result = findWay(basin, src, dest);
        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        Basin basin = parseBasin(input);
        Point src = new Point(1, 0);
        Point dest = new Point(basin.width() - 2, basin.height() - 1);
        int result = findWay(basin, src, dest, src, dest);
        return String.valueOf(result);
    }

    private int findWay(Basin basin, Point src, Point... dst) {
        State initialState = new State(0, src.x(), src.y());
        Set<State> visited = new HashSet<>();
        Queue<State> queue = new ArrayDeque<>();
        queue.add(initialState);
        visited.add(initialState);

        int i = 0;
        Point currentDst = dst[i];
        int result = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            State nextState = null;
            State state = queue.poll();

            if (state.x == currentDst.x() && state.y == currentDst.y()) {

                if (++i < dst.length) {
                    queue.clear();
                    visited.clear();
                    currentDst = dst[i];
                } else {
                    result = state.minute;
                    break;
                }
            }

            int nextMinute = state.minute + 1;
            int[][] map = basin.getMap(nextMinute);

            // going down
            if (state.y < basin.height() - 1 && map[state.y + 1][state.x] == 0) {
                nextState = new State(nextMinute, state.x, state.y + 1);
                if (!visited.contains(nextState)) {
                    queue.add(nextState);
                    visited.add(nextState);
                }
            }

            // going right
            if (map[state.y][state.x + 1] == 0) {
                nextState = new State(nextMinute, state.x + 1, state.y);
                if (!visited.contains(nextState)) {
                    queue.add(nextState);
                    visited.add(nextState);
                }
            }

            // waiting
            if (map[state.y][state.x] == 0) {
                nextState = new State(nextMinute, state.x, state.y);
                if (!visited.contains(nextState)) {
                    queue.add(nextState);
                    visited.add(nextState);
                }
            }

            // going left
            if (map[state.y][state.x - 1] == 0) {
                nextState = new State(nextMinute, state.x - 1, state.y);
                if (!visited.contains(nextState)) {
                    queue.add(nextState);
                    visited.add(nextState);
                }
            }

            // going up
            if (state.y > 0 && map[state.y - 1][state.x] == 0) {
                nextState = new State(nextMinute, state.x, state.y - 1);
                if (!visited.contains(nextState)) {
                    queue.add(nextState);
                    visited.add(nextState);
                }
            }

        }

        return result;
    }

    record State(int minute, int x, int y) {

    }

    record Point(int x, int y) {

    }

    Basin parseBasin(String input) {
        String[] lines = input.split("\n");
        int width = lines[0].length();
        int height = lines.length;
        List<Blizzard> blizzards = new ArrayList<>();
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                char ch = lines[y].charAt(x);
                if (ch == '.') {
                    continue;
                }
                blizzards.add(
                    new Blizzard(
                        width, height, x, y,
                        switch (ch) {
                            case '^' -> 0;
                            case '>' -> 1;
                            case 'v' -> 2;
                            case '<' -> 3;
                            default -> throw new IllegalStateException("Unexpected value: " + ch);
                        }
                    ));
            }
        }
        return new Basin(blizzards);
    }

    static class Blizzard {

        final private int width;
        final private int height;
        int x;
        int y;
        private final int dir;

        public Blizzard(int width, int height, int x, int y, int dir) {
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
            this.dir = dir;
        }

        void move() {
            switch (dir) {
                case 0 -> y = y == 1 ? height - 2 : y - 1;
                case 1 -> x = x == width - 2 ? 1 : x + 1;
                case 2 -> y = y == height - 2 ? 1 : y + 1;
                case 3 -> x = x == 1 ? width - 2 : x - 1;
            }
        }
    }

    static class Basin {

        private final int[][] template;

        private final int lcm;

        public Basin(List<Blizzard> blizzards) {
            this.blizzards = blizzards;

            template = new int[height()][width()];

            int gcd = BigInteger.valueOf(height() - 2).gcd(BigInteger.valueOf(width() - 2)).intValue();
            lcm = (height() - 2) * (width() - 2) / gcd;

            for (int y = 0; y < template.length; y++) {
                template[y][0] = 1;
                template[y][template[y].length - 1] = 1;
            }
            for (int x = 2; x < template[0].length - 1; x++) {
                template[0][x] = 1;
                template[template.length - 1][x - 1] = 1;
            }

            calculateNextState();
        }

        public int[][] getMap(int minute) {
            int stateId = minute % lcm;
            while (stateId >= states.size()) {
                calculateNextState();
            }
            return states.get(stateId);
        }

        public int height() {
            return blizzards.get(0).height;
        }

        public int width() {
            return blizzards.get(0).width;
        }

        private void calculateNextState() {
            int[][] state = new int[template.length][];
            for (int i = 0; i < template.length; i++) {
                state[i] = Arrays.copyOf(template[i], template[i].length);
            }

            for (Blizzard b : blizzards) {
                state[b.y][b.x] = 1;
            }

            states.add(state);

            blizzards.forEach(Blizzard::move);
        }

        private final List<int[][]> states = new ArrayList<>();

        private final List<Blizzard> blizzards;
    }

}
