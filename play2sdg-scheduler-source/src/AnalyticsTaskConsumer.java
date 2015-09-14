
public class AnalyticsTaskConsumer implements Runnable{
	
	
	
	 private static final int SCALE = 10000;  
	    private static final int ARRINIT = 2000;  
	  
	    public static String pi_digits(int digits){  
	        StringBuffer pi = new StringBuffer();  
	        int[] arr = new int[digits + 1];  
	        int carry = 0;  
	  
	        for (int i = 0; i <= digits; ++i)  
	            arr[i] = ARRINIT;  
	  
	        for (int i = digits; i > 0; i-= 14) {  
	            int sum = 0;  
	            for (int j = i; j > 0; --j) {  
	                sum = sum * j + SCALE * arr[j];  
	                arr[j] = sum % (j * 2 - 1);  
	                sum /= j * 2 - 1;  
	            }  
	  
	            pi.append(String.format("%04d", carry + sum / SCALE));  
	            carry = sum % SCALE;  
	        }  
	        return pi.toString();  
	    } 
	    

	long refTime = System.currentTimeMillis();
	int reportInterval = 1000; //ms
	int events = 0;
	
	@Override
	public void run() {
		QueueObject got = null;
		while(true){
			try {
				 got = SimpleScheduler.AnalyticsQueue.take();
				 AnalyticsTaskConsumer.pi_digits(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			events++;
			if(System.currentTimeMillis() - refTime > reportInterval) {
				System.out.println("A: queue size:"+ SimpleScheduler.AnalyticsQueue.remainingCapacity() + " e/s: "+events + " proc latency: "+ (System.currentTimeMillis() - got.startTime) + " ms");
				events = 0;
				refTime = System.currentTimeMillis();
			}
		}
		
	}

}
