import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class Day18 extends Day {

    public static void main(String[] args) {
        Day18 day = new Day18();  // https://adventofcode.com/2022/day/18

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(64, day.part1(sample));
        assertEquals(4300, day.part1(full));

        assertEquals(58, day.part2(sample));
        assertEquals(2490, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {

        Set<Side> sides = new HashSet<>();
        int coveredSides = 0;

        for (String line : input.split("\n")) {
            String[] split = line.split(",");
            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);
            int z = Integer.parseInt(split[2]);

            coveredSides += sides.add(new Side(x, y, z, x + 1, y, z)) ? 0 : 1;
            coveredSides += sides.add(new Side(x - 1, y, z, x, y, z)) ? 0 : 1;
            coveredSides += sides.add(new Side(x, y, z, x, y + 1, z)) ? 0 : 1;
            coveredSides += sides.add(new Side(x, y - 1, z, x, y, z)) ? 0 : 1;
            coveredSides += sides.add(new Side(x, y, z, x, y, z + 1)) ? 0 : 1;
            coveredSides += sides.add(new Side(x, y, z - 1, x, y, z)) ? 0 : 1;
        }

        return String.valueOf(sides.size() - coveredSides);
    }

    record Side(int x1, int y1, int z1, int x2, int y2, int z2) {

    }

    record Cube(int x, int y, int z) {

    }

    @Override
    public String part2(String input) {

        Set<Cube> lavaCubes = new HashSet<>();

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (String line : input.split("\n")) {
            String[] split = line.split(",");
            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);
            int z = Integer.parseInt(split[2]);

            lavaCubes.add(new Cube(x, y, z));

            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
        }

        int lowLimitX = minX - 2;
        int lowLimitY = minY - 2;
        int lowLimitZ = minZ - 2;

        int highLimitX = maxX + 2;
        int highLimitY = maxY + 2;
        int highLimitZ = maxZ + 2;

        Set<Cube> visitedAirCubes = new HashSet<>();
        Deque<Cube> queue = new ArrayDeque<>();

        Cube firstCube = new Cube(lowLimitX + 1, lowLimitY + 1, lowLimitZ + 1);
        visitedAirCubes.add(firstCube);
        queue.add(firstCube);

        Cube cube;
        Cube nextCube;
        int sidesCounter = 0;
        while ((cube = queue.pollFirst()) != null) {
            if (cube.x() > lowLimitX + 1) {
                nextCube = new Cube(cube.x() - 1, cube.y(), cube.z());
                if (lavaCubes.contains(nextCube)) {
                    sidesCounter++;
                } else if (!visitedAirCubes.contains(nextCube)) {
                    visitedAirCubes.add(nextCube);
                    queue.add(nextCube);
                }
            }

            if (cube.x() < highLimitX - 1) {
                nextCube = new Cube(cube.x() + 1, cube.y(), cube.z());
                if (lavaCubes.contains(nextCube)) {
                    sidesCounter++;
                } else if (!visitedAirCubes.contains(nextCube)) {
                    visitedAirCubes.add(nextCube);
                    queue.add(nextCube);
                }
            }

            if (cube.y() > lowLimitY + 1) {
                nextCube = new Cube(cube.x(), cube.y() - 1, cube.z());
                if (lavaCubes.contains(nextCube)) {
                    sidesCounter++;
                } else if (!visitedAirCubes.contains(nextCube)) {
                    visitedAirCubes.add(nextCube);
                    queue.add(nextCube);
                }
            }

            if (cube.y() < highLimitY - 1) {
                nextCube = new Cube(cube.x(), cube.y() + 1, cube.z());
                if (lavaCubes.contains(nextCube)) {
                    sidesCounter++;
                } else if (!visitedAirCubes.contains(nextCube)) {
                    visitedAirCubes.add(nextCube);
                    queue.add(nextCube);
                }
            }

            if (cube.z() > lowLimitZ + 1) {
                nextCube = new Cube(cube.x(), cube.y(), cube.z() - 1);
                if (lavaCubes.contains(nextCube)) {
                    sidesCounter++;
                } else if (!visitedAirCubes.contains(nextCube)) {
                    visitedAirCubes.add(nextCube);
                    queue.add(nextCube);
                }
            }

            if (cube.z() < highLimitZ - 1) {
                nextCube = new Cube(cube.x(), cube.y(), cube.z() + 1);
                if (lavaCubes.contains(nextCube)) {
                    sidesCounter++;
                } else if (!visitedAirCubes.contains(nextCube)) {
                    visitedAirCubes.add(nextCube);
                    queue.add(nextCube);
                }
            }
        }

        return String.valueOf(sidesCounter);
    }
}
