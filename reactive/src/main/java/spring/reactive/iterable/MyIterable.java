package spring.reactive.iterable;

import java.util.Iterator;

public class MyIterable implements Iterable<Integer> {
    @Override
    public Iterator<Integer> iterator() {
        return new MyIterator();
    }
}
