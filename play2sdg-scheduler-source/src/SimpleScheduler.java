

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SimpleScheduler {
		
	public static BlockingQueue<QueueObject> ServeQueue = new ArrayBlockingQueue<>(10000);
	public static BlockingQueue<QueueObject> AnalyticsQueue = new ArrayBlockingQueue<>(100000000);
	
	public static boolean violatedSLO = false;
	public static boolean policyEnabled = true;
	
	
	public static void main(String[] args) {
		int threads = Runtime.getRuntime().availableProcessors();
		System.out.println("Number of available processor cores: " + threads);
		AnalyticsTaskConsumer analytics = new AnalyticsTaskConsumer();
		ServingTaskConsumer serving = new ServingTaskConsumer();
	    ExecutorService pool = Executors.newFixedThreadPool(2);
	    pool.submit(serving);
	    pool.submit(analytics);
	   
	    
	    double i = 0;
	    while(true) {
	    	double radians = Math.PI/2500 * i;
	    	//System.out.format("The sine of %.1f radians is %.4f%n", radians, Math.abs( Math.sin(radians) ));
			try {
				int tosend = (int) (Math.abs( Math.sin(radians) ) * 10000);
				//System.out.println("To send serving: "+ tosend);
				for(int t = 0; t <= tosend ; t++){
					SimpleScheduler.ServeQueue.put(new QueueObject(tosend, System.currentTimeMillis()));
					
					if(!policyEnabled){
						SimpleScheduler.AnalyticsQueue.put(new QueueObject(tosend, System.currentTimeMillis()));
					}
					else{
						if(!violatedSLO){
							SimpleScheduler.AnalyticsQueue.put(new QueueObject(tosend, System.currentTimeMillis()));
						}
						else{
							//System.out.println("Serving SLO violation!");
							continue;
						}
					}
				}
				
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			i++;
		}
		
	}

}
