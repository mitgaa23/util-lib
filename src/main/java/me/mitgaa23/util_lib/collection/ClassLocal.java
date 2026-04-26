package me.mitgaa23.util_lib.collection;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

public class ClassLocal<T> {
	private final Map<Class<?>, T> map = new WeakHashMap<>();
	private final Function<Class<?>, T> generator;

	public ClassLocal(Function<Class<?>, T> generator) {
		this.generator = generator;
	}

	public void remove(Class<?> key) {
		map.remove(key);
	}

	public T get(Class<?> key) {
		return map.computeIfAbsent(key, generator);
	}

	public T set(Class<?> key, T value) {
		return map.put(key, value);
	}

	public void reset(Class<?> key) {
		map.remove(key);
	}
}
