import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day5 extends Day {

    public static void main(String[] args) {
        Day5 day = new Day5(); // https://adventofcode.com/2022/day/5

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals("CMZ", day.part1(sample));
        assertEquals("ZWHVFWQWW", day.part1(full));

        assertEquals("MCD", day.part2(sample));
        assertEquals("HZFZCCWWV", day.part2(full));

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

    private String partSolution(String input, boolean isCrateMover9001) {
        Pattern instructionPattern = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");
        String[] lines = input.split("\n");

        Map<Integer, Deque<Character>> stacksMap = new TreeMap<>();
        boolean parsingInstructions = false;
        for (String line : lines) {
            if (line.length() == 0) {
                parsingInstructions = true;
                continue;
            }

            if (parsingInstructions) {
                Matcher m = instructionPattern.matcher(line);
                if (m.find()) {
                    int cratesToMove = Integer.parseInt(m.group(1));
                    Integer fromStack = Integer.valueOf(m.group(2));
                    Integer toStack = Integer.valueOf(m.group(3));

                    List<Character> buf = new ArrayList<>();
                    for (int j = 0; j < cratesToMove; j++) {
                        buf.add(stacksMap.get(fromStack).pop());
                    }

                    if (isCrateMover9001) {
                        Collections.reverse(buf);
                    }

                    for (Character crate : buf) {
                        stacksMap.get(toStack).push(crate);
                    }

                }
            } else {
                if (!line.contains("[")) {
                    continue;
                }

                // parsing crates
                for (int j = 0; j < (line.length() + 1) / 4; j++) {
                    Deque<Character> stack = stacksMap.computeIfAbsent(j + 1, k -> new LinkedList<>());
                    char crate = line.charAt(j * 4 + 1);
                    if (crate != ' ') {
                        stack.add(crate);
                    }
                }
            }
        }

        return stacksMap.values().stream()
            .map(Deque::peek)
            .map(String::valueOf)
            .collect(Collectors.joining(""));
    }
}
