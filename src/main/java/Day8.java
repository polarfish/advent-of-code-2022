import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;

public class Day8 extends Day {

    public static void main(String[] args) {
        Day8 day = new Day8(); // https://adventofcode.com/2022/day/8

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(21, day.part1(sample));
        assertEquals(1688, day.part1(full));

        assertEquals(8, day.part2(sample));
        assertEquals(410400, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2Solution1, "Part 2 result (check all directions)");
        day.run(full, day::part2Solution2, "Part 2 result (use monotonic decreasing stack)");
    }


    @Override
    public String part1(String input) {
        return String.valueOf(
            processForest(
                prepareForest(input),
                this::calculateVisibility,
                Integer::sum)
        );
    }

    @Override
    public String part2(String input) {
        return part2Solution2(input);
    }

    // check all directions for every point
    String part2Solution1(String input) {
        return String.valueOf(
            processForest(
                prepareForest(input),
                this::calculateScenicScore,
                Integer::max));
    }

    // use monotonic decreasing stack
    String part2Solution2(String input) {
        int[][] arr = prepareForest(input);

        int[][] buf = initialiseScoreBuffer(new int[arr.length][arr[0].length]);

        for (int i = 1; i < arr.length - 1; i++) {
            MonotonicDecreasingStack left = new MonotonicDecreasingStack();
            MonotonicDecreasingStack right = new MonotonicDecreasingStack();
            left.push(0, arr[i][0]);
            right.push(0, arr[i][arr[0].length - 1]);

            for (int j = 1; j < arr[0].length - 1; j++) {
                buf[i][j] *= left.push(j, arr[i][j]);
                buf[i][arr[0].length - j - 1] *= right.push(j, arr[i][arr[0].length - j - 1]);
            }
        }

        for (int j = 1; j < arr[0].length - 1; j++) {
            MonotonicDecreasingStack top = new MonotonicDecreasingStack();
            MonotonicDecreasingStack bottom = new MonotonicDecreasingStack();
            top.push(0, arr[0][j]);
            bottom.push(0, arr[arr.length - 1][j]);
            for (int i = 1; i < arr.length - 1; i++) {
                buf[i][j] *= top.push(i, arr[i][j]);
                buf[arr.length - i - 1][j] *= bottom.push(i, arr[arr.length - i - 1][j]);
            }
        }

        return String.valueOf(
            Arrays.stream(buf)
                .mapToInt(line -> Arrays.stream(line).max().orElseThrow())
                .max().orElseThrow());
    }

    int[][] prepareForest(String input) {
        String[] lines = input.split("\n");
        int[][] arr = new int[lines.length][lines[0].length()];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = lines[i].charAt(j) - 48;
            }
        }

        return arr;
    }

    int[][] initialiseScoreBuffer(int[][] buf) {
        for (int i = 1; i < buf.length - 1; i++) {
            for (int j = 1; j < buf[0].length - 1; j++) {
                buf[i][j] = 1;
            }
        }
        return buf;
    }

    int processForest(int[][] arr, BiFunction<int[][], int[], Integer> map, IntBinaryOperator reduce) {
        int[] coords = new int[2];
        int acc = 0;
        for (coords[0] = 0; coords[0] < arr.length; coords[0]++) {
            for (coords[1] = 0; coords[1] < arr[0].length; coords[1]++) {
                acc = reduce.applyAsInt(acc, map.apply(arr, coords));
            }
        }
        return acc;
    }

    int calculateVisibility(int[][] arr, int[] coords) {
        int i = coords[0];
        int j = coords[1];
        int tree = arr[i][j];
        int h = arr.length;
        int w = arr.length;
        int[] visibility = {1, 1, 1, 1}; // default - visible from all 4 directions

        if (i == 0 || j == 0 || i == h - 1 || j == w - 1) {
            return 1;
        }

        // look up
        for (int k = i - 1; k >= 0; k--) {
            if (arr[k][j] >= tree) {
                visibility[0] = 0;
                break;
            }
        }

        // look right
        for (int k = j + 1; k < w; k++) {
            if (arr[i][k] >= tree) {
                visibility[1] = 0;
                break;
            }
        }

        // look down
        for (int k = i + 1; k < h; k++) {
            if (arr[k][j] >= tree) {
                visibility[2] = 0;
                break;
            }
        }

        // look left
        for (int k = j - 1; k >= 0; k--) {
            if (arr[i][k] >= tree) {
                visibility[3] = 0;
                break;
            }
        }

        return Integer.signum(IntStream.of(visibility).sum());
    }

    int calculateScenicScore(int[][] arr, int[] coords) {
        int i = coords[0];
        int j = coords[1];
        int tree = arr[i][j];
        int h = arr.length;
        int w = arr.length;
        int[] scenicScores = {0, 0, 0, 0}; // default - zero scenery from all 4 directions

        if (i == 0 || j == 0 || i == h - 1 || j == w - 1) {
            return 0;
        }

        // look up
        for (int k = i - 1; k >= 0; k--) {
            scenicScores[0]++;
            if (arr[k][j] >= tree) {
                break;
            }
        }

        // look right
        for (int k = j + 1; k < w; k++) {
            scenicScores[1]++;
            if (arr[i][k] >= tree) {
                break;
            }
        }

        // look down
        for (int k = i + 1; k < h; k++) {
            scenicScores[2]++;
            if (arr[k][j] >= tree) {
                break;
            }
        }

        // look left
        for (int k = j - 1; k >= 0; k--) {
            scenicScores[3]++;
            if (arr[i][k] >= tree) {
                break;
            }
        }

        return IntStream.of(scenicScores).reduce(1, (l, r) -> l * r);
    }

    record MonotonicDecreasingStack(ArrayDeque<Tree> stack) {

        MonotonicDecreasingStack() {
            this(new ArrayDeque<>());
        }

        int push(int position, int height) {
            while (!stack.isEmpty() && stack.peek().height() < height) {
                stack.pop();
            }
            int score = stack.isEmpty() ? position : (position - stack.peek().position());
            stack.push(new Tree(position, height));
            return score;
        }
    }

    record Tree(int position, int height) {

    }

}
