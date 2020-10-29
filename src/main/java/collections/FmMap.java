/*
 * Copyright 2020 Fraser McCallum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package collections;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public interface FmMap<K, V> extends Map<K, V>, Cloneable, Serializable {
    V random();

    K randomKey();

    <R> List<R> map(BiFunction<? super K, ? super V, R> action);

    FmMap<K, V> filter(BiPredicate<? super K, ? super V> predicate);

    FmMap<K, V> sweep(BiPredicate<? super K, ? super V> predicate);

    boolean every(BiPredicate<? super K, ? super V> predicate);

    boolean some(BiPredicate<? super K, ? super V> predicate);

    <R> R reduce(R identity, ReducerFunction<R, K, V> reducer);

    FmMap<K, V> intersect(Map<K, V> other);

    FmMap<K, V> intersectElements(Map<K, V> other);

    FmMap<K, V> difference(Map<K, V> other);

    List<V> valueList();

    List<K> keyList();

    FmMap<K, V> set(K key, V value);

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
    public interface ReducerFunction<R, K2, V2> {
        /**
         * Executes the supplied function.
         *
         * @param accumulator  The accumulated value.
         * @param currentKey   The value of the current key.
         * @param currentValue The value of the current value.
         * @return the new accumulated value after this iteration.
         */
        R apply(R accumulator, K2 currentKey, V2 currentValue);
    }
}
