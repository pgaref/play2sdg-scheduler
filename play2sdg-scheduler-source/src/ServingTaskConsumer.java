import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import main.java.uk.ac.imperial.lsds.utils.SystemStats;


public class ServingTaskConsumer implements Runnable{

	private static SystemStats s = new SystemStats();
	
	private static String filename = "serving-stats.dat";
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
	
	ArrayList<Long> latencyPercentile = new ArrayList<Long>();
	
	@Override
	public void run() {
		
		try{
			fw = new FileWriter(filename,true); //the true will append the new data
		}
		catch(IOException ioe){
		    System.err.println("IOException: " + ioe.getMessage());
		}
		
		DecimalFormat df = new DecimalFormat("###.00");
		QueueObject got = null;
		while(true){
			try {
				 got = SimpleScheduler.ServeQueue.take();
				 ServingTaskConsumer.pi_digits(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			events++;
			long currlatency =( System.currentTimeMillis() - got.startTime );
			latencyPercentile.add( currlatency );
			
			if(System.currentTimeMillis() - refTime > reportInterval) {
				
				Collections.sort(latencyPercentile);
				
				if(SimpleScheduler.policyEnabled){
					if (latencyPercentile
							.get(latencyPercentile.size() * 99 / 100) > 100)
						SimpleScheduler.violatedSLO = true;
					else if (SimpleScheduler.violatedSLO
							&& (latencyPercentile
									.get(latencyPercentile.size() * 99 / 100) < 20))
						SimpleScheduler.violatedSLO = false;
				}
				
				s.measureAll();				
				System.out.println("Count: "+ count +" Cpu Util : "+ df.format(s.getCpuUtilisation()*100)  + " Used Memory: "  + (100 - ((double)s.getMemAvailable()/(double)s.getMemTotal())*100));
				
				try {
					fw.write(count++ + " " + got.taskSent + " "  + latencyPercentile.get(latencyPercentile.size()*90/100) + " "+ latencyPercentile.get(latencyPercentile.size()*99/100) + " "+ df.format(s.getCpuUtilisation()*100)  + " "+ (100 - ((double)s.getMemAvailable()/(double)s.getMemTotal())*100)+  "\n");// appends the string
															// to the file
					fw.flush();
				} catch (IOException ioe) {
					System.err.println("IOException: " + ioe.getMessage());
				}
				System.out.println("S: Q size:"+ SimpleScheduler.ServeQueue.remainingCapacity() + " e/s: "+events + " 90th latency: "+ latencyPercentile.get(latencyPercentile.size()*90/100) + " 99th latency: "+ latencyPercentile.get(latencyPercentile.size()*99/100) +" ms  \t\t " +got.taskSent/1000 + "k sin");
				
				events = 0;
				latencyPercentile = new ArrayList<Long>();
				refTime = System.currentTimeMillis();
			}
		}
		
	}

}
