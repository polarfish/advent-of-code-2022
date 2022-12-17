import java.util.ArrayList;
import java.util.List;

public class Day17 extends Day {

    public static void main(String[] args) {
        Day17 day = new Day17();  // https://adventofcode.com/2022/day/17

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(3068, day.part1(sample));
        assertEquals(3119, day.part1(full));

        assertEquals(1514285714288L, day.part2(sample));
        assertEquals(1536994219669L, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    @Override
    public String part1(String input) {
        int[][][] shapes = new int[][][]{
            {{0, 0}, {1, 0}, {2, 0}, {3, 0}},
            {{0, 1}, {1, 0}, {1, 1}, {1, 2}, {2, 1}},
            {{0, 0}, {1, 0}, {2, 0}, {2, 1}, {2, 2}},
            {{0, 0}, {0, 1}, {0, 2}, {0, 3}},
            {{0, 0}, {1, 0}, {0, 1}, {1, 1}}
        };

        int[][] chamber = new int[4 * 2022][7];
        int highest = -1;
        int lastHighest;
        int j = 0;

        for (int i = 0; i < 2022; i++) {
            int[][] shape = shapes[i % 5];
            int[] coords = {2, highest + 4};

            do {
                if (input.charAt(j++ % input.length()) == '>') {
                    moveRight(chamber, coords, shape);
                } else {
                    moveLeft(chamber, coords, shape);
                }

                lastHighest = moveDown(chamber, coords, shape, highest);
                highest = Math.max(highest, lastHighest);

            } while (lastHighest == -1);

        }

        return String.valueOf(highest + 1);
    }

    @Override
    public String part2(String input) {
        int[][][] shapes = new int[][][]{
            {{0, 0}, {1, 0}, {2, 0}, {3, 0}},
            {{0, 1}, {1, 0}, {1, 1}, {1, 2}, {2, 1}},
            {{0, 0}, {1, 0}, {2, 0}, {2, 1}, {2, 2}},
            {{0, 0}, {0, 1}, {0, 2}, {0, 3}},
            {{0, 0}, {1, 0}, {0, 1}, {1, 1}}
        };

        int multiplier = 20;
        int[][] chamber = new int[4 * 2022 * multiplier][7];
        int highest = -1; // floor
        int lastHighest;
        int j = 0;
        int totalMoves = input.length();

        List<Integer> ladder = new ArrayList<>();
        int zeroIterationHeight = 0;
        int zeroIterationShapes = 0;

        int everyIterationHeight = 0;
        int everyIterationShapes = 0;

        int stage = 0;

        for (int i = 0; i < 2022 * multiplier; i++) {
            int[][] shape = shapes[i % 5];
            int[] coords = {2, highest + 4};

            do {
                if (input.charAt(j++ % totalMoves) == '>') {
                    moveRight(chamber, coords, shape);
                } else {
                    moveLeft(chamber, coords, shape);
                }

                lastHighest = moveDown(chamber, coords, shape, highest);

                if (lastHighest != -1) {
                    highest = Math.max(highest, lastHighest);
                }
            } while (lastHighest == -1);

            if (j % totalMoves < 2) {
                stage++;
                if (stage == 1) {
                    zeroIterationHeight = highest;
                    zeroIterationShapes = i + 1;
                } else if (stage == 2) {
                    everyIterationHeight = highest - zeroIterationHeight;
                    everyIterationShapes = i + 1 - zeroIterationShapes;
                } else {
                    break;
                }
            }

            if (stage == 1) {
                ladder.add(highest - zeroIterationHeight);
            }
        }

        return String.valueOf(
            zeroIterationHeight +
            ((1000000000000L - zeroIterationShapes) / everyIterationShapes) * everyIterationHeight
            + ladder.get((int) ((1000000000000L - zeroIterationShapes) % everyIterationShapes) + 1)
        );
    }

    private void moveLeft(int[][] chamber, int[] coords, int[][] shape) {
        for (int[] shapeMod : shape) {
            int x = coords[0] + shapeMod[0];
            int y = coords[1] + shapeMod[1];
            int newX = x - 1;
            if (newX < 0 || chamber[y][newX] == 1) {
                return;
            }
        }
        coords[0]--;
    }

    private void moveRight(int[][] chamber, int[] coords, int[][] shape) {
        for (int[] shapeMod : shape) {
            int x = coords[0] + shapeMod[0];
            int y = coords[1] + shapeMod[1];
            int newX = x + 1;
            if (newX > 6 || chamber[y][newX] == 1) {
                return;
            }
        }
        coords[0]++;
    }

    private int moveDown(int[][] chamber, int[] coords, int[][] shape, int highest) {
        boolean comeToRest = false;
        for (int[] shapeMod : shape) {
            int x = coords[0] + shapeMod[0];
            int y = coords[1] + shapeMod[1];
            int newY = y - 1;
            if (newY < 0 || chamber[newY][x] == 1) {
                comeToRest = true;
                break;
            }
        }

        if (!comeToRest) {
            coords[1]--;
            return -1;
        }

        int shapeHighest = -1;
        for (int[] shapeMod : shape) {
            int x = coords[0] + shapeMod[0];
            int y = coords[1] + shapeMod[1];
            chamber[y][x] = 1;
            shapeHighest = Math.max(shapeHighest, y);
        }

        return Math.max(highest, shapeHighest);
    }

}
