import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class Day12 extends Day {

    public static void main(String[] args) {
        Day12 day = new Day12(); // https://adventofcode.com/2022/day/12

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(31, day.part1(sample));
        assertEquals(394, day.part1(full));

        assertEquals(29, day.part2(sample));
        assertEquals(388, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        HillsMap map = buildHillsMap(input);

        return String.valueOf(
            findDistance(map.start(), n -> map.end().equals(n), Node::connectedNodes)
        );
    }

    @Override
    public String part2(String input) {
        HillsMap map = buildHillsMap(input);

        return String.valueOf(
            findDistance(map.end(), n -> n.height() == 0, Node::connectedFromNodes)
        );
    }

    HillsMap buildHillsMap(String input) {
        String[] lines = input.split("\n");
        Node start = null;
        Node end = null;
        Node[][] nodes = new Node[lines[0].length()][lines.length];
        for (int x = 0; x < nodes.length; x++) {
            for (int y = 0; y < nodes[0].length; y++) {

                char heightChar = lines[y].charAt(x);
                int height = switch (heightChar) {
                    case 'S' -> 'a' - 97;
                    case 'E' -> 'z' - 97;
                    default -> heightChar - 97;
                };

                Node n = new Node(x, y, height);
                nodes[x][y] = switch (heightChar) {
                    case 'S' -> start = n;
                    case 'E' -> end = n;
                    default -> n;
                };
            }
        }

        Node top;
        Node right;
        Node bottom;
        Node left;
        for (int x = 0; x < nodes.length; x++) {
            for (int y = 0; y < nodes[0].length; y++) {
                Node node = nodes[x][y];
                if (y > 0 && canPass(node, top = nodes[x][y - 1])) {
                    connectNodes(node, top);
                }

                if (x > 0 && canPass(node, right = nodes[x - 1][y])) {
                    connectNodes(node, right);
                }

                if (y < nodes[0].length - 1 && canPass(node, bottom = nodes[x][y + 1])) {
                    connectNodes(node, bottom);
                }

                if (x < nodes.length - 1 && canPass(node, left = nodes[x + 1][y])) {
                    connectNodes(node, left);
                }
            }
        }

        return new HillsMap(nodes, start, end);
    }


    private int findDistance(Node from, Predicate<Node> toPredicate,
        Function<Node, Iterable<Node>> accessibleNodesSupplier) {
        Map<Node, Integer> distanceToStart = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        distanceToStart.put(from, 0);
        visited.add(from);
        queue.add(from);

        Node node;
        while ((node = queue.poll()) != null) {
            for (Node connectedNode : accessibleNodesSupplier.apply(node)) {
                if (toPredicate.test(connectedNode)) {
                    return distanceToStart.get(node) + 1;
                }
                if (!visited.contains(connectedNode)) {
                    distanceToStart.put(connectedNode, distanceToStart.get(node) + 1);
                    queue.add(connectedNode);
                    visited.add(connectedNode);
                }
            }
        }

        return -1;
    }

    private boolean canPass(Node from, Node to) {
        return to.height() <= from.height + 1;
    }

    private void connectNodes(Node from, Node to) {
        from.connectedNodes().add(to);
        to.connectedFromNodes().add(from);
    }

    record HillsMap(Node[][] nodes, Node start, Node end) {

    }

    record Node(int x, int y, int height, Set<Node> connectedNodes, Set<Node> connectedFromNodes) {

        Node(int x, int y, int height) {
            this(x, y, height, new HashSet<>(), new HashSet<>());
        }

        @Override
        public boolean equals(Object obj) {
            return (this == obj);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "[x=%d,y=%d,h=%d]".formatted(x, y, height);
        }
    }

}
