
public class TaskProducer implements Runnable{
	
	@Override
	public void run() {
		while(true) {
			try {
				SimpleScheduler.AnalyticsQueue.put(new QueueObject (0, System.currentTimeMillis()) );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
