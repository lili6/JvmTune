package lili.tune.concurrent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by liguofang on 2015/1/23.
 * 基于Treiber算法实现Stack:基于CAS以及AtomicReference
 * CAS的语义是“我认为V的值应该为A，如果是，那么将V的值更新为B，否则不修改并告诉V的值实际为多少”，CAS是项 乐观锁 技术
 */
public class ConcurrentStack<E> {
	AtomicReference<Node<E>> head = new AtomicReference<Node<E>>();

	public  void push(E item) {
		Node<E>  newHead = new Node<E>(item);
		Node<E>  oldHead;
		do {
			oldHead = head.get();
			newHead.next = oldHead;
		} while (!head.compareAndSet(oldHead,newHead));
	}

	public E pop(){
		Node<E> oldHead;
		Node<E> newHead;

		do {
			oldHead = head.get();
			if (oldHead == null){
				return null;
			}
			newHead = oldHead.next;
		} while (!head.compareAndSet(oldHead,newHead));
		return oldHead.item;
	}

	static class Node<E> {
		final  E item;
		Node<E> next;
		public Node(E item) {
			this.item = item;
		}
	}
}
