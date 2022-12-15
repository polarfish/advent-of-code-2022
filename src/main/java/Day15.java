import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15 extends Day {

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
        String[] lines = input.split("\n");
        int[][] sn = new int[lines.length][2];
        int[] sg = new int[lines.length];
        int[][] bc = new int[lines.length][2];
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        Pattern pattern = Pattern.compile(
            "Sensor at x=([\\-\\d]+), y=([\\-\\d]+): closest beacon is at x=([\\-\\d]+), y=([\\-\\d]+)");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                sn[i][0] = Integer.parseInt(matcher.group(1));
                sn[i][1] = Integer.parseInt(matcher.group(2));
                minY = Math.min(minY, sn[i][1]);
                maxY = Math.max(maxY, sn[i][1]);
                bc[i][0] = Integer.parseInt(matcher.group(3));
                bc[i][1] = Integer.parseInt(matcher.group(4));
                minY = Math.min(minY, bc[i][1]);
                maxY = Math.max(maxY, bc[i][1]);

                sg[i] = dst(sn[i][0], bc[i][0]) + dst(sn[i][1], bc[i][1]);
            }
        }

        int targetLine = minY <= 2000000 && maxY >= 2000000
            ? 2000000
            : 10;

        List<int[]> intervals = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            if (dst(sn[i][1], targetLine) <= sg[i]) {
                int overlap = sg[i] - dst(sn[i][1], targetLine);
                intervals.add(new int[]{sn[i][0] - overlap, sn[i][0] + overlap});
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
            - Arrays.stream(bc).filter(b -> b[1] == targetLine).map(b -> b[0]).distinct().count()
        );
    }

    private int dst(int i1, int i2) {
        return i1 > i2 ? i1 - i2 : i2 - i1;
    }

    @Override
    public String part2(String input) {
        String[] lines = input.split("\n");
        int[][] sn = new int[lines.length][2];
        int[] sg = new int[lines.length];
        int[][] bc = new int[lines.length][2];
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        Pattern pattern = Pattern.compile(
            "Sensor at x=([\\-\\d]+), y=([\\-\\d]+): closest beacon is at x=([\\-\\d]+), y=([\\-\\d]+)");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                sn[i][0] = Integer.parseInt(matcher.group(1));
                sn[i][1] = Integer.parseInt(matcher.group(2));
                minY = Math.min(minY, sn[i][1]);
                maxY = Math.max(maxY, sn[i][1]);
                bc[i][0] = Integer.parseInt(matcher.group(3));
                bc[i][1] = Integer.parseInt(matcher.group(4));
                minY = Math.min(minY, bc[i][1]);
                maxY = Math.max(maxY, bc[i][1]);

                sg[i] = dst(sn[i][0], bc[i][0]) + dst(sn[i][1], bc[i][1]);
            }
        }

        int positionLimit = minY <= 2000000 && maxY >= 2000000
            ? 4000000
            : 20;

        int x;
        int y;
        for (int i = 0; i < sn.length; i++) {
            int[] s = sn[i];

            x = s[0];
            y = s[1] - sg[i] - 1;
            main:
            while (x <= s[0] + sg[i] && y < s[1]) {

                if (x < 0 || x > positionLimit || y < 0 || y > positionLimit) {
                    x++;
                    y++;
                    continue;
                }

                for (int j = 0; j < sn.length; j++) {
                    if (dst(x, sn[j][0]) + dst(y, sn[j][1]) <= sg[j]) {
                        x++;
                        y++;
                        continue main;
                    }
                }

                return String.valueOf(x * (long) 4000000 + y);
            }

            x = s[0] + sg[i] + 1;
            y = s[1];
            main:
            while (x > s[0] && y <= s[1] + sg[i]) {

                if (x < 0 || x > positionLimit || y < 0 || y > positionLimit) {
                    x--;
                    y++;
                    continue;
                }

                for (int j = 0; j < sn.length; j++) {
                    if (dst(x, sn[j][0]) + dst(y, sn[j][1]) <= sg[j]) {
                        x--;
                        y++;
                        continue main;
                    }
                }

                return String.valueOf(x * (long) 4000000 + y);
            }

            x = s[0];
            y = s[1] + sg[i] + 1;
            main:
            while (x >= s[0] - sg[i] && y > s[1]) {

                if (x < 0 || x > positionLimit || y < 0 || y > positionLimit) {
                    x--;
                    y--;
                    continue;
                }

                for (int j = 0; j < sn.length; j++) {
                    if (dst(x, sn[j][0]) + dst(y, sn[j][1]) <= sg[j]) {
                        x--;
                        y--;
                        continue main;
                    }
                }

                return String.valueOf(x * (long) 4000000 + y);
            }

            x = s[0] - sg[i] - 1;
            y = s[1];
            main:
            while (x < s[0] && y >= s[1] - sg[i]) {

                if (x < 0 || x > positionLimit || y < 0 || y > positionLimit) {
                    x++;
                    y--;
                    continue;
                }

                for (int j = 0; j < sn.length; j++) {
                    if (dst(x, sn[j][0]) + dst(y, sn[j][1]) <= sg[j]) {
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

}
