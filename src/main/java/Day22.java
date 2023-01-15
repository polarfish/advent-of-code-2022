import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Day22 extends Day {

    public static void main(String[] args) {
        Day22 day = new Day22();  // https://adventofcode.com/2022/day/22

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(6032, day.part1(sample));
        assertEquals(88226, day.part1(full));

        assertEquals(5031, day.part2(sample));
        assertEquals(57305, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    private static final int X = 0;
    private static final int Y = 1;
    private static final int DIR = 2;

    private static final Map<String, List<Wrapper>> LAYOUTS = Map.of(
        "4-2456ab", List.of(
            new Wrapper(8, 0, 11, 0, 3, pos -> setPos(pos, 11 - pos[X], 4, 1)),
            new Wrapper(11, 4, 11, 7, 0, pos -> setPos(pos, 19 - pos[Y], 8, 1)),
            new Wrapper(8, 11, 11, 11, 1, pos -> setPos(pos, 11 - pos[X], 7, 3)),
            new Wrapper(4, 4, 7, 4, 3, pos -> setPos(pos, 8, pos[X] - 4, 0))
        ),
        "50-12589c", List.of(
            new Wrapper(50, 0, 99, 0, 3, pos -> setPos(pos, 0, pos[X] + 100, 0)),
            new Wrapper(0, 150, 0, 199, 2, pos -> setPos(pos, pos[Y] - 100, 0, 1)),
            new Wrapper(49, 150, 49, 199, 0, pos -> setPos(pos, pos[Y] - 100, 149, 3)),
            new Wrapper(50, 149, 99, 149, 1, pos -> setPos(pos, 49, pos[X] + 100, 2)),
            new Wrapper(0, 100, 49, 100, 3, pos -> setPos(pos, 50, pos[X] + 50, 0)),
            new Wrapper(50, 50, 50, 99, 2, pos -> setPos(pos, pos[Y] - 50, 100, 1)),
            new Wrapper(99, 100, 99, 149, 0, pos -> setPos(pos, 149, 149 - pos[Y], 2)),
            new Wrapper(149, 0, 149, 49, 0, pos -> setPos(pos, 99, 149 - pos[Y], 2)),
            new Wrapper(100, 0, 149, 0, 3, pos -> setPos(pos, pos[X] - 100, 199, 3)),
            new Wrapper(0, 199, 49, 199, 1, pos -> setPos(pos, pos[X] + 100, 0, 1)),
            new Wrapper(100, 49, 149, 49, 1, pos -> setPos(pos, 99, pos[X] - 50, 2)),
            new Wrapper(99, 50, 99, 99, 0, pos -> setPos(pos, pos[Y] + 50, 49, 3)),
            new Wrapper(0, 100, 0, 149, 2, pos -> setPos(pos, 50, 149 - pos[Y], 0)),
            new Wrapper(50, 0, 50, 49, 2, pos -> setPos(pos, 0, 149 - pos[Y], 0))
        )
    );

    private static void setPos(int[] pos, int x, int y, int dir) {
        pos[X] = x;
        pos[Y] = y;
        pos[DIR] = dir;
    }

    @Override
    public String part1(String input) {
        String[] lines = input.split("\n");
        String[] mapLines = Arrays.copyOfRange(lines, 0, lines.length - 2);
        String instructions = lines[lines.length - 1];
        int mapWidth = Arrays.stream(mapLines).mapToInt(String::length).max().orElseThrow();

        char[][] map = new char[mapLines.length][mapWidth];

        int[] pos = null;
        for (int y = 0; y < mapLines.length; y++) {
            for (int x = 0; x < mapWidth; x++) {
                map[y][x] = x >= lines[y].length()
                    ? ' '
                    : lines[y].charAt(x);
                if (pos == null && y == 0 && map[y][x] == '.') {
                    pos = new int[]{x, y, 0};
                }
            }
        }

        int steps = 0;
        for (int i = 0; i < instructions.length(); i++) {
            char nextChar = instructions.charAt(i);
            if (nextChar == 'L') {
                moveFlat(map, pos, steps);
                steps = 0;
                pos[DIR] = ((pos[DIR] - 1) + 4) % 4;
            } else if (nextChar == 'R') {
                moveFlat(map, pos, steps);
                steps = 0;
                pos[DIR] = (pos[DIR] + 1) % 4;
            } else {
                steps = steps * 10 + (nextChar - 48);
            }
        }
        moveFlat(map, pos, steps);

        return String.valueOf(
            (pos[Y] + 1) * 1000 + (pos[X] + 1) * 4 + pos[DIR]
        );
    }

    @Override
    public String part2(String input) {
        String[] lines = input.split("\n");
        String[] mapLines = Arrays.copyOfRange(lines, 0, lines.length - 2);
        String instructions = lines[lines.length - 1];
        int mapSize = Math.max(
            Arrays.stream(mapLines).mapToInt(String::length).max().orElseThrow(),
            mapLines.length
        );

        char[][] map = new char[mapSize][mapSize];

        int[] pos = null;
        for (int y = 0; y < mapSize; y++) {
            if (y >= mapLines.length) {
                Arrays.fill(map[y], ' ');
                continue;
            }

            for (int x = 0; x < mapSize; x++) {
                map[y][x] = x >= mapLines[y].length()
                    ? ' '
                    : mapLines[y].charAt(x);
                if (pos == null && y == 0 && map[y][x] == '.') {
                    pos = new int[]{x, y, 0};
                }
            }
        }

        // determine cube layout
        int cubeSide = mapSize / 4;
        StringBuilder sb = new StringBuilder().append(cubeSide).append('-');
        for (int i = 0; i < 16; i++) {
            int x = (i % 4) * cubeSide;
            int y = (i / 4) * cubeSide;
            if (map[y][x] != ' ') {
                sb.append("0123456789abcdef".charAt(i));
            }
        }

        String layout = sb.toString();

        int steps = 0;
        for (int i = 0; i < instructions.length(); i++) {
            char nextChar = instructions.charAt(i);
            if (nextChar == 'L') {
                moveCubic(layout, map, pos, steps);
                steps = 0;
                pos[DIR] = ((pos[DIR] - 1) + 4) % 4;
            } else if (nextChar == 'R') {
                moveCubic(layout, map, pos, steps);
                steps = 0;
                pos[DIR] = (pos[DIR] + 1) % 4;
            } else {
                steps = steps * 10 + (nextChar - 48);
            }
        }
        moveCubic(layout, map, pos, steps);

        return String.valueOf(
            (pos[Y] + 1) * 1000 + (pos[X] + 1) * 4 + pos[DIR]
        );
    }

    record Wrapper(int x1, int y1, int x2, int y2, int dir,
                   Consumer<int[]> wrapModifier) {

        boolean applies(int[] pos) {
            return pos[DIR] == dir & pos[X] >= x1 && pos[X] <= x2 && pos[Y] >= y1 && pos[Y] <= y2;
        }
    }

    void moveCubic(String layout, char[][] map, int[] pos, int steps) {
        int[] newPos = new int[3];
        for (int i = 0; i < steps; i++) {
            System.arraycopy(pos, 0, newPos, 0, 3);
            int x = pos[X];
            int y = pos[Y];
            switch (pos[DIR]) {
                case 0 -> {
                    if (x == map[0].length - 1 || map[pos[Y]][x + 1] == ' ') {
                        wrapCubic(layout, newPos);
                    } else {
                        newPos[X]++;
                    }
                }
                case 1 -> {
                    if (y == map.length - 1 || map[y + 1][x] == ' ') {
                        wrapCubic(layout, newPos);
                    } else {
                        newPos[Y]++;
                    }

                }
                case 2 -> {
                    if (x == 0 || map[y][x - 1] == ' ') {
                        wrapCubic(layout, newPos);
                    } else {
                        newPos[X]--;
                    }
                }
                case 3 -> {
                    if (y == 0 || map[y - 1][x] == ' ') {
                        wrapCubic(layout, newPos);
                    } else {
                        newPos[Y]--;
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + pos[DIR]);
            }

            if (map[newPos[Y]][newPos[X]] == '#') {
                return;
            }

            System.arraycopy(newPos, 0, pos, 0, 3);
        }
    }

    void wrapCubic(String layout, int[] pos) {
        LAYOUTS.get(layout).stream()
            .filter(wrapper -> wrapper.applies(pos))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Missing wrap function for " + Arrays.toString(pos)))
            .wrapModifier.accept(pos);
    }

    void moveFlat(char[][] map, int[] pos, int steps) {
        int x = pos[X];
        int y = pos[Y];
        for (int i = 0; i < steps; i++) {
            switch (pos[DIR]) {
                case 0 -> {
                    if (x == map[0].length - 1 || map[y][x + 1] == ' ') {
                        x = wrapLtrX(map, y);
                    } else {
                        x++;
                    }
                    if (map[y][x] == '#') {
                        return;
                    } else {
                        pos[X] = x;
                    }
                }
                case 1 -> {
                    if (y == map.length - 1 || map[y + 1][x] == ' ') {
                        y = wrapUtdY(map, x);
                    } else {
                        y++;
                    }
                    if (map[y][x] == '#') {
                        return;
                    } else {
                        pos[Y] = y;
                    }
                }
                case 2 -> {
                    if (x == 0 || map[y][x - 1] == ' ') {
                        x = wrapRtlX(map, y);
                    } else {
                        x--;
                    }
                    if (map[y][x] == '#') {
                        return;
                    } else {
                        pos[X] = x;
                    }
                }
                case 3 -> {
                    if (y == 0 || map[y - 1][x] == ' ') {
                        y = wrapDtuY(map, x);
                    } else {
                        y--;
                    }
                    if (map[y][x] == '#') {
                        return;
                    } else {
                        pos[Y] = y;
                    }
                }
            }
        }
    }

    int wrapLtrX(char[][] map, int y) {
        for (int x = 0; x < map[y].length; x++) {
            if (map[y][x] != ' ') {
                return x;
            }
        }
        return -1;
    }

    int wrapRtlX(char[][] map, int y) {
        for (int x = map[y].length - 1; x >= 0; x--) {
            if (map[y][x] != ' ') {
                return x;
            }
        }
        return -1;
    }

    int wrapUtdY(char[][] map, int x) {
        for (int y = 0; y < map.length; y++) {
            if (map[y][x] != ' ') {
                return y;
            }
        }
        return -1;
    }

    int wrapDtuY(char[][] map, int x) {
        for (int y = map.length - 1; y >= 0; y--) {
            if (map[y][x] != ' ') {
                return y;
            }
        }
        return -1;
    }

}
