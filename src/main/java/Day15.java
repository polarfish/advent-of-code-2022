import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15 extends Day {

    private static final Pattern INPUT_PATTERN = Pattern.compile(
        "Sensor at x=([\\-\\d]+), y=([\\-\\d]+): closest beacon is at x=([\\-\\d]+), y=([\\-\\d]+)");

    public static void main(String[] args) {
        Day15 day = new Day15();  // https://adventofcode.com/2022/day/15

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(26, day.part1(sample));
        assertEquals(5108096, day.part1(full));

        assertEquals(56000011, day.part2(sample));
        assertEquals(10553942650264L, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    @Override
    public String part1(String input) {
        List<Sensor> sensors = parseSensors(input);
        int targetLine = sensors.size() < 20 ? 10 : 2000000;

        List<int[]> intervals = new ArrayList<>();
        for (Sensor sensor : sensors) {
            if (dst(sensor.y(), targetLine) <= sensor.signal()) {
                int overlap = sensor.signal() - dst(sensor.y(), targetLine);
                intervals.add(new int[]{sensor.x() - overlap, sensor.x() + overlap});
            }
        }

        intervals.sort(Comparator.comparingInt(o -> o[0]));
        ArrayDeque<int[]> stack = new ArrayDeque<>();
        Iterator<int[]> iter = intervals.iterator();
        stack.push(Arrays.copyOf(iter.next(), 2));
        while (iter.hasNext()) {
            int[] interval = stack.element();
            int[] nextInterval = iter.next();
            if (interval[1] < nextInterval[0]) {
                stack.push(Arrays.copyOf(nextInterval, 2));
            } else if (interval[1] < nextInterval[1]) {
                interval[1] = nextInterval[1];
            }
        }

        return String.valueOf(
            stack.stream().mapToInt(o -> o[1] - o[0] + 1).sum()
            - sensors.stream().map(Sensor::beacon).filter(b -> b.y() == targetLine).map(Beacon::x).distinct().count()
        );
    }

    private int dst(int i1, int i2) {
        return i1 > i2 ? i1 - i2 : i2 - i1;
    }

    @Override
    public String part2(String input) {
        List<Sensor> sensors = parseSensors(input);
        int positionLimit = sensors.size() < 20 ? 20 : 4000000;

        int x;
        int y;
        for (Sensor s : sensors) {
            x = s.x();
            y = s.y() - s.signal() - 1;
            main:
            while (x <= s.x() + s.signal() && y < s.y()) {

                if (x < 0 || x > positionLimit || y < 0 || y > positionLimit) {
                    x++;
                    y++;
                    continue;
                }

                for (Sensor sensor : sensors) {
                    if (dst(x, sensor.x()) + dst(y, sensor.y()) <= sensor.signal()) {
                        x++;
                        y++;
                        continue main;
                    }
                }

                return String.valueOf(x * (long) 4000000 + y);
            }

            x = s.x() + s.signal() + 1;
            y = s.y();
            main:
            while (x > s.x() && y <= s.y() + s.signal()) {

                if (x < 0 || x > positionLimit || y < 0 || y > positionLimit) {
                    x--;
                    y++;
                    continue;
                }

                for (Sensor sensor : sensors) {
                    if (dst(x, sensor.x()) + dst(y, sensor.y()) <= sensor.signal()) {
                        x--;
                        y++;
                        continue main;
                    }
                }

                return String.valueOf(x * (long) 4000000 + y);
            }

            x = s.x();
            y = s.y() + s.signal() + 1;
            main:
            while (x >= s.x() - s.signal() && y > s.y()) {

                if (x < 0 || x > positionLimit || y < 0 || y > positionLimit) {
                    x--;
                    y--;
                    continue;
                }

                for (Sensor sensor : sensors) {
                    if (dst(x, sensor.x()) + dst(y, sensor.y()) <= sensor.signal()) {
                        x--;
                        y--;
                        continue main;
                    }
                }

                return String.valueOf(x * (long) 4000000 + y);
            }

            x = s.x() - s.signal() - 1;
            y = s.y();
            main:
            while (x < s.x() && y >= s.y() - s.signal()) {

                if (x < 0 || x > positionLimit || y < 0 || y > positionLimit) {
                    x++;
                    y--;
                    continue;
                }

                for (Sensor sensor : sensors) {
                    if (dst(x, sensor.x()) + dst(y, sensor.y()) <= sensor.signal()) {
                        x++;
                        y--;
                        continue main;
                    }
                }

                return String.valueOf(x * (long) 4000000 + y);
            }
        }

        return String.valueOf(
            0
        );
    }

    private List<Sensor> parseSensors(String input) {
        return Arrays.stream(input.split("\n")).map(line -> {
            Matcher matcher = INPUT_PATTERN.matcher(line);
            if (!matcher.matches()) {
                return null;
            }

            int sensorX = Integer.parseInt(matcher.group(1));
            int sensorY = Integer.parseInt(matcher.group(2));
            int beaconX = Integer.parseInt(matcher.group(3));
            int beaconY = Integer.parseInt(matcher.group(4));

            return new Sensor(
                sensorX,
                sensorY,
                dst(sensorX, beaconX) + dst(sensorY, beaconY),
                new Beacon(beaconX, beaconY));

        }).toList();
    }

    record Beacon(int x, int y) {

    }

    record Sensor(int x, int y, int signal, Beacon beacon) {

    }
}
