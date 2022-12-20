import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Day20 extends Day {

    public static void main(String[] args) {
        Day20 day = new Day20();  // https://adventofcode.com/2022/day/20

        String sample = readFile("%s_sample.txt".formatted(day.name()));
        String full = readFile("%s.txt".formatted(day.name()));

        assertEquals(3, day.part1(sample));
        assertEquals(9945, day.part1(full));

        assertEquals(1623178306, day.part2(sample));
        assertEquals(3338877775442L, day.part2(full));

        day.run(full, day::part1, "Part 1 result");
        day.run(full, day::part2, "Part 2 result");
    }


    @Override
    public String part1(String input) {
        List<Node> nodes = parseNodes(input);

        Node zeroNode = createCircularList(nodes);

        performMovements(nodes, 1, 1);

        long answer = getGroveCoordinates(zeroNode);

        return String.valueOf(answer);
    }

    @Override
    public String part2(String input) {
        int encryptionKey = 811589153;

        List<Node> nodes = parseNodes(input);

        Node zeroNode = createCircularList(nodes);

        performMovements(nodes, 10, encryptionKey);

        long answer = getGroveCoordinates(zeroNode);

        return String.valueOf(answer * encryptionKey);
    }

    private Node createCircularList(List<Node> nodes) {
        Iterator<Node> iter = nodes.iterator();
        Node zero = null;
        Node head = iter.next();
        Node prev = head;
        Node curr = null;
        while (iter.hasNext()) {
            curr = iter.next();
            if (curr.value == 0) {
                zero = curr;
            }
            prev.next = curr;
            curr.prev = prev;
            prev = curr;
        }

        curr.next = head;
        head.prev = curr;

        return zero;
    }

    private static void performMovements(List<Node> nodes, int times, int encryptionKey) {
        for (int i = 0; i < times; i++) {
            for (Node node : nodes) {
                node.move(encryptionKey, nodes.size());
            }
        }
    }

    private long getGroveCoordinates(Node zeroNode) {
        long answer = 0;
        Node cursor = zeroNode;
        for (int i = 1; i <= 3000; i++) {
            cursor = cursor.next;
            if (i == 1000 || i == 2000 || i == 3000) {
                answer += cursor.value;
            }
        }
        return answer;
    }

    private List<Node> parseNodes(String input) {
        return Arrays.stream(input.split("\n"))
            .map(Integer::parseInt)
            .map(Node::new).toList();
    }

    static class Node {

        public Node(int value) {
            this.value = value;
        }

        final int value;
        Node next;
        Node prev;

        public void move(int encryptionKey, int totalNodes) {
            if (value == 0) {
                return;
            }

            long lim = ((long) encryptionKey) * Math.abs(value) % (totalNodes - 1);

            Node newPlace = this;
            if (value > 0) {
                for (long i = 0; i < lim; i++) {
                    newPlace = newPlace.next == this ? newPlace.next.next : newPlace.next;
                }
                moveBetween(newPlace, newPlace.next);
            } else {
                for (long i = 0; i < lim; i++) {
                    newPlace = newPlace.prev == this ? newPlace.prev.prev : newPlace.prev;
                }
                moveBetween(newPlace.prev, newPlace);
            }

        }

        private void moveBetween(Node n1, Node n2) {
            if (n1 == this || n2 == this) {
                return;
            }

            next.prev = prev;
            prev.next = next;

            n1.next = this;
            prev = n1;
            n2.prev = this;
            next = n2;
        }
    }
}
