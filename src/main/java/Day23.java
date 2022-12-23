import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Day23 extends Day {

    public static void main(String[] args) {
        Day23 day = new Day23();  // https://adventofcode.com/2022/day/23

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(110, day.part1(sample));
        assertEquals(3877, day.part1(full));

        assertEquals(20, day.part2(sample));
        assertEquals(982, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        List<Elf> elves = parseElves(input);

        simulate(elves, 10);

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Elf elf : elves) {
            minX = Math.min(minX, elf.location.x);
            maxX = Math.max(maxX, elf.location.x);
            minY = Math.min(minY, elf.location.y);
            maxY = Math.max(maxY, elf.location.y);
        }
        int result = (maxX - minX + 1) * (maxY - minY + 1) - elves.size();

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        List<Elf> elves = parseElves(input);

        int step = simulate(elves, 0);

        return String.valueOf(step);
    }

    private int simulate(List<Elf> elves, int stepLimit) {
        if (stepLimit <= 0) {
            stepLimit = Integer.MAX_VALUE;
        }
        Set<Point> locations = new HashSet<>();
        elves.forEach(elf -> locations.add(elf.location));
        int step = 0;
        while (step++ < stepLimit) {
            Map<Point, List<Elf>> proposedLocationsMap = new HashMap<>();
            for (Elf elf : elves) {
                Point proposedPoint = elf.proposeMove(locations);
                if (proposedPoint == null) {
                    continue;
                }
                proposedLocationsMap.computeIfAbsent(proposedPoint, p -> new ArrayList<>()).add(elf);
            }

            if (proposedLocationsMap.isEmpty()) {
                break;
            }

            for (Entry<Point, List<Elf>> e : proposedLocationsMap.entrySet()) {
                if (e.getValue().size() > 1) {
                    continue;
                }

                locations.remove(e.getValue().get(0).location);
                e.getValue().get(0).location = e.getKey();
                locations.add(e.getKey());
            }
        }

        return step;
    }

    List<Elf> parseElves(String input) {
        String[] lines = input.split("\n");
        List<Elf> elves = new ArrayList<>();
        for (int y = 0; y < lines.length; y++) {
            for (int x = 0; x < lines[0].length(); x++) {
                if (lines[y].charAt(x) == '#') {
                    elves.add(new Elf(new Point(x, y)));
                }
            }
        }
        return elves;
    }

    record Point(int x, int y) {

    }


    static class Elf {

        private int direction; // 0 - N, 1 - S, 2 - W, 3 - E
        Point location;

        public Elf(Point location) {
            this.location = location;
        }

        int nextDirection() {
            return direction++ % 4;
        }

        Point proposeMove(Set<Point> locations) {
            int x = location.x;
            int y = location.y;
            boolean n = !locations.contains(new Point(x, y - 1));
            boolean s = !locations.contains(new Point(x, y + 1));
            boolean w = !locations.contains(new Point(x - 1, y));
            boolean e = !locations.contains(new Point(x + 1, y));
            boolean nw = !locations.contains(new Point(x - 1, y - 1));
            boolean ne = !locations.contains(new Point(x + 1, y - 1));
            boolean sw = !locations.contains(new Point(x - 1, y + 1));
            boolean se = !locations.contains(new Point(x + 1, y + 1));

            int direction = nextDirection();

            if (n & s & w & e & nw & ne & sw & se) {
                return null;
            }

            for (int i = 0; i < 4; i++) {
                switch ((direction + i) % 4) {
                    case 0 -> {
                        if (nw && n && ne) {
                            return new Point(x, y - 1);
                        }
                    }
                    case 1 -> {
                        if (sw && s && se) {
                            return new Point(x, y + 1);
                        }
                    }
                    case 2 -> {
                        if (nw && w && sw) {
                            return new Point(x - 1, y);
                        }
                    }
                    case 3 -> {
                        if (ne && e && se) {
                            return new Point(x + 1, y);
                        }
                    }
                }
            }

            return null;
        }
    }
}
