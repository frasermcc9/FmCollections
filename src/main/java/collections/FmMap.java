package collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

/**
 * A Hashmap extension with additional utility methods.
 *
 * @author Fraser McCallum
 * @since 1.0
 */
public class FmMap<K, V> extends HashMap<K, V> {
    private static final long serialVersionUID = 1L;

    /**
     * Gets a random value from the map's values.
     *
     * @return a random value from the map.
     * @since 1.0
     */
    public V random() {
        var values = valueList();
        return values.get((int) (valueList().size() * Math.random()));
    }

    /**
     * Gets a random key from the maps key-set.
     *
     * @return a random key from this map.
     * @since 1.0
     */
    public K randomKey() {
        var values = keyList();
        return values.get((int) (valueList().size() * Math.random()));
    }

    /**
     * Maps each element of the map to another value, as given by the function. For example, List<String> list =
     * map((K,V)->K.toString()+V.toString())
     *
     * @param <R>    The return type for each element.
     * @param action The action to take on each element. Parameters K and V for the BiFunction represent the Key and
     *               Value for the map.
     * @return A list of the new values.
     * @since 1.0
     */
    public <R> List<R> map(BiFunction<? super K, ? super V, R> action) {
        List<R> list = new ArrayList<>();
        forEach((K, V) -> {
            list.add(action.apply(K, V));
        });
        return list;
    }

    /**
     * Returns a subset of the map. The elements that exist in the subset are those which are true for the given
     * predicate.
     *
     * @param predicate BiPredicate function that returns a boolean that each element is tested on.
     * @return A map containing all the elements that were truthy for the test.
     * @see FmMap#sweep(BiPredicate)
     * @since 1.0
     */
    public FmMap<K, V> filter(BiPredicate<? super K, ? super V> predicate) {
        FmMap<K, V> filtered = new FmMap<>();
        forEach((k, v) -> {
            if (predicate.test(k, v)) {
                filtered.put(k, v);
            }
        });
        return filtered;
    }

    /**
     * Returns a subset of the map. Any elements that pass the given predicate are not included in the returned map.
     *
     * @param predicate BiPredicate function that returns a boolean that each element is tested on.
     * @return A map containing all elements, except those which were true in the predicate.
     * @see FmMap#filter(BiPredicate)
     * @since 1.0
     */
    public FmMap<K, V> sweep(BiPredicate<? super K, ? super V> predicate) {
        FmMap<K, V> filtered = new FmMap<>();
        forEach((k, v) -> {
            if (!predicate.test(k, v)) {
                filtered.put(k, v);
            }
        });
        return filtered;
    }

    /**
     * Checks if every element in the map fits the condition.
     *
     * @param predicate The test to execute on all elements of the map.
     * @return True if every element passes the test. False if any element fails.
     * @see #some(BiPredicate)
     * @since 1.0
     */
    public boolean every(BiPredicate<? super K, ? super V> predicate) {
        for (K key : keySet()) {
            if (!predicate.test(key, get(key)))
                return false;
        }
        return true;
    }

    /**
     * Checks if some element in the map fits the condition.
     *
     * @param predicate The test to execute on all elements of the map.
     * @return True if any element passes the test. False if all elements fail.
     * @see #every(BiPredicate)
     * @since 1.0
     */
    public boolean some(BiPredicate<? super K, ? super V> predicate) {
        for (K key : keySet()) {
            if (predicate.test(key, get(key)))
                return true;
        }
        return false;
    }

    /**
     * @param <R>      The type of the value that the map will be reduced to.
     * @param identity The starting value of the reducer.
     * @param reducer  The function to apply to each element in the map. See {@link ReducerFunction#apply(Object,
     *                 Object, Object)}, which has parameters for the accumulated value at this point, the current key
     *                 and the current value.
     * @return a single value of type R that represents this map reduced to a single value.
     * @see ReducerFunction#apply(Object, Object, Object)
     * @see Stream#reduce(Object, BiFunction, BinaryOperator)
     * @since 1.0
     */
    public <R> R reduce(R identity, ReducerFunction<R, K, V> reducer) {
        for (K key : this.keySet()) {
            identity = reducer.apply(identity, key, this.get(key));
        }
        return identity;
    }

    /**
     * Returns this map, but any key in this map that does not exist in the map
     * <i>other</i> is removed.
     *
     * @param other the map to compare this map to.
     * @return a map containing all this maps values, except for keys where that key does not exist in the other map.
     * @see #intersectElements(Map)
     * @see #difference(Map)
     * @since 1.0
     */
    public FmMap<K, V> intersect(Map<K, V> other) {
        return filter((k, v) -> other.containsKey(k));
    }

    /**
     * Returns a map containing key value pairs in which only key value pairs that occurred in both maps remain.
     * <p>
     * Equality is tested using the {@link #equals(Object)} method. Both the key from this map and the other map, as
     * well as the value from this map and the other map, must be true according to the {@link #equals(Object)} method.
     * <p>
     * Objects in the return map originate from this map.
     *
     * @param other the map to compare this map to.
     * @return a map containing all this maps values, except for keys where that key does not exist in the other map.
     * @see #intersect(Map)
     * @see #difference(Map)
     * @since 1.0
     */
    public FmMap<K, V> intersectElements(Map<K, V> other) {
        return filter((k, v) -> other.containsKey(k) && other.get(k).equals(v));
    }

    /**
     * Returns a map containing key-value pairs where the key only occurred in one map - either this one or the one
     * passed into the other parameter.
     *
     * @param other the map to compare this map to.
     * @return a map containing key-value pairs only where the key occurred in at most one of this map and the other
     * map.
     * @see #intersectElements(Map)
     * @see #intersect(Map)
     * @since 1.0
     */
    public FmMap<K, V> difference(Map<K, V> other) {
        var map = filter((k, v) -> !other.containsKey(k));
        other.forEach((k, v) -> {
            if (!containsKey(k))
                map.put(k, v);
        });
        return map;
    }

    /**
     * Returns a list of all values in this map.
     *
     * @return a list of all values in this map.
     * @see FmMap#keyList()
     * @since 1.0
     */
    public List<V> valueList() {
        return new ArrayList<>(super.values());
    }

    /**
     * Returns a list of all keys in this map.
     *
     * @return a list of all keys in this map.
     * @see FmMap#valueList()
     * @since 1.0
     */
    public List<K> keyList() {
        return new ArrayList<>(keySet());
    }

    /**
     * Identical behaviour to {@link Map#put(Object, Object)}, except will return the map, allowing for chained calls.
     *
     * @param key   the key to set
     * @param value the value to set for the key
     * @return this
     * @see Map#put(Object, Object)
     * @since 1.0
     */
    public FmMap<K, V> set(K key, V value) {
        this.put(key, value);
        return this;
    }

    /**
     * Functional interface used for the {@link FmMap#reduce(Object, ReducerFunction)} method. Supplies the current
     * accumulated value (i.e. what the method has been reduced to so far), and then the value for the key and value in
     * the current iteration of the map.
     *
     * @author Fraser McCallum
     * @see FmMap#reduce(Object, ReducerFunction)
     * @since 1.0
     */
    @FunctionalInterface
    public interface ReducerFunction<R, K, V> {
        /**
         * Executes the supplied function.
         *
         * @param accumulator  The accumulated value.
         * @param currentKey   The value of the current key.
         * @param currentValue The value of the current value.
         * @return the new accumulated value after this iteration.
         */
        R apply(R accumulator, K currentKey, V currentValue);
    }
}