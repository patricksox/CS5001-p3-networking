/**
 * The class of synchronized counter which helps keep track of the number of connections.
 *
 */
public class ConnectionCount {
private int counter;
/**
 * Initialize counter with initial value 0.
 */
public ConnectionCount() {
this(0);
}
/**
 * Initialize counter with initial value.
 * @param counter - Initial number
 */
public ConnectionCount(int counter) {
this.counter = counter;
}
/**
 * Retrieve current value of counter.
 * @return current value of counter
 */
public int getCounter() {
return this.counter;
}
/**
 * Add the counter when new connection is established.
 */
public synchronized void add() {
counter += 1;
}
/**
 * Delete the counter when a connection is terminated.
 */
public synchronized void delete() {
counter -= 1;
}
/**
 * Make the couter show by String.
 */
@Override
public String toString() {
return "" + counter;
}
}
