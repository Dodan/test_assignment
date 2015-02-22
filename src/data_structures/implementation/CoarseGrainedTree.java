package data_structures.implementation;

import data_structures.Sorted;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.*;

public class CoarseGrainedTree<T extends Comparable<T>> implements Sorted<T> {
	private class Node {
		T data;
		Node left;
		Node right;
		Node(T data, Node left, Node right) {
			this.data = data;
			this.left = left;
			this.right = right;
		}
	}

	private Node root = null;
	private Lock lock = new ReentrantLock();
	
	public void add(T t) {
		lock.lock();
		try {
			// Add first item in the tree
			if (root == null) {
				root = new Node(t, null, null);
			} else {
				Node curr = root;
				// Find correct position of item to be added in tree
				// Start from the root of the tree
				// Go to left subtree if item is <= than item in current node,
				// otherwise go to right subtree, until you find empty subtree
				// Add item when subtree is empty
				while(true) {
					if (t.compareTo(curr.data) <= 0) {
						if (curr.left == null) {
							curr.left = new Node(t, null, null);
							break;
						} else {
							curr = curr.left;
						}
					} else {
						if (curr.right == null) {
							curr.right = new Node(t, null, null);
							break;
						} else {
							curr = curr.right;
						}
					}
				}
			}
		} finally {
			lock.unlock();
		}
	}

	public void remove(T t) {
		lock.lock();
		try {
			int branch = 0, diff;
			Node parent = null, curr = root;
			// Find the item to be removed and its parent
			while(curr != null) {
				diff = t.compareTo(curr.data);
				if (diff < 0) {
					branch = 1;
					parent = curr;
					curr = curr.left;
				} else if (diff > 0) {
					branch = 2;
					parent = curr;
					curr = curr.right;
				} else {
					break;
				}
			}

			if (curr == null) {
				System.err.println(t + " not found");
			} else {
				if (curr.left == null && curr.right == null) {
					// Case 1: Delete leaf node
					// Set corresponding link of parent to null
					if (branch == 0) {
						root = null;
					} else if (branch == 1) {
						parent.left = null;
					} else {
						parent.right = null;
					}
				} else if (curr.left != null && curr.right == null) {
					// Case 2: Delete node which has only left child
					// Set corresponding link of parent to subtree of the child to be removed
					if (branch == 0) {
						root = root.left;
					} else if (branch == 1) {
						parent.left = curr.left;
					} else {
						parent.right = curr.left;
					}
				} else if (curr.left == null && curr.right != null) {
					// Case 3: Delete node which has only right child
					// Set corresponding link of parent to subtree of the child to be removed
					if (branch == 0) {
						root = root.right;
					} else if (branch == 1) {
						parent.left = curr.right;
					} else {
						parent.right = curr.right;
					}
				} else {
					// Case 4: Node has two children
					// min: Find minimum node in right subtree
					// Replace the node to be removed with min and remove min from tree
					Node parent_min = curr, min = curr.right;
					int branch_min = 2;
					while (min.left != null) {
						parent_min = min;
						min = min.left;
						branch_min = 1;
					}
					curr.data = min.data;

					// Notice min will never have two children, it can have maximum one right child
					if (min.right == null) {
						// Case 1: Delete min which is leaf node
						// Set corresponding link of parent to null
						if (branch_min == 1) {
							parent_min.left = null;
						} else {
							parent_min.right = null;
						}
					} else {
						// Case 3: Delete min which has only right child
						// Set corresponding link of parent to subtree of the child to be removed
						if (branch_min == 1) {
							parent_min.left = min.right;
						} else {
							parent_min.right = min.right;
						}
					}
				}
			}
		} finally {
			lock.unlock();
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
		lock.lock();
		StringBuilder strList = new StringBuilder("[");
		try {
			// Do in order traversal of tree to print it in textual format
			if (root != null) {
				inorder_traversal(root, strList);
			}
			strList.append("]");
		} finally {
			lock.unlock();
		}
		return strList.toString();
	}
}
