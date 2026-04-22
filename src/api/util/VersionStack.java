package api.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Function;

public class VersionStack<T> {
	protected final Deque<T> stack = new ArrayDeque<>();
	protected T current;

	public VersionStack(T startingState) {
		this.current = assertState(startingState);
	}

	private static <T> T assertState(T state) {
		return Objects.requireNonNull(state, "state must be non-null");
	}

	/**
	 * Pushes the current state onto the stack.
	 */
	public void push() {
		push(get());
	}

	/**
	 * Pushes the given state onto the stack.
	 *
	 * @param state The given state.
	 */
	public void push(T state) {
		stack.offerFirst(assertState(state));
	}

	/**
	 * @return The current state.
	 */
	public T get() {
		return current;
	}

	/**
	 * Sets the current state to the state on top of the stack.
	 *
	 * @return The old state.
	 */
	public T pop() {
		T newState = stack.pollFirst();
		T prevState = get();

		if (newState != null) {
			set(newState);
		}

		return prevState;
	}

	/**
	 * Sets the current state to the given state.
	 *
	 * @param state The given state.
	 */
	public void set(T state) {
		this.current = assertState(state);
	}

	/**
	 * Update the current value based on the given func.
	 *
	 * @param func The given func.
	 */
	public void update(Function<T, T> func) {
		this.current = func.apply(current);
	}

	/**
	 * Removes the latest state without setting current to it.
	 *
	 * @return The removed state.
	 */
	public T remove() {
		return stack.pollFirst();
	}

	/**
	 * @return The state that is on top of the stack.
	 */
	public T peek() {
		return stack.peekFirst();
	}

	/**
	 * Clears the underlying stack.
	 */
	public void clear() {
		stack.clear();
	}

	@Override
	public String toString() {
		return "MarkedState{current=" + current + "; stack=" + stack + '}';
	}
}
