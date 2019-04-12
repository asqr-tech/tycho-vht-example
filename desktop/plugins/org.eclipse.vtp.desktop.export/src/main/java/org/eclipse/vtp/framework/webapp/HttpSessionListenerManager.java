package org.eclipse.vtp.framework.webapp;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class HttpSessionListenerManager implements HttpSessionListener {
	
	private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private static final Collection<HttpSessionListener> listeners = new LinkedList<HttpSessionListener>();

	public static void addHttpSessionListener (HttpSessionListener listener) {
		Lock lock = readWriteLock.writeLock();
		lock.lock();
		try {
			listeners.add(listener);
		} finally {
			lock.unlock();
		}
	}
	
	public static void removeHttpSessionListener (HttpSessionListener listener) {
		Lock lock = readWriteLock.writeLock();
		lock.lock();
		try {
			listeners.remove(listener);
		} finally {
			lock.unlock();
		}
	}

	public void sessionCreated(HttpSessionEvent event) {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			for (HttpSessionListener listener : listeners)
				listener.sessionCreated(event);
		} finally {
			lock.unlock();
		}
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			for (HttpSessionListener listener : listeners)
				listener.sessionDestroyed(event);
		} finally {
			lock.unlock();
		}
	}

}
