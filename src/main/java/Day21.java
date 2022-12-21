import java.util.HashMap;
import java.util.Map;

public class Day21 extends Day {

    private static final String ROOT_MONKEY = "root";
    private static final String HUMAN = "humn";

    public static void main(String[] args) {
        Day21 day = new Day21();  // https://adventofcode.com/2022/day/21

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(152, day.part1(sample));
        assertEquals(145167969204648L, day.part1(full));

        assertEquals(301, day.part2(sample));
        assertEquals(3330805295850L, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        Map<String, Expression> expressions = new HashMap<>();
        Map<String, Long> results = new HashMap<>();
        buildExpressionTree(input, expressions, results);

        return String.valueOf(
            expressions.get(ROOT_MONKEY).execute()
        );
    }

    private static void buildExpressionTree(
        String input, Map<String, Expression> expressions, Map<String, Long> results) {
        for (String line : input.split("\n")) {
            String name = line.substring(0, 4);
            if (line.matches(".*[+\\-*/]+.*")) {
                expressions.put(
                    name,
                    new Expression(
                        expressions,
                        results,
                        line.charAt(11),
                        line.substring(6, 10),
                        line.substring(13, 17)));
            } else {
                results.put(name, Long.parseLong(line.substring(6)));
            }
        }
    }

    @Override
    public String part2(String input) {

        Map<String, Expression> expressions = new HashMap<>();
        Map<String, Long> results = new HashMap<>();

        buildExpressionTree(input, expressions, results);

        results.put(HUMAN, null);
        Expression root = expressions.get(ROOT_MONKEY);
        Expression rootLeft = expressions.get(root.left());
        Expression rootRight = expressions.get(root.right());
        Long rootLeftResult = rootLeft.execute();
        Long rootRightResult = rootRight.execute();

        return String.valueOf(
            rootLeftResult == null
                ? rootLeft.rewind(rootRightResult)
                : rootRight.rewind(rootLeftResult));
    }

    record Expression(Map<String, Expression> expressions,
                      Map<String, Long> results,
                      char operation,
                      String left,
                      String right) {

        public Long execute() {
            Long leftResult = results.get(left);
            if (leftResult == null && expressions.containsKey(left)) {
                results.put(left, leftResult = expressions.get(left).execute());
            }
            Long rightResult = results.get(right);
            if (rightResult == null && expressions.containsKey(right)) {
                results.put(right, rightResult = expressions.get(right).execute());
            }

            if (leftResult == null || rightResult == null) {
                return null;
            }

            return switch (operation) {
                case '+' -> leftResult + rightResult;
                case '-' -> leftResult - rightResult;
                case '*' -> leftResult * rightResult;
                case '/' -> leftResult / rightResult;
                default -> throw new IllegalStateException("Unexpected value: " + operation);
            };
        }

        public Long rewind(long expectedResult) {
            long nextExpectedResult;
            Long leftResult = results.get(left);
            Long rightResult = results.get(right);

            if (leftResult == null) {
                nextExpectedResult = switch (operation) {
                    case '+' -> expectedResult - rightResult;
                    case '-' -> expectedResult + rightResult;
                    case '*' -> expectedResult / rightResult;
                    case '/' -> expectedResult * rightResult;
                    default -> throw new IllegalStateException("Unexpected value: " + operation);
                };
                return HUMAN.equals(left)
                    ? nextExpectedResult
                    : expressions.get(left).rewind(nextExpectedResult);
            } else {
                nextExpectedResult = switch (operation) {
                    case '+' -> expectedResult - leftResult;
                    case '-' -> leftResult - expectedResult;
                    case '*' -> expectedResult / leftResult;
                    case '/' -> expectedResult * leftResult;
                    default -> throw new IllegalStateException("Unexpected value: " + operation);
                };
                return HUMAN.equals(right)
                    ? nextExpectedResult
                    : expressions.get(right).rewind(nextExpectedResult);
            }
        }
    }
}
