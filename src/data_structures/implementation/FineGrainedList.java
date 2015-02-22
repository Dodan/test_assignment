package data_structures.implementation;

import data_structures.Sorted;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.*;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {
	abstract class Node {
		T data = null;
		Node next = null;
		Lock lock = new ReentrantLock();
		abstract int compareTo(T t);
		public void lock() {
                        lock.lock();
                }
                public void unlock() {
                        lock.unlock();
                }
	}

	class FirstNode extends Node {
		// This should be the first node of the linked list
		// Hence, compareTo method will always return -1, making its data smaller than everything else
		int compareTo(T t) {
			return -1;
		}
	}

	class ListNode extends Node {
		// This is just normal node, it is needed because FirstNode and LastNode
		// have null data, so they dont support compareTo method
		public ListNode(T t) {
			data = t;
		}
		int compareTo(T t) {
			return data.compareTo(t);
		}
	}

	class LastNode extends Node {
		// This should be the last node of the linked list
		// Hence, compareTo method will always return 1, making its data bigger than everything else
		int compareTo(T t) {
			return 1;
		}
	}

	private Node head = null;

	public FineGrainedList() {
		// Add sentinel nodes to start and end of the tree, for locking purpose
		head = new FirstNode();
		head.next = new LastNode();
	}

	public void add(T t) {
		head.lock();
		Node prev = head;
		try {
			Node curr = prev.next;
			curr.lock();
			try {
				// Find appropriate position of the item to be added such that
				// linked list is in non descending order
				while (curr.compareTo(t) < 0) {
					prev.unlock();
					prev = curr;
					curr = curr.next;
					curr.lock();
				}
				// Found position, now add node to the list
				Node newNode = new ListNode(t);
				newNode.next = curr;
				prev.next = newNode;
				return;
			} finally {
				curr.unlock();
			}
		} finally {
			prev.unlock();
		}
	}

	public void remove(T t) {
		head.lock();
		Node prev = head;
		try {
			Node curr = prev.next;
			curr.lock();
			try {
				// Traverse the list to find the node to be removed
				while(curr.compareTo(t) < 0) {
					prev.unlock();
					prev = curr;
					curr = curr.next;
					curr.lock();
				}
				// Found the node, now remove it
				if(curr.compareTo(t) == 0) {
					prev.next = curr.next;
					return;
				} else {
					System.err.println(t + " not found");
					return;
				}
			} finally {
                		curr.unlock();
			}
		} finally {
			prev.unlock();
		}
	}

	public String toString() {
		StringBuilder strList = new StringBuilder("[");
		Node curr = head.next;
		// If there is only one element in list
		if (curr != null && curr.next == null) {
			strList.append("]");
			return strList.toString();
		} else {
			// Traverse whole list and append items
			while (curr.data != null) {
					strList.append(curr.data);
				curr = curr.next;
				if (curr.data != null) {
					strList.append(", ");
				}
			}
		}
		strList.append("]");
		return strList.toString();
	}
}
