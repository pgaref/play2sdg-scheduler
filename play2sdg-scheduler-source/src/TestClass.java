
public class TestClass {
	
	 public static void main(String args[]) throws InterruptedException{
		 
		for(int i =0 ;i < 1000; i++){
	     double radians = Math.PI/48 * i;
	     //double radians = Math.toRadians(degrees);

	     	System.out.format("The value of radians is %.4f%n", radians);
	     	System.out.format("The sine of %.1f radians is %.4f%n", radians, Math.abs( Math.sin(radians) ));
	     	Thread.sleep(100);
	 	}

	   }
}
