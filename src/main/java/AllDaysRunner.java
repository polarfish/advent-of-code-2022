import java.util.stream.Stream;

public class AllDaysRunner {

    public static void main(String[] args) {
        System.out.println("Running Advent Of Code 2022");
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
            new Day11()
        ).forEach(day -> {
            System.out.println();
            day.run();
        });
    }

}
