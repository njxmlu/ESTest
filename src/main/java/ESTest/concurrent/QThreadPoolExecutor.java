package ESTest.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程池
 * @author mming.li
 * */
public class QThreadPoolExecutor extends ThreadPoolExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(QThreadPoolExecutor.class);
	
    // 记录每个线程执行任务开始时间
    private ThreadLocal<Long> start = new ThreadLocal<Long>();
    
    // 记录所有任务完成使用的时间
    private AtomicLong totals = new AtomicLong();
    
    // 记录线程池完成的任务数
    private AtomicInteger tasks = new AtomicInteger();
	
	public QThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                               BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	public QThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                               BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}

	public QThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                               BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}

	public QThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}
	
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        start.set(System.currentTimeMillis());
    }

    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        tasks.incrementAndGet();
        totals.addAndGet(System.currentTimeMillis() - start.get());
    }

    @Override
    protected void terminated() {
        super.terminated();
        int task = tasks.get();
        if (task > 0) {
        	 LOGGER.info(String.format("Threadpool complete [%s] task,average cost [%s] ms", task ,totals.get() / task));
		} else {
			LOGGER.info(String.format("Threadpool receive [%s] task", task));
		}
    }

	protected void shotdown() {
		super.shutdown();
	}


	
}
