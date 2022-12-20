import java.util.stream.Stream;

public class AllDaysRunner {

    public static void main(String[] args) {
        System.out.println("Running Advent Of Code 2022");
        long totalTime =
            Stream.of(
                new Day1(),
                new Day2(),
                new Day3(),
                new Day4(),
                new Day5(),
                new Day6(),
                new Day7(),
                new Day8(),
                new Day9(),
                new Day10(),
                new Day11(),
                new Day12(),
                new Day13(),
                new Day14(),
                new Day15(),
                new Day16(),
                new Day17(),
                new Day18(),

                new Day20()
            ).mapToLong(day -> {
                System.out.println();
                return day.run();
            }).sum();

        System.out.println();
        System.out.printf("Total time: %d ms%n", totalTime);
    }

}
