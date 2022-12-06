public class Day0 extends Day {

    Day0() {
        super(0);
    }

    public static void main(String[] args) {
        Day0 day = new Day0();

        assertEquals(0, day.part1(readFile("Day0_sample.txt")));

        assertEquals(0, day.part2(readFile("Day0_sample.txt")));

        day.run();
    }


    @Override
    public String part1(String input) {
        return String.valueOf(input.length());
    }

    public String part2(String input) {
        return String.valueOf(input.length());
    }
}
