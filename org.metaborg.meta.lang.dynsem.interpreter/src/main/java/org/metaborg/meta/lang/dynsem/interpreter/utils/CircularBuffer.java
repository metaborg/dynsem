package org.metaborg.meta.lang.dynsem.interpreter.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CircularBuffer<T> implements Iterable<T> {

	private int numElems = 0;
	private Box current;

	public CircularBuffer(T[] elems) {
		init(elems);
	}

	private void init(T[] elems) {
		for (T elem : elems) {
			Box newBox = new Box();
			newBox.elem = elem;
			if (numElems == 0) {
				newBox.next = newBox;
				newBox.prev = newBox;
			} else {
				Box last = current.prev;
				last.next = newBox;
				newBox.prev = last;
				newBox.next = current;
				current.prev = newBox;
			}
			current = newBox;
			numElems++;
		}
	}

	public int size() {
		return numElems;
	}

	public boolean isEmpty() {
		return numElems == 0;
	}

	private class Box {
		T elem;
		Box prev;
		Box next;
	}

	@Override
	public Iterator<T> iterator() {
		return new CircularBufferIterator();
	}

	private class CircularBufferIterator implements Iterator<T> {
		private int numVisited = 0;
		private Box visiting = current;
		private Box lastAccessed = null;

		@Override
		public boolean hasNext() {
			return numElems - numVisited > 0;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			lastAccessed = visiting;
			T elem = visiting.elem;
			visiting = visiting.next;
			current = visiting;
			numVisited++;
			return elem;
		}

		@Override
		public void remove() {
			if (lastAccessed == null) {
				throw new IllegalStateException();
			}
			Box left = lastAccessed.prev;
			Box right = lastAccessed.next;
			left.next = right;
			right.prev = left;
			numElems--;
			numVisited--;
			if (current == lastAccessed) {
				current = right;
			}
			lastAccessed = null;
		}

	}

}
