package data_structures.implementation;

import data_structures.Sorted;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.*;

public class FineGrainedTree<T extends Comparable<T>> implements Sorted<T> {
	class Node {
		T data = null;
		Node left = null, right = null;
		Lock lock = new ReentrantLock();
		public Node (T t) {
			data = t;
		}
		public void lock() {
			lock.lock();
		}
		public void unlock() {
			lock.unlock();
		}
	}

	// Add sentinel node to start for locking purpose
	private Node head = new Node(null);

	public void add(T t) {
		head.lock();
		Node prev = head;
		try {
			// Add first element in the tree, notice that tree root is head.left
			if (head.left == null) {
				head.left = new Node(t);
				return;
			}

			// Find correct position of node to be added in tree
			// Start from the head of the tree
			// Go to left subtree if node is <= than node in current node,
			// otherwise go to right subtree, until you find empty subtree
			// Add node when subtree is empty
			Node curr = head.left;
			curr.lock();
			try {
				while(true) {
					Node next;
					if (curr.data.compareTo(t) >= 0) {
						if (curr.left == null) {
							curr.left = new Node(t);
							return;
						}
						next = curr.left;
					} else {
						if (curr.right == null) {
							curr.right = new Node(t);
							return;
						}
						next = curr.right;
					}
					prev.unlock();
					prev = curr;
					curr = next;
					curr.lock();
				}
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
			Node curr = head.left;
			curr.lock();
			try {
				while(true) {
					if (curr.data.compareTo(t) != 0) {
						// Traverse tree to find the node to be removed
						prev.unlock();
						prev = curr;
						curr = curr.data.compareTo(t) >= 0 ? curr.left : curr.right;
						if (curr == null) {
							System.err.println(t + " not found");
							return;
						}
						curr.lock();
					} else {
						// Found the node, now remove it from tree
						if (curr.right == null) {
							// Case 1: Delete node which has maximum one left child
							// Set corresponding link of prev to subtree of the child to be removed
							if (prev.data == null || prev.left == curr) {
								prev.left = curr.left;
							} else {
								prev.right = curr.left;
							}
						} else if (curr.left == null) {
							// Case 2: Delete node which has only one right child
							// Set corresponding link of prev to subtree of the child to be removed
							if (prev.data == null || prev.left == curr) {
								prev.left = curr.right;
							} else {
								prev.right = curr.right;
							}
						} else {
							// Case 3: Node has two children
							// min: Find minimum node in right subtree
							// Replace the node to be removed with min and remove min from tree
							Node prev_min = curr, curr_min = curr.right;
							curr_min.lock();
							try {
								while (curr_min.left != null) {
									prev_min = curr_min;
									curr_min = curr_min.left;
									curr_min.lock();
									prev_min.unlock();
								}
								// Notice min can have maximum one right child
								// Set corresponding link of prev to subtree of the child to be removed
								if (prev_min.data == null || prev_min.left == curr_min) {
									prev_min.left = curr_min.right;
								} else {
									prev_min.right = curr_min.right;
								}
								curr.data = curr_min.data;
							} finally {
								curr_min.unlock();
							}
						}
						return;
					}
				}
			} finally {
				curr.unlock();
			}
		} finally {
			prev.unlock();
		}
	}
	
	public void inorder_traversal (Node curr, StringBuilder s) {
		if (curr.left != null) {
			inorder_traversal(curr.left, s);
			s.append(", ");
		}
		s.append(curr.data);
		if (curr.right != null) {
			s.append(", ");
			inorder_traversal(curr.right, s);
		}
	}

	public String toString() {
		StringBuilder strList = new StringBuilder("[");
		// Do in order traversal of tree to print it in textual format
		if (head.left != null) {
			inorder_traversal(head.left, strList);
		}
		strList.append("]");
		return strList.toString();
	}
}
