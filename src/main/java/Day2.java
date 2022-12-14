import java.util.Arrays;
import java.util.Map;

public class Day2 extends Day {

    public static void main(String[] args) {
        Day2 day = new Day2(); // https://adventofcode.com/2022/day/2

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(15, day.part1(sample));
        assertEquals(13526, day.part1(full));

        assertEquals(12, day.part2(sample));
        assertEquals(14204, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    static final Map<Character, Character> LEFT_COLUMN_SHAPE_TRANSLATION = Map.of('A', 'R', 'B', 'P', 'C', 'S');
    static final Map<Character, Character> RIGHT_COLUMN_SHAPE_TRANSLATION = Map.of('X', 'R', 'Y', 'P', 'Z', 'S');
    static final Map<Character, Character> RIGHT_COLUMN_OUTCOME_TRANSLATION = Map.of('X', 'L', 'Y', 'D', 'Z', 'W');
    static final Map<Character, Integer> SHAPE_SCORE = Map.of('R', 1, 'P', 2, 'S', 3);

    private static final Map<Character, Map<Character, Integer>> GAME_SCORE = Map.of(
        'R', Map.of('R', 3, 'P', 6, 'S', 0),
        'P', Map.of('R', 0, 'P', 3, 'S', 6),
        'S', Map.of('R', 6, 'P', 0, 'S', 3)
    );

    private static final Map<Character, Map<Character, Character>> OUTCOME_TO_SHAPE = Map.of(
        'R', Map.of('L', 'S', 'D', 'R', 'W', 'P'),
        'P', Map.of('L', 'R', 'D', 'P', 'W', 'S'),
        'S', Map.of('L', 'P', 'D', 'S', 'W', 'R')
    );

    private int getScore1(String s) {
        char opp = LEFT_COLUMN_SHAPE_TRANSLATION.get(s.charAt(0));
        char my = RIGHT_COLUMN_SHAPE_TRANSLATION.get(s.charAt(2));
        return GAME_SCORE.get(opp).get(my) + SHAPE_SCORE.get(my);
    }

    private int getScore2(String s) {
        char opp = LEFT_COLUMN_SHAPE_TRANSLATION.get(s.charAt(0));
        char outcome = RIGHT_COLUMN_OUTCOME_TRANSLATION.get(s.charAt(2));
        char my = OUTCOME_TO_SHAPE.get(opp).get(outcome);
        return GAME_SCORE.get(opp).get(my) + SHAPE_SCORE.get(my);
    }

    @Override
    public String part1(String input) {
        return String.valueOf(
            Arrays.stream(input.split("\n"))
                .mapToInt(this::getScore1).sum());
    }

    @Override
    public String part2(String input) {
        return String.valueOf(
            Arrays.stream(input.split("\n"))
                .mapToInt(this::getScore2).sum());
    }
}
