import java.util.Arrays;

public class Day14 extends Day {

    public static void main(String[] args) {
        Day14 day = new Day14();  // https://adventofcode.com/2022/day/14

        String sample = readFile("Day14_sample.txt");
        String full = readFile("Day14.txt");

        assertEquals(24, day.part1(sample));
        assertEquals(888, day.part1(full));

        assertEquals(93, day.part2(sample));
        assertEquals(26461, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        return partSolution(input, false);
    }

    @Override
    public String part2(String input) {
        return partSolution(input, true);
    }

    public String partSolution(String input, boolean hasFloor) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (String s : input.split(" -> |\n")) {
            String[] split = s.split(",");
            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }

        int caveHeight = hasFloor ? maxY + 2 : maxY;

        minX = Math.min(minX, 500 - caveHeight);
        maxX = Math.max(maxX, 500 + caveHeight);

        // 0 - air, 1 - rock, 2 - sand
        int[][] map = new int[caveHeight + 2][maxX - minX + 3];

        for (String line : input.split("\n")) {
            int[][] coords = Arrays.stream(line.split(" -> ")).map(s -> {
                String[] split = s.split(",");
                return new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])};
            }).toArray(int[][]::new);

            for (int i = 0; i < coords.length - 1; i++) {
                int x1 = coords[i][0] - minX + 1;
                int y1 = coords[i][1];
                int x2 = coords[i + 1][0] - minX + 1;
                int y2 = coords[i + 1][1];

                map[y1][x1] = 1;
                while (x1 != x2 || y1 != y2) {
                    x1 += Integer.signum(x2 - x1);
                    y1 += Integer.signum(y2 - y1);
                    map[y1][x1] = 1;
                }
            }
        }

        if (hasFloor) {
            Arrays.fill(map[caveHeight], 1);
        }

        int sandInRestCount = 0;
        main:
        while (map[0][501 - minX] == 0) {
            int x = 501 - minX;
            int y = 0;

            while (map[y + 1][x] == 0 || map[y + 1][x - 1] == 0 || map[y + 1][x + 1] == 0) {
                if (map[y + 1][x] == 0) {
                    y = y + 1;
                } else if (map[y + 1][x - 1] == 0) {
                    y = y + 1;
                    x = x - 1;
                } else if (map[y + 1][x + 1] == 0) {
                    y = y + 1;
                    x = x + 1;
                }
                if (y > caveHeight) {
                    break main;
                }
            }
            map[y][x] = 2;
            sandInRestCount++;
        }

        return String.valueOf(sandInRestCount);
    }

}
