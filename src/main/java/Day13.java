import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day13 extends Day {

    public static void main(String[] args) {
        Day13 day = new Day13();  // https://adventofcode.com/2022/day/13

        String sample = readFile("Day13_sample.txt");
        String full = readFile("Day13.txt");

        assertEquals(13, day.part1(sample));
        assertEquals(4809, day.part1(full));

        assertEquals(140, day.part2(sample));
        assertEquals(22600, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        List<List<ListNode>> pairs = partition(
            Arrays.stream(input.split("\n"))
                .filter(Predicate.not(String::isEmpty))
                .map(ListNode::parseNode)
                .toList(),
            2);

        int result = 0;
        for (int i = 0; i < pairs.size(); i++) {
            if (pairs.get(i).get(0).compareTo(pairs.get(i).get(1)) < 0) {
                result += (i + 1);
            }
        }

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        ListNode extra6 = ListNode.parseNode("[[6]]");
        ListNode extra2 = ListNode.parseNode("[[2]]");

        List<ListNode> nodes = Stream.concat(
                Stream.of(extra2, extra6),
                Arrays.stream(input.split("\n"))
                    .filter(Predicate.not(String::isEmpty))
                    .map(ListNode::parseNode)
            )
            .sorted()
            .toList();

        return String.valueOf(
            (nodes.indexOf(extra2) + 1) * (nodes.indexOf(extra6) + 1)
        );
    }


    interface Node {

    }

    record ListNode(List<Node> nodes) implements Node, Comparable<ListNode> {

        public ListNode() {
            this(new ArrayList<>());
        }

        public ListNode(Node node) {
            this();
            nodes.add(node);
        }

        @Override
        public String toString() {
            return "[%s]".formatted(nodes.stream().map(Objects::toString).collect(Collectors.joining(",")));
        }

        public static ListNode parseNode(String line) {
            ListNode current = null;
            LinkedList<ListNode> stack = new LinkedList<>();
            for (int i = 0; i < line.length(); i++) {
                switch (line.charAt(i)) {
                    case '[':
                        ListNode listNode = new ListNode();
                        if (current != null) {
                            current.nodes.add(listNode);
                            stack.push(current);
                        }
                        current = listNode;
                        break;
                    case ']':
                        current = stack.isEmpty() ? current : stack.pop();
                        break;
                    case ',':
                        break;
                    default:
                        int value = Character.isDigit(line.charAt(i + 1))
                            ? (line.charAt(i) - 48) * 10 + (line.charAt(++i) - 48)
                            : (line.charAt(i) - 48);
                        current.nodes.add(new ValueNode(value));
                }
            }

            return current;
        }

        @Override
        public int compareTo(ListNode o) {
            return compare(this, o);
        }

        private int compare(Node left, Node right) {
            if (left instanceof ValueNode leftValue && right instanceof ValueNode rightValue) {
                return leftValue.compareTo(rightValue);
            } else if (left instanceof ListNode leftList && right instanceof ListNode rightList) {
                Iterator<Node> leftIterator = leftList.nodes().iterator();
                Iterator<Node> rightIterator = rightList.nodes().iterator();
                while (leftIterator.hasNext() || rightIterator.hasNext()) {
                    if (!leftIterator.hasNext()) {
                        return -1;
                    }
                    if (!rightIterator.hasNext()) {
                        return 1;
                    }

                    int result = compare(leftIterator.next(), rightIterator.next());

                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            } else {
                return compare(
                    left instanceof ValueNode ? new ListNode(left) : left,
                    right instanceof ValueNode ? new ListNode(right) : right
                );
            }
        }
    }

    record ValueNode(int value) implements Node, Comparable<ValueNode> {

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public int compareTo(ValueNode o) {
            return Integer.compare(this.value, o.value);
        }
    }
}
