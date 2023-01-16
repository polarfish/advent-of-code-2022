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

    @Override
    public String part1(String input) {
        Basin basin = parseBasin(input);
        int result = findWay(basin, new int[]{1, 0}, new int[]{basin.width() - 2, basin.height() - 1});
        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        Basin basin = parseBasin(input);
        int result = findWay(basin, new int[]{1, 0},
            new int[]{basin.width() - 2, basin.height() - 1},
            new int[]{1, 0},
            new int[]{basin.width() - 2, basin.height() - 1});
        return String.valueOf(result);
    }

    private int findWay(Basin basin, int[] src, int[]... dst) {
        State nextState = new State(0, src[X], src[Y], 0);
        Set<State> visited = new HashSet<>();
        Queue<State> queue = new ArrayDeque<>();
        queue.add(nextState);
        visited.add(nextState);

        int i = 0;
        int[] currentDst = dst[i];
        int result = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            State s = queue.poll();

            if (s.x == currentDst[X] && s.y == currentDst[Y]) {

                if (++i < dst.length) {
                    queue.clear();
                    visited.clear();
                    currentDst = dst[i];
                } else {
                    result = s.minute;
                    break;
                }
            }

            int nextMinute = s.minute + 1;
            int[][] map = basin.getMap(nextMinute);

            // going down
            if (s.y < basin.height() - 1 && map[s.y + 1][s.x] == 0) {
                nextState = new State(nextMinute, s.x, s.y + 1, nextMinute % basin.lcm());
                if (!visited.contains(nextState)) {
                    queue.add(nextState);
                    visited.add(nextState);
                }
            }

            // going right
            if (map[s.y][s.x + 1] == 0) {
                nextState = new State(nextMinute, s.x + 1, s.y, nextMinute % basin.lcm());
                if (!visited.contains(nextState)) {
                    queue.add(nextState);
                    visited.add(nextState);
                }
            }

            // waiting
            if (map[s.y][s.x] == 0) {
                nextState = new State(nextMinute, s.x, s.y, nextMinute % basin.lcm());
                if (!visited.contains(nextState)) {
                    queue.add(nextState);
                    visited.add(nextState);
                }
            }

            // going left
            if (map[s.y][s.x - 1] == 0) {
                nextState = new State(nextMinute, s.x - 1, s.y, nextMinute % basin.lcm());
                if (!visited.contains(nextState)) {
                    queue.add(nextState);
                    visited.add(nextState);
                }
            }

            // going up
            if (s.y > 0 && map[s.y - 1][s.x] == 0) {
                nextState = new State(nextMinute, s.x, s.y - 1, nextMinute % basin.lcm());
                if (!visited.contains(nextState)) {
                    queue.add(nextState);
                    visited.add(nextState);
                }
            }

        }

        return result;
    }

    record State(int minute, int x, int y, int z) {

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            State state = (State) o;

            if (x != state.x) {
                return false;
            }
            if (y != state.y) {
                return false;
            }
            return z == state.z;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + z;
            return result;
        }
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

        public int lcm() {
            return lcm;
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
