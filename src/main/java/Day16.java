import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day16 extends Day {

    private static final Pattern INPUT_PATTERN = Pattern.compile(
        "Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z ,]+)");

    public static void main(String[] args) {
        Day16 day = new Day16();  // https://adventofcode.com/2022/day/16

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(1651, day.part1(sample));
        assertEquals(1906, day.part1(full));

        assertEquals(1707, day.part2(sample));
        assertEquals(2548, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        Map<String, Valve> valvesMap = new HashMap<>();
        Map<String, String[]> valveIdToConnectedValvesIds = new HashMap<>();

        String[] lines = input.split("\n");
        Arrays.stream(lines).forEach(line -> {
                Matcher m = INPUT_PATTERN.matcher((line));
                if (!m.matches()) {
                    throw new IllegalArgumentException();
                }

                String id = m.group(1);
                int rate = Integer.parseInt(m.group(2));
                String[] tunnels = m.group(3).split(", ");

                valvesMap.put(id, new Valve(id, rate));
                valveIdToConnectedValvesIds.put(id, tunnels);

            }
        );

        valveIdToConnectedValvesIds.forEach((id, connectedValvesIds) -> {
            Valve valve = valvesMap.get(id);
            for (String connectedValveId : connectedValvesIds) {
                valve.tunnels().add(valvesMap.get(connectedValveId));
            }
        });

        Map<Valve, Map<Valve, Integer>> distanceMap = new HashMap<>();

        for (Valve valve : valvesMap.values()) {
            calculateDistances(valve, distanceMap.computeIfAbsent(valve, value -> new HashMap<>()));
        }

        Valve aa = valvesMap.get("AA");
        int[] result = {0};
        closeValves(distanceMap, aa, result);

        return String.valueOf(result[0]);
    }

    public String part2(String input) {
        Map<String, Valve> valvesMap = new HashMap<>();
        Map<String, String[]> valveIdToConnectedValvesIds = new HashMap<>();

        String[] lines = input.split("\n");
        Arrays.stream(lines).forEach(line -> {
                Matcher m = INPUT_PATTERN.matcher((line));
                if (!m.matches()) {
                    throw new IllegalArgumentException();
                }

                String id = m.group(1);
                int rate = Integer.parseInt(m.group(2));
                String[] tunnels = m.group(3).split(", ");

                valvesMap.put(id, new Valve(id, rate));
                valveIdToConnectedValvesIds.put(id, tunnels);

            }
        );

        valveIdToConnectedValvesIds.forEach((id, connectedValvesIds) -> {
            Valve valve = valvesMap.get(id);
            for (String connectedValveId : connectedValvesIds) {
                valve.tunnels().add(valvesMap.get(connectedValveId));
            }
        });

        Map<Valve, Map<Valve, Integer>> distanceMap = new HashMap<>();

        for (Valve valve : valvesMap.values()) {
            calculateDistances(valve, distanceMap.computeIfAbsent(valve, value -> new HashMap<>()));
        }

        Valve aa = valvesMap.get("AA");
        int[] result = {0};
        closeValves2(distanceMap, aa, result);

        return String.valueOf(result[0]);
    }

    private void closeValves(Map<Valve, Map<Valve, Integer>> distanceMap,
        Valve startingValve, int[] result) {
        closeValves(new LinkedHashSet<>(), startingValve, distanceMap, 0, 1, 0, result);
    }

    private void closeValves(Set<Valve> openValves, Valve valve, Map<Valve, Map<Valve, Integer>> distanceMap,
        int pressurePerMinute, int minutes, int previousPressure, int[] result) {
        int openingScore = 0;
        if (valve.rate() > 0) {
            openingScore = valve.rate() * (30 - minutes);
            minutes++;
            openValves.add(valve);
        }

        int processedNextValves = 0;
        for (Entry<Valve, Integer> nextValveEntry : distanceMap.get(valve).entrySet()) {
            Valve nextValve = nextValveEntry.getKey();
            Integer nextDistance = nextValveEntry.getValue();

            if (nextValve == valve || nextValve.rate() == 0 || openValves.contains(nextValve)
                || nextDistance >= 30 - minutes) {
                continue;
            }

            closeValves(
                openValves,
                nextValve,
                distanceMap,
                pressurePerMinute + valve.rate(),
                minutes + nextDistance,
                previousPressure + openingScore,
                result);

            processedNextValves++;
        }

        if (processedNextValves == 0) {
            result[0] = Math.max(result[0], previousPressure + openingScore);
//            System.out.println(openValves.stream().map(Valve::id).collect(Collectors.joining(" -> ")) + " = " + (previousPressure + openingScore));
        }

        openValves.remove(valve);
    }

    private void closeValves2(Map<Valve, Map<Valve, Integer>> distanceMap,
        Valve startingValve, int[] result) {

        List<Valve> significantValves = distanceMap.keySet().stream().filter(v -> v.rate() > 0).toList();

        for (int i = 1, limit = ((1 << significantValves.size()) - 1); i < limit; i++) {
            Set<Valve> humanSet = new HashSet<>();
            Set<Valve> elephantSet = new HashSet<>();

            int buf = i;
            for (int j = 0; j < significantValves.size(); j++) {
                if ((buf & 1) == 1) {
                    humanSet.add(significantValves.get(j));
                } else {
                    elephantSet.add(significantValves.get(j));
                }
                buf >>= 1;
            }

            int[] humanResult = {0};
            int[] elephantResult = {0};
            closeValves2(humanSet, startingValve, distanceMap, 0, 1, 0, elephantResult);
            closeValves2(elephantSet, startingValve, distanceMap, 0, 1, 0, humanResult);

            result[0] = Math.max(
                result[0],
                humanResult[0] + elephantResult[0]
            );
        }
    }

    private void closeValves2(Set<Valve> openValves, Valve valve, Map<Valve, Map<Valve, Integer>> distanceMap,
        int pressurePerMinute, int minutes, int previousPressure, int[] result) {
        int openingScore = 0;
        if (valve.rate() > 0) {
            openingScore = valve.rate() * (26 - minutes);
            minutes++;
            openValves.add(valve);
        }

        int processedNextValves = 0;
        for (Entry<Valve, Integer> nextValveEntry : distanceMap.get(valve).entrySet()) {
            Valve nextValve = nextValveEntry.getKey();
            Integer nextDistance = nextValveEntry.getValue();

            if (nextValve == valve || nextValve.rate() == 0 || openValves.contains(nextValve)
                || nextDistance >= 26 - minutes) {
                continue;
            }

            closeValves2(
                openValves,
                nextValve,
                distanceMap,
                pressurePerMinute + valve.rate(),
                minutes + nextDistance,
                previousPressure + openingScore,
                result);

            processedNextValves++;
        }

        if (processedNextValves == 0) {
            result[0] = Math.max(result[0], previousPressure + openingScore);
        }

        openValves.remove(valve);
    }

    void calculateDistances(Valve valve, Map<Valve, Integer> distanceMap) {
        distanceMap.put(valve, 0);
        ArrayDeque<Valve> stack = new ArrayDeque<>();
        stack.push(valve);
        while (!stack.isEmpty()) {
            Valve next = stack.remove();
            for (Valve tunnel : next.tunnels()) {
                if (!distanceMap.containsKey(tunnel)) {
                    distanceMap.put(tunnel, distanceMap.get(next) + 1);
                    stack.add(tunnel);
                }
            }
        }
    }

    record Valve(String id, int rate, List<Valve> tunnels) {

        public Valve(String id, int rate) {
            this(id, rate, new ArrayList<>());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Valve valve = (Valve) o;

            return Objects.equals(id, valve.id);
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "{id=%s, rate=%d, tunnels=[%s]}".formatted(
                id, rate, tunnels.stream().map(Valve::id).collect(Collectors.joining(",")));
        }
    }
}
