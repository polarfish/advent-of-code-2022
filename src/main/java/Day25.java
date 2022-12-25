import java.util.Arrays;

public class Day25 extends Day {

    public static void main(String[] args) {
        Day25 day = new Day25();  // https://adventofcode.com/2022/day/25

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals("2=-1=0", day.part1(sample));
        assertEquals("2-0=11=-0-2-1==1=-22", day.part1(full));

        day.run(full, day::part1, "Part 1 result");
    }


    @Override
    public String part1(String input) {
        long result = Arrays.stream(input.split("\n"))
            .mapToLong(this::snafuToInt)
            .sum();
        return intToSnafu(result);
    }

    long snafuToInt(String num) {
        int len = num.length();

        long result = 0;
        long base = 1;
        for (int i = len - 1; i >= 0; i--) {
            char ch = num.charAt(i);
            result += switch (ch) {
                case '2' -> base * 2;
                case '1' -> base;
                case '0' -> 0;
                case '-' -> -base;
                case '=' -> -base * 2;
                default -> throw new IllegalStateException("Unexpected value: " + ch);
            };
            base *= 5;
        }

        return result;
    }

    String intToSnafu(long n) {
        StringBuilder sb = new StringBuilder();
        int buf = 0;
        while (n > 0) {
            int next = (int) (n % 5 + buf);
            sb.append(switch (next) {
                case 0, 5 -> '0';
                case 1 -> '1';
                case 2 -> '2';
                case 3 -> '=';
                case 4 -> '-';
                default -> throw new IllegalStateException("Unexpected value " + next);
            });

            buf = next > 2 ? 1 : 0;
            n /= 5;
        }

        if (buf == 1) {
            sb.append('1');
        }

        return sb.reverse().toString();
    }

    @Override
    public String part2(String input) {
        return "0";
    }
}
