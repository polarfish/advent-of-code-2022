import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day19 extends Day {

    private static final int ORE = 0;
    private static final int CLAY = 1;
    private static final int OBSIDIAN = 2;
    private static final int GEODE = 3;

    private static final Pattern INPUT_PATTERN = Pattern.compile(
        "Blueprint (\\d+): "
        + "Each ore robot costs (\\d+) ore. "
        + "Each clay robot costs (\\d+) ore. "
        + "Each obsidian robot costs (\\d+) ore and (\\d+) clay. "
        + "Each geode robot costs (\\d+) ore and (\\d+) obsidian."
    );

    public static void main(String[] args) {
        Day19 day = new Day19();  // https://adventofcode.com/2022/day/19

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(33, day.part1(sample));
        assertEquals(1115, day.part1(full));

        assertEquals(3472, day.part2(sample));
        assertEquals(25056, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        String[] lines = input.split("\n");
        List<State> blueprints = parseBluePrints(lines, 24);

        int result = IntStream.range(0, blueprints.size())
            .map(i -> (i + 1) * calculateMaxGeodes(blueprints.get(i)))
            .sum();

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        String[] lines = input.split("\n");
        List<State> blueprints = parseBluePrints(lines, 32);

        int result = blueprints.stream()
            .limit(3)
            .mapToInt(this::calculateMaxGeodes)
            .reduce(1, (i1, i2) -> i1 * i2);

        return String.valueOf(result);
    }

    private static List<State> parseBluePrints(String[] lines, int timeLimit) {
        List<State> blueprints = new ArrayList<>(lines.length);
        for (String line : lines) {
            Matcher matcher = INPUT_PATTERN.matcher(line);
            if (matcher.find()) {
                int[][] robotsCost;
                blueprints.add(new State(
                    timeLimit,
                    0,
                    robotsCost = new int[][]{
                        {Integer.parseInt(matcher.group(2)), 0, 0, 0},
                        {Integer.parseInt(matcher.group(3)), 0, 0, 0},
                        {Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5)), 0, 0},
                        {Integer.parseInt(matcher.group(6)), 0, Integer.parseInt(matcher.group(7)), 0}
                    },
                    new int[]{
                        Math.max(
                            Math.max(robotsCost[0][0], robotsCost[1][0]),
                            Math.max(robotsCost[2][0], robotsCost[3][0])),
                        robotsCost[2][1],
                        robotsCost[3][2],
                        Integer.MAX_VALUE
                    },
                    new int[]{1, 0, 0, 0},
                    new int[]{0, 0, 0, 0}
                ));
            }
        }
        return blueprints;
    }

    int calculateMaxGeodes(State initialState) {

        int maxGeodes = 0;
        int[] robotsOptions = {
            GEODE,
            OBSIDIAN,
            CLAY,
            ORE
        };
        Deque<State> queue = new ArrayDeque<>();
        queue.add(initialState);
        State state;
        while ((state = queue.poll()) != null) {

            int potentialMaxGeodes = state.resources()[GEODE]
                                     + state.robotsCount()[GEODE] * state.minutesLeft()
                                     + (state.minutesLeft() - 1) * state.minutesLeft() / 2;

            if (potentialMaxGeodes <= maxGeodes) {
                continue;
            }

            int optionsCount = 0;
            if (state.minutesLeft() > 1) {
                for (int robot : robotsOptions) {
                    State nextState = state.buildNextRobot(robot);
                    if (nextState == null) {
                        continue;
                    }
                    queue.add(nextState);
                    optionsCount++;
                }
            }
            if (optionsCount == 0) {
                int thisBranchMaxGeodes = state.resources()[GEODE]
                                          + state.robotsCount()[GEODE] * state.minutesLeft();
                maxGeodes = Math.max(maxGeodes, thisBranchMaxGeodes);
            }
        }
        return maxGeodes;
    }

    record State(
        int timeLimit,
        int minute,
        int[][] robotsCost,
        int[] maxRobotsCost,
        int[] robotsCount,
        int[] resources
    ) {

        int minutesLeft() {
            return timeLimit - minute;
        }

        State buildNextRobot(int robot) {
            if (!canBuildRobot(robot)) {
                return null;
            }

            if (robotsCount[robot] >= maxRobotsCost[robot]) {
                return null;
            }

            int minutesRequiredToStartBuilding = 0;
            int[] robotCost = robotsCost[robot];

            for (int i = 0; i < robotCost.length; i++) {
                if (resources[i] >= robotCost[i]) {
                    continue;
                }

                minutesRequiredToStartBuilding = Math.max(
                    minutesRequiredToStartBuilding,
                    (robotCost[i] - resources[i] + robotsCount[i] - 1) / robotsCount[i]
                );
            }

            int minutesRequiredToBuild = minutesRequiredToStartBuilding + 1;

            return new State(
                timeLimit,
                minute + minutesRequiredToBuild,
                robotsCost,
                maxRobotsCost,
                new int[]{
                    robotsCount[ORE] + (robot == ORE ? 1 : 0),
                    robotsCount[CLAY] + (robot == CLAY ? 1 : 0),
                    robotsCount[OBSIDIAN] + (robot == OBSIDIAN ? 1 : 0),
                    robotsCount[GEODE] + (robot == GEODE ? 1 : 0)
                },
                new int[]{
                    resources[ORE] + minutesRequiredToBuild * robotsCount[ORE]
                    - robotCost[ORE],
                    resources[CLAY] + minutesRequiredToBuild * robotsCount[CLAY]
                    - robotCost[CLAY],
                    resources[OBSIDIAN]
                    + minutesRequiredToBuild * robotsCount[OBSIDIAN]
                    - robotCost[OBSIDIAN],
                    resources[GEODE] + minutesRequiredToBuild * robotsCount[GEODE]
                    - robotCost[GEODE]
                }
            );
        }

        private boolean canBuildRobot(int robot) {
            int[] robotCost = robotsCost[robot];

            for (int i = 0; i < robotCost.length; i++) {
                int resourceCost = robotCost[i];
                if (resourceCost == 0) {
                    continue;
                }
                int totalPotentialResourceCount = resources[i] + robotsCount[i] * minutesLeft();
                if (resourceCost >= totalPotentialResourceCount) {
                    return false;
                }
            }
            return true;
        }

    }
}
