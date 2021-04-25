package homework.ch11_13.p4;

public class NullIterator implements Iterator{
    public boolean hasNext() {
        return false;
    }

    public Component next() {
        return null;
    }
}
