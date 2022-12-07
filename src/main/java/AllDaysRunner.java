import java.util.stream.Stream;

public class AllDaysRunner {

    public static void main(String[] args) {
        Stream.of(
            new Day1(),
            new Day2(),
            new Day3(),
            new Day4(),
            new Day5(),
            new Day6(),
            new Day7()
        ).forEach(day -> {
            System.out.println();
            day.run();
        });
    }

}
