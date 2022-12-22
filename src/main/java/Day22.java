import java.util.Arrays;

public class Day22 extends Day {

    public static void main(String[] args) {
        Day22 day = new Day22();  // https://adventofcode.com/2022/day/0

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(6032, day.part1(sample));
        assertEquals(88226, day.part1(full));

//        assertEquals(5031, day.part2(sample));
//        assertEquals(0, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
//        day.run(full, day::part2, "Part 2 result");
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
                move(map, pos, steps);
                steps = 0;
                pos[2] = ((pos[2] - 1) + 4) % 4;
            } else if (nextChar == 'R') {
                move(map, pos, steps);
                steps = 0;
                pos[2] = (pos[2] + 1) % 4;
            } else {
                steps = steps * 10 + (nextChar - 48);
            }
        }
        move(map, pos, steps);

//        printMap(map, pos);


        return String.valueOf(
            (pos[1] + 1) * 1000 + (pos[0] + 1) * 4 + pos[2]
        );
    }

    @Override
    public String part2(String input) {
        return "0";
    }

    void move(char[][] map, int[] pos, int steps) {
        int x = pos[0];
        int y = pos[1];
        for (int i = 0; i < steps; i++) {
            switch (pos[2]) {
                case 0 -> {
                    if (x == map[0].length - 1 || map[y][x + 1] == ' ') {
                        x = wrapLtrX(map, y);
                    } else {
                        x++;
                    }
                    if (map[y][x] == '#') {
                        return;
                    } else {
                        pos[0] = x;
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
                        pos[1] = y;
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
                        pos[0] = x;
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
                        pos[1] = y;
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

    void printMap(char[][] map, int[] pos) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (pos[0] == x && pos[1] == y) {
                    System.out.print('@');
                } else {
                    System.out.print(map[y][x]);
                }
            }
            System.out.println();
        }
    }
}
