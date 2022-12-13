import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Day13 extends Day {

    Day13() {
        super(13);
    }

    public static void main(String[] args) {
        Day13 day = new Day13();

        assertEquals(13, day.part1(readFile("Day13_sample.txt")));
        assertEquals(140, day.part2(readFile("Day13_sample.txt")));

        day.run();
    }


    @Override
    public String part1(String input) {
        List<List<ListNode>> pairs = partition(
            Arrays.stream(input.split("\n"))
                .filter(Predicate.not(String::isEmpty))
                .map(this::parseNode)
                .toList(),
            2);

        int result = 0;
        for (int i = 0; i < pairs.size(); i++) {
            if (compare(pairs.get(i).get(0), pairs.get(i).get(1)) == -1) {
                result += (i + 1);
            }
        }

        return String.valueOf(result);
    }

    @Override
    public String part2(String input) {
        ListNode extra6 = parseNode("[[6]]");
        ListNode extra2 = parseNode("[[2]]");

        List<ListNode> nodes = Stream.concat(
                Stream.of(extra2, extra6),
                Arrays.stream(input.split("\n"))
                    .filter(Predicate.not(String::isEmpty))
                    .map(this::parseNode)
            )
            .sorted(this::compare)
            .toList();

        return String.valueOf(
            (nodes.indexOf(extra2) + 1) * (nodes.indexOf(extra6) + 1)
        );
    }

    private int compare(Node left, Node right) {
        if (left instanceof ValueNode leftValue && right instanceof ValueNode rightValue) {
            return Integer.compare(leftValue.value(), rightValue.value());
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

    ListNode parseNode(String line) {
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
                    int value = line.charAt(i + 1) == '0' ? 10 : (line.charAt(i) - 48);
                    current.nodes.add(new ValueNode(value));
            }
        }

        return current;
    }

    interface Node {

    }

    record ListNode(List<Node> nodes) implements Node {

        public ListNode() {
            this(new ArrayList<>());
        }

        public ListNode(Node node) {
            this();
            nodes.add(node);
        }
    }

    record ValueNode(int value) implements Node {

    }
}
