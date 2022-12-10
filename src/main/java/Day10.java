import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Day10 extends Day {

    Day10() {
        super(10, "Sum of six signal strengths", "CRT output");
    }

    public static void main(String[] args) {
        Day10 day = new Day10();

        assertEquals(13140, day.part1(readFile("Day10_sample.txt")));

        assertEquals(
            """
                                
                ##..##..##..##..##..##..##..##..##..##..
                ###...###...###...###...###...###...###.
                ####....####....####....####....####....
                #####.....#####.....#####.....#####.....
                ######......######......######......####
                #######.......#######.......#######.....""",
            day.part2(readFile("Day10_sample.txt")));

        day.run();
    }


    @Override
    public String part1(String input) {
        final int[] result = {0};
        Set<Integer> signalIndexes = Set.of(20, 60, 100, 140, 180, 220);
        processCommands(input, (c, x) -> {
            if (signalIndexes.contains(c)) {
                result[0] += c * x;
            }
        });
        return String.valueOf(result[0]);
    }

    public String part2(String input) {
        final char[][] crt = new char[6][40];
        processCommands(input, (c, x) -> {
            if (c <= 240) {
                int i = c - 1;
                crt[i / 40][i % 40] = Math.abs(i % 40 - x) < 2 ? '#' : '.';
            }
        });
        return "\n" + Arrays.stream(crt)
            .map(String::copyValueOf)
            .collect(Collectors.joining("\n"));
    }

    void processCommands(String input, BiConsumer<Integer, Integer> cycleHandler) {
        ArrayDeque<Command> queue = new ArrayDeque<>();
        int cyclesLeft = 0;
        int x = 1;

        Arrays.stream(input.split("\n")).map(this::parseCommand).forEach(queue::add);
        int cycle = 0;

        do {
            // before cycle
            cycle++;
            if (cyclesLeft == 0) {
                cyclesLeft = queue.element().cycles();
            }

            // during cycle
            cycleHandler.accept(cycle, x);

            // execute command
            if (--cyclesLeft == 0) {
                x = queue.remove().execute(x);
                if (!queue.isEmpty()) {
                    cyclesLeft = queue.element().cycles();
                }
            }
        } while (!queue.isEmpty());
    }

    Command parseCommand(String line) {
        return switch (line.charAt(0)) {
            case 'a' -> new AddX(Integer.parseInt(line.substring(5)));
            case 'n' -> Noop.INSTANCE;
            default -> throw new IllegalArgumentException();
        };
    }

    interface Command {

        int cycles();

        int execute(int x);

    }

    record Noop() implements Command {

        static final Noop INSTANCE = new Noop();

        @Override
        public int cycles() {
            return 1;
        }

        @Override
        public int execute(int x) {
            return x;
        }

        @Override
        public String toString() {
            return "noop";
        }
    }

    record AddX(int x) implements Command {

        @Override
        public int cycles() {
            return 2;
        }

        @Override
        public int execute(int x) {
            return x + this.x;
        }

        @Override
        public String toString() {
            return "addx " + x;
        }
    }
}