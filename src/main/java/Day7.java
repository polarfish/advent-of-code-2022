import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Day7 extends Day {

    public static void main(String[] args) {
        Day7 day = new Day7(); // https://adventofcode.com/2022/day/7

        String sample = readFile("Day7_sample.txt");
        String full = readFile("Day7.txt");

        assertEquals(95437, day.part1(sample));
        assertEquals(1644735, day.part1(full));

        assertEquals(24933642, day.part2(sample));
        assertEquals(1300850, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        ElfDir root = buildElfFileSystem(input);
        List<Long> sizes = new ArrayList<>();
        calculateDirectorySize(root, sizes);
        return String.valueOf(
            sizes.stream()
                .mapToLong(l -> l)
                .filter(s -> s <= 100000)
                .sum()
        );
    }

    public String part2(String input) {
        ElfDir root = buildElfFileSystem(input);
        NavigableSet<Long> sizes = new TreeSet<>();
        long rootSize = calculateDirectorySize(root, sizes);
        long unusedSpace = 70000000L - rootSize;
        long requiredSpace = 30000000L - unusedSpace;
        return String.valueOf(
            sizes.ceiling(requiredSpace)
        );
    }

    private ElfDir buildElfFileSystem(String input) {
        String[] lines = input.split("\n");
        ElfDir root = new ElfDir(null, "/");
        ElfDir current = root;
        for (String line : lines) {
            if (isCommand(line)) {
                if (isCd(line)) {
                    String target = line.substring(5);
                    switch (target) {
                        case "/" -> current = root;
                        case ".." -> current = current.parentDir;
                        default -> {
                            if (!current.dirs.containsKey(target)) {
                                current.dirs.put(target, new ElfDir(current, target));
                            }
                            current = current.dirs.get(target);
                        }
                    }
                }
            } else {
                if (isDirOutput(line)) {
                    String dir = line.substring(4);
                    if (!current.dirs.containsKey(dir)) {
                        current.dirs.put(dir, new ElfDir(current, dir));
                    }
                } else {
                    String[] parts = line.split(" ");
                    String name = parts[1];
                    Long size = Long.valueOf(parts[0]);

                    if (!current.files.containsKey(name)) {
                        current.files.put(name, new ElfFile(current, name, size));
                    }
                }
            }
        }
        return root;
    }

    Long calculateDirectorySize(ElfDir dir, Collection<Long> sizes) {
        long size = dir.files.values().stream().mapToLong(ElfFile::size).sum()
                    + dir.dirs.values().stream().mapToLong(d -> calculateDirectorySize(d, sizes)).sum();
        sizes.add(size);
        return size;
    }

    boolean isCommand(String line) {
        return line.charAt(0) == '$';
    }

    boolean isCd(String line) {
        return line.charAt(2) == 'c';
    }

    boolean isDirOutput(String line) {
        return line.charAt(0) == 'd';
    }

    record ElfDir(ElfDir parentDir, String name, Map<String, ElfDir> dirs, Map<String, ElfFile> files) {

        @Override
        public String toString() {
            return "{\"name\":\"%s\",\"dirs\":%s,\"files\":%s}".formatted(name, dirs.values(), files.values());
        }

        ElfDir(ElfDir parentDir, String name) {
            this(parentDir, name, new HashMap<>(), new HashMap<>());
        }
    }

    record ElfFile(ElfDir parentDir, String name, Long size) {

        @Override
        public String toString() {
            return "{\"name\":\"%s\",\"size\":%d}".formatted(name, size);
        }
    }

}
