import java.util.function.BiFunction;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;

public class Day8 extends Day {

    Day8() {
        super(8, "Number of visible tree", "Best scenic score");
    }

    public static void main(String[] args) {
        Day8 day = new Day8();

        assertEquals(21, day.part1(readFile("Day8_sample.txt")));

        assertEquals(8, day.part2(readFile("Day8_sample.txt")));

        day.run();
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
        return String.valueOf(
            processForest(
                prepareForest(input),
                this::calculateScenicScore,
                Integer::max)
        );
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

}
