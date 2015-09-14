import java.io.FileWriter;
import java.io.IOException;


public class ServingTaskConsumer implements Runnable{

	private static String filename = "latency.dat";
	private static int count = 0;
	private static FileWriter fw = null;
	private static final int SCALE = 10000;  
	private static final int ARRINIT = 2000;  
	  
	public static String pi_digits(int digits) {
		StringBuffer pi = new StringBuffer();
		int[] arr = new int[digits + 1];
		int carry = 0;

		for (int i = 0; i <= digits; ++i)
			arr[i] = ARRINIT;

		for (int i = digits; i > 0; i -= 14) {
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
		
		try{
			fw = new FileWriter(filename,true); //the true will append the new data
		}
		catch(IOException ioe){
		    System.err.println("IOException: " + ioe.getMessage());
		}
		
		QueueObject got = null;
		while(true){
			try {
				 got = SimpleScheduler.ServeQueue.take();
				 ServingTaskConsumer.pi_digits(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			events++;
			if(System.currentTimeMillis() - refTime > reportInterval) {
				long latency =(System.currentTimeMillis() - got.startTime);
				if(latency > 30 )
					SimpleScheduler.violatedSLO = true;
				if(latency < 25 )
					SimpleScheduler.violatedSLO = false;
				try {
					fw.write(count++ + " " + got.taskSent + " " + latency +"\n");// appends the string
															// to the file
					fw.flush();
				} catch (IOException ioe) {
					System.err.println("IOException: " + ioe.getMessage());
				}
				System.out.println("S: queue size:"+ SimpleScheduler.ServeQueue.remainingCapacity() + " e/s: "+events + " proc latency: "+ latency + " ms  \t\t " +got.taskSent/1000 + "k sin");
				events = 0;
				refTime = System.currentTimeMillis();
			}
		}
		
	}

}
