import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day19 extends Day {

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
                Map<Resource, Map<Resource, Integer>> robotsCost;
                blueprints.add(new State(
                    timeLimit,
                    0,
                    robotsCost = Map.of(
                        Resource.ORE, Map.of(
                            Resource.ORE, Integer.valueOf(matcher.group(2)),
                            Resource.CLAY, 0,
                            Resource.OBSIDIAN, 0,
                            Resource.GEODE, 0
                        ),
                        Resource.CLAY, Map.of(
                            Resource.ORE, Integer.valueOf(matcher.group(3)),
                            Resource.CLAY, 0,
                            Resource.OBSIDIAN, 0,
                            Resource.GEODE, 0
                        ),
                        Resource.OBSIDIAN, Map.of(
                            Resource.ORE, Integer.valueOf(matcher.group(4)),
                            Resource.CLAY, Integer.valueOf(matcher.group(5)),
                            Resource.OBSIDIAN, 0,
                            Resource.GEODE, 0
                        ),
                        Resource.GEODE, Map.of(
                            Resource.ORE, Integer.valueOf(matcher.group(6)),
                            Resource.CLAY, 0,
                            Resource.OBSIDIAN, Integer.valueOf(matcher.group(7)),
                            Resource.GEODE, 0
                        )
                    ),
                    Map.of(
                        Resource.ORE,
                        robotsCost.values().stream().mapToInt(m -> m.get(Resource.ORE)).max().orElseThrow(),
                        Resource.CLAY,
                        robotsCost.values().stream().mapToInt(m -> m.get(Resource.CLAY)).max().orElseThrow(),
                        Resource.OBSIDIAN,
                        robotsCost.values().stream().mapToInt(m -> m.get(Resource.OBSIDIAN)).max().orElseThrow(),
                        Resource.GEODE, Integer.MAX_VALUE
                    ),
                    Map.of(
                        Resource.ORE, 1,
                        Resource.CLAY, 0,
                        Resource.OBSIDIAN, 0,
                        Resource.GEODE, 0
                    ),
                    Map.of(
                        Resource.ORE, 0,
                        Resource.CLAY, 0,
                        Resource.OBSIDIAN, 0,
                        Resource.GEODE, 0
                    )
                ));
            }
        }
        return blueprints;
    }

    int calculateMaxGeodes(State initialState) {

        // System.out.println("simulating " + initialState.toString());

        int maxGeodes = 0;
        Resource[] robotsOptions = {
            Resource.GEODE,
            Resource.OBSIDIAN,
            Resource.CLAY,
            Resource.ORE
        };
        Deque<State> queue = new ArrayDeque<>();
        queue.add(initialState);
        State state;
        while ((state = queue.poll()) != null) {

            int potentialMaxGeodes = state.resources().get(Resource.GEODE)
                                     + state.robotsCount().get(Resource.GEODE) * state.minutesLeft()
                                     + (state.minutesLeft() - 1) * state.minutesLeft() / 2;

            if (potentialMaxGeodes <= maxGeodes) {
                continue;
            }

//            System.out.printf("Processed cycles %d Next cycle, queue length is %d max geodes %d%n", cyclesProcessed++, queue.size(), maxGeodes);
            int optionsCount = 0;
            if (state.minutesLeft() > 1) {
                for (Resource robot : robotsOptions) {
                    State nextState = state.buildNextRobot(robot);
                    if (nextState == null) {
                        continue;
                    }
                    queue.add(nextState);
                    optionsCount++;
                }
            }
            if (optionsCount == 0) {
                int thisBranchMaxGeodes = state.resources().get(Resource.GEODE)
                                          + state.robotsCount().get(Resource.GEODE) * state.minutesLeft();
                maxGeodes = Math.max(maxGeodes, thisBranchMaxGeodes);
            }
        }
        return maxGeodes;
    }

    enum Resource {
        ORE,
        CLAY,
        OBSIDIAN,
        GEODE
    }

    record State(
        int timeLimit,
        int minute,
        Map<Resource, Map<Resource, Integer>> robotsCost,
        Map<Resource, Integer> maxRobotsCost,
        Map<Resource, Integer> robotsCount,
        Map<Resource, Integer> resources
    ) {

        int minutesLeft() {
            return timeLimit - minute;
        }

        State buildNextRobot(Resource robot) {
            if (!canBuildRobot(robot)) {
                return null;
            }

            if (robotsCount.get(robot) >= maxRobotsCost.get(robot)) {
                //System.out.printf("Skip building %s robot because it is enough of them %n", robot);
                return null;
            }

            int minutesRequiredToStartBuilding = 0;
            Map<Resource, Integer> robotCost = robotsCost.get(robot);
            for (Resource resource : robotCost.keySet()) {
                if (resources.get(resource) >= robotCost.get(resource)) {
                    continue;
                }

                minutesRequiredToStartBuilding = Math.max(
                    minutesRequiredToStartBuilding,
                    (robotCost.get(resource) - resources.get(resource) + robotsCount.get(resource) - 1)
                    / robotsCount.get(resource)
                );
            }

            int minutesRequiredToBuild = minutesRequiredToStartBuilding + 1;

            return new State(
                timeLimit,
                minute + minutesRequiredToBuild,
                robotsCost,
                maxRobotsCost,
                Map.of(
                    Resource.ORE, robotsCount.get(Resource.ORE) + (robot == Resource.ORE ? 1 : 0),
                    Resource.CLAY, robotsCount.get(Resource.CLAY) + (robot == Resource.CLAY ? 1 : 0),
                    Resource.OBSIDIAN, robotsCount.get(Resource.OBSIDIAN) + (robot == Resource.OBSIDIAN ? 1 : 0),
                    Resource.GEODE, robotsCount.get(Resource.GEODE) + (robot == Resource.GEODE ? 1 : 0)
                ),
                Map.of(
                    Resource.ORE, resources.get(Resource.ORE) + minutesRequiredToBuild * robotsCount.get(Resource.ORE)
                                  - robotCost.get(Resource.ORE),
                    Resource.CLAY,
                    resources.get(Resource.CLAY) + minutesRequiredToBuild * robotsCount.get(Resource.CLAY)
                    - robotCost.get(Resource.CLAY),
                    Resource.OBSIDIAN,
                    resources.get(Resource.OBSIDIAN) + minutesRequiredToBuild * robotsCount.get(Resource.OBSIDIAN)
                    - robotCost.get(Resource.OBSIDIAN),
                    Resource.GEODE,
                    resources.get(Resource.GEODE) + minutesRequiredToBuild * robotsCount.get(Resource.GEODE)
                    - robotCost.get(Resource.GEODE)
                ));
        }

        private boolean canBuildRobot(Resource robot) {
            Map<Resource, Integer> robotCost = robotsCost.get(robot);
            for (Resource resource : robotCost.keySet()) {
                Integer resourceCost = robotCost.get(resource);
                if (resourceCost == 0) {
                    continue;
                }
                int totalPotentialResourceCount =
                    resources.get(resource) + robotsCount.get(resource) * minutesLeft();
                if (resourceCost >= totalPotentialResourceCount) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            return "\n    State{" +
                   "\n        minute=" + minute +
                   ",\n        robotsCost=" + robotsCost +
                   ",\n        robotsCount=" + robotsCount +
                   ",\n        resources=" + resources +
                   "\n    }";
        }
    }
}
