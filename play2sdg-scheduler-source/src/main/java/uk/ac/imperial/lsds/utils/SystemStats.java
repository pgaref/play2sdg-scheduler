package main.java.uk.ac.imperial.lsds.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Processor;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

public class SystemStats {

	
	private SystemInfo si;
	private String osName;
	private HardwareAbstractionLayer hal;
	
	private int cpuNumber;
	private String cpuVendor;
	private int cpuCores;
	private long cpuFreq;
	
	private ArrayList<HashMap<String, Long>> ProcessorTimes;
	
	private long memAvailable;
	private long memTotal;
	
	private double systemLoad;
	private double systemLoadAverage;
	
	
	public SystemStats() {
		this.si = new SystemInfo();
		OperatingSystem os = si.getOperatingSystem();
		this.osName = os.toString();
		hal = si.getHardware();
		measureAll();
	}
	
	public void measureAll(){
			
		
		this.cpuNumber = hal.getProcessors().length;
		this.ProcessorTimes = new ArrayList<HashMap<String, Long>>(this.cpuNumber);
		
		this.memAvailable =  hal.getMemory().getAvailable();
		this.memTotal = hal.getMemory().getTotal();
		
		for (Processor cpu : hal.getProcessors()) {
			HashMap<String, Long> currMeasure = new HashMap<String, Long>();
			currMeasure.put("Load", (long) cpu.getLoad());	
			currMeasure.put("User", cpu.getProcessorCpuLoadTicks()[0]);	
			currMeasure.put("Nice", cpu.getProcessorCpuLoadTicks()[1]);
			currMeasure.put("System", cpu.getProcessorCpuLoadTicks()[2]);
			currMeasure.put("Idle", cpu.getProcessorCpuLoadTicks()[3]);
			this.ProcessorTimes.add(currMeasure);
			
			this.systemLoad = cpu.getSystemCpuLoad();
			this.systemLoadAverage  = cpu.getSystemLoadAverage();
			this.cpuVendor = cpu.getVendor();
			this.cpuFreq = cpu.getVendorFreq();
		}
		
	}
	public void printAll(){
		System.out.println(" #################### ");
		
		System.out.println("OS: "+ this.osName);
		System.out.println(this.cpuNumber + " Cores at  "+  (double)(this.cpuFreq/1000000000) + " Ghz from  " + this.cpuVendor );
		System.out.println( FormatUtil.formatBytes(this.memTotal) +" Memory in total "  + FormatUtil.formatBytes(this.memAvailable) +" Memory available" ); 
		
		System.out.println("System load: "+ this.systemLoad + " load avg "+ this.systemLoadAverage);
		System.out.println("Explicit core load: ");
		for(HashMap<String, Long> meas : this.ProcessorTimes){
			System.out.println("->"+ meas.toString());
			double total = meas.get("User") + meas.get("Idle") + meas.get("Nice") + meas.get("System");
			double usage = (((double)(total-meas.get("Idle")))/total)*100 ;
			DecimalFormat df = new DecimalFormat("###.00");
			System.out.println("Usage = " + df.format(usage) + " %");
		}
		
		System.out.println(" #################### ");

	}

	public static void main(String[] args) throws InterruptedException {
		SystemStats s = new SystemStats();
		while(true){
			s.measureAll();
			s.printAll();
			Thread.sleep(2000);
		}
	}
	

	/**
	 * @return the osName
	 */
	public String getOsName() {
		return osName;
	}

	/**
	 * @param osName the osName to set
	 */
	public void setOsName(String osName) {
		this.osName = osName;
	}

	/**
	 * @return the cpuNumber
	 */
	public int getCpuNumber() {
		return cpuNumber;
	}

	/**
	 * @param cpuNumber the cpuNumber to set
	 */
	public void setCpuNumber(int cpuNumber) {
		this.cpuNumber = cpuNumber;
	}

	/**
	 * @return the cpuVendor
	 */
	public String getCpuVendor() {
		return cpuVendor;
	}

	/**
	 * @param cpuVendor the cpuVendor to set
	 */
	public void setCpuVendor(String cpuVendor) {
		this.cpuVendor = cpuVendor;
	}

	/**
	 * @return the cpuCores
	 */
	public int getCpuCores() {
		return cpuCores;
	}

	/**
	 * @param cpuCores the cpuCores to set
	 */
	public void setCpuCores(int cpuCores) {
		this.cpuCores = cpuCores;
	}

	/**
	 * @return the cpuFreq
	 */
	public long getCpuFreq() {
		return cpuFreq;
	}

	/**
	 * @param cpuFreq the cpuFreq to set
	 */
	public void setCpuFreq(long cpuFreq) {
		this.cpuFreq = cpuFreq;
	}


	/**
	 * @return the memAvailable
	 */
	public long getMemAvailable() {
		return memAvailable;
	}

	/**
	 * @param memAvailable the memAvailable to set
	 */
	public void setMemAvailable(long memAvailable) {
		this.memAvailable = memAvailable;
	}

	/**
	 * @return the memTotal
	 */
	public long getMemTotal() {
		return memTotal;
	}

	/**
	 * @param memTotal the memTotal to set
	 */
	public void setMemTotal(long memTotal) {
		this.memTotal = memTotal;
	}

	/**
	 * @return the systemLoad
	 */
	public double getSystemLoad() {
		return systemLoad;
	}

	/**
	 * @param systemLoad the systemLoad to set
	 */
	public void setSystemLoad(double systemLoad) {
		this.systemLoad = systemLoad;
	}

	/**
	 * @return the systemLoadAverage
	 */
	public double getSystemLoadAverage() {
		return systemLoadAverage;
	}

	/**
	 * @param systemLoadAverage the systemLoadAverage to set
	 */
	public void setSystemLoadAverage(double systemLoadAverage) {
		this.systemLoadAverage = systemLoadAverage;
	}

	/**
	 * @return the processorTimes
	 */
	public ArrayList<HashMap<String, Long>> getProcessorTimes() {
		return ProcessorTimes;
	}

	/**
	 * @param processorTimes the processorTimes to set
	 */
	public void setProcessorTimes(ArrayList<HashMap<String, Long>> processorTimes) {
		ProcessorTimes = processorTimes;
	}


}