import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day11 extends Day {

    public static void main(String[] args) {
        Day11 day = new Day11(); // https://adventofcode.com/2022/day/11

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(10605, day.part1(sample));
        assertEquals(55930, day.part1(full));

        assertEquals(2713310158L, day.part2(sample));
        assertEquals(14636993466L, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }

    @Override
    public String part1(String input) {
        return String.valueOf(
            partSolution(input, 20, true)
        );
    }

    @Override
    public String part2(String input) {
        return String.valueOf(
            partSolution(input, 10_000, false)
        );
    }

    Long partSolution(String input, int rounds, boolean isReliefEnabled) {
        List<Monkey> monkeys = new ArrayList<>();

        String[] lines = input.split("\n");
        BigInteger lcm = BigInteger.ONE;
        for (int i = 0; i < lines.length; i += 7) {
            int monkeyNumber = Integer.parseInt(lines[i].substring(7, lines[i].length() - 1));
            Deque<BigInteger> monkeyItems = Arrays.stream(lines[i + 1].substring(18).split(","))
                .map(String::trim)
                .map(BigInteger::new)
                .collect(Collectors.toCollection(LinkedList::new));
            BigInteger dividedBy = new BigInteger(lines[i + 3].substring(21));
            lcm = lcm.divide(lcm.gcd(dividedBy)).multiply(dividedBy);

            monkeys.add(
                new Monkey(
                    monkeyNumber,
                    monkeyItems,
                    createMonkeyOperation(lines[i + 2]),
                    createMonkeyTest(lines[i + 3], lines[i + 4], lines[i + 5])));
        }

        long[] inspections = new long[monkeys.size()];
        for (int i = 0; i < rounds; i++) {
            for (Monkey m : monkeys) {
                while (!m.items.isEmpty()) {
                    inspections[m.n]++;
                    BigInteger item = m.items.remove();
                    item = m.operation.apply(item);

                    item = isReliefEnabled
                        ? item.divide(BigInteger.valueOf(3))
                        : item.remainder(lcm);

                    monkeys.get(m.test.apply(item)).items.add(item);
                }
            }
        }

        Arrays.sort(inspections);

        return inspections[inspections.length - 1] * inspections[inspections.length - 2];
    }

    record Monkey(int n,
                  Deque<BigInteger> items,
                  Function<BigInteger, BigInteger> operation,
                  Function<BigInteger, Integer> test) {

    }

    Function<BigInteger, BigInteger> createMonkeyOperation(String operationLine) {
        String secondOperand = operationLine.substring(25);
        boolean againstConstant = secondOperand.charAt(0) != 'o';
        BigInteger secondOperandBigInteger = againstConstant ? new BigInteger(secondOperand) : null;
        return switch (operationLine.charAt(23)) {
            case '+' -> againstConstant
                ? i -> i.add(secondOperandBigInteger)
                : i -> i.add(i);
            case '*' -> againstConstant
                ? i -> i.multiply(secondOperandBigInteger)
                : i -> i.pow(2);
            default -> null;
        };
    }

    Function<BigInteger, Integer> createMonkeyTest(String testLine, String ifTrueLine, String ifFalseLine) {
        BigInteger divisibleBy = new BigInteger(testLine.substring(21));
        int ifTrueMonkeyNumber = Integer.parseInt(ifTrueLine.substring(29));
        int ifFalseMonkeyNumber = Integer.parseInt(ifFalseLine.substring(30));
        return i -> BigInteger.ZERO.compareTo(i.remainder(divisibleBy)) == 0 ? ifTrueMonkeyNumber : ifFalseMonkeyNumber;
    }

}
