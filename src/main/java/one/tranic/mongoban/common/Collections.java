package one.tranic.mongoban.common;

import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Collections {
    private static boolean fastutil = false;

    static {
        try {
            Class.forName("it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap");
            fastutil = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static <K> Map<K, Integer> newIntHashMap() {
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<>() : new HashMap<>();
    }

    public static <K> Map<K, Integer> newIntHashMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<>(initialCapacity) : new HashMap<>(initialCapacity);
    }

    public static <K> Map<K, Long> newLongHashMap() {
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap<>() : new HashMap<>();
    }

    public static <K> Map<K, Long> newLongHashMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap<>(initialCapacity) : new HashMap<>(initialCapacity);
    }

    public static <K> Map<K, Float> newFloatHashMap() {
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap<>() : new HashMap<>();
    }

    public static <K> Map<K, Float> newFloatHashMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap<>(initialCapacity) : new HashMap<>(initialCapacity);
    }

    public static <K> Map<K, Double> newDoubleHashMap() {
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap<>() : new HashMap<>();
    }

    public static <K> Map<K, Double> newDoubleHashMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap<>(initialCapacity) : new HashMap<>(initialCapacity);
    }

    public static <K> Map<K, Boolean> newBooleanHashMap() {
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap<>() : new HashMap<>();
    }

    public static <K> Map<K, Boolean> newBooleanHashMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap<>(initialCapacity) : new HashMap<>(initialCapacity);
    }

    public static <K, V> Map<K, V> newHashMap() {
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>() : new HashMap<>();
    }

    public static <K, V> Map<K, V> newHashMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(initialCapacity) : new HashMap<>(initialCapacity);
    }

    public static <K, V> Map<K, V> newHashMap(@NotNull Map<K, V> map) {
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(map) : new HashMap<>(map);
    }

    public static <T> Set<T> newHashSet() {
        return fastutil ? new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<>() : new HashSet<>();
    }

    public static <T> Set<T> newHashSet(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        return fastutil ? new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<>(initialCapacity) : new HashSet<>(initialCapacity);
    }

    public static <T> Set<T> newTreeSet(Collection<? extends T> c) {
        return fastutil ? new it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet<>(c) : new TreeSet<>(c);
    }

    public static <T> Set<T> newTreeSetRB(@NotNull @Flow(sourceIsContainer = true, targetIsContainer = true) Collection<? extends T> c) {
        return fastutil ? new it.unimi.dsi.fastutil.objects.ObjectRBTreeSet<>(c) : new TreeSet<>(c);
    }

    public static <T> List<T> newArrayList() {
        return fastutil ? new it.unimi.dsi.fastutil.objects.ObjectArrayList<>() : new ArrayList<>();
    }

    public static <T> List<T> newArrayList(@Range(from = 0, to = Integer.MAX_VALUE) int size) {
        return fastutil ? new it.unimi.dsi.fastutil.objects.ObjectArrayList<>(size) : new ArrayList<>(size);
    }

    @SafeVarargs
    public static <T> List<T> newArrayList(@NotNull T... elements) {
        if (fastutil) {
            return new it.unimi.dsi.fastutil.objects.ObjectArrayList<>(elements);
        }
        List<T> list = new ArrayList<>(elements.length);
        java.util.Collections.addAll(list, elements);
        return list;
    }

    public static <K, V> void entryForEach(Map<K, V> map, final Consumer<? super Map.Entry<K, V>> consumer) {
        if (fastutil && map instanceof it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<K, V> fastMap) {
            fastMap.object2ObjectEntrySet().fastForEach(consumer);
        } else map.entrySet().forEach(consumer);
    }

    public static <K, V> boolean removeIf(Map<K, V> map, Predicate<? super Map.Entry<K, V>> filter) {
        return (fastutil && map instanceof it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<K, V> fastMap) ?
                fastMap.object2ObjectEntrySet().removeIf(filter) :
                map.entrySet().removeIf(filter);
    }
}
