package data_structures.implementation;

import data_structures.Sorted;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.*;

public class CoarseGrainedList<T extends Comparable<T>> implements Sorted<T> {
	private class Node {
		T data;
		Node next;
		Node(T data, Node next) {
			this.data = data;
			this.next = next;
		}
	}

	private Node head = null;
	private Lock lock = new ReentrantLock();
	
	public void add(T t) {
		// Whole linked list is locked for inserting items
		lock.lock();
		try {
			Node prev = null, curr = head;
			// Add item at the start of linked list if it is empty or new item is smaller
			if (curr == null || t.compareTo(curr.data) < 0) {
				Node node = new Node(t, head);
				head = node;
			} else {
				// Find appropriate position of the item to be added such that
				// linked list is in non descending order
				while (curr != null && t.compareTo(curr.data) > 0) {
					prev = curr;
					curr = curr.next;
				}
				Node node = new Node(t, curr);
				prev.next = node;
			}
		} finally {
			lock.unlock();
		}
	}

	public void remove(T t) {
		lock.lock();
		try {
			Node prev = null, curr = head;
			// Find position of the item to be removed
			while (curr != null && t.compareTo(curr.data) > 0) {
				prev = curr;
				curr = curr.next;
			}
			// Check if item is present in the linked list and remove it
			if (t.compareTo(curr.data) == 0) {
				if (curr == head) {
					head = curr.next;
				} else {
					prev.next = curr.next;
				}
			} else {
				System.err.println(t + " not found");
			}
		} finally {
			lock.unlock();
		}
	}

	public String toString() {
		lock.lock();
		StringBuilder strList = new StringBuilder("[");
		try {
			Node curr = head;
			// If there is only one element in list
			if (curr != null && curr.next == null) {
				strList.append(curr.data);  
			} else {
				// Traverse whole list and append items
				while (curr != null) {
					strList.append(curr.data);
					curr = curr.next;
					if (curr != null) {
						strList.append(", ");
					}
				}
			}
			strList.append("]");
		} finally {
			lock.unlock();
		}
		return strList.toString();
	}
}
