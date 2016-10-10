package ESTest.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 并发服务
 * @author mming.li
 * */
public abstract class MultiThreadService {

	protected QThreadPoolExecutor executor;
	
	protected BlockingQueue<Runnable> queue;
	
	protected int capacity;
	
	public MultiThreadService(int corePoolSize, int maximumPoolSize, Long keepAliveTime, TimeUnit unit, int capacity) {
		this.capacity = capacity;
		queue = new LinkedBlockingQueue<Runnable>(capacity);
		executor = new QThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue);
	}

	public int getCapacity() {
		return capacity;
	}
	public void shutdown() {
		if (executor != null) {
			executor.shutdown();
		}
	}
}
