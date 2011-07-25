package org.jclouds.virtualbox.experiment;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.virtualbox_4_0.AccessMode;
import org.virtualbox_4_0.DeviceType;
import org.virtualbox_4_0.IMachine;
import org.virtualbox_4_0.IMedium;
import org.virtualbox_4_0.IProgress;
import org.virtualbox_4_0.ISession;
import org.virtualbox_4_0.LockType;
import org.virtualbox_4_0.MachineState;
import org.virtualbox_4_0.MediumType;
import org.virtualbox_4_0.SessionState;
import org.virtualbox_4_0.StorageBus;
import org.virtualbox_4_0.VirtualBoxManager;
import org.virtualbox_4_0.jaxws.MediumState;
import org.virtualbox_4_0.jaxws.MediumVariant;

import com.google.common.base.Predicate;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "live", testName = "virtualbox.VirtualboxLiveTest")
public class VirtualboxLiveTest {

	protected String provider = "virtualbox";
	protected String identity;
	protected String credential;
	protected String endpoint;
	protected String apiversion;
	protected String vmName;

	VirtualBoxManager manager = VirtualBoxManager.createInstance(null);

	protected Injector injector;
	protected Predicate<IPSocket> socketTester;
	protected SshClient.Factory sshFactory;

	protected String osUsername;
	protected String osPassword;
	protected String controller;
	protected String diskFormat;

	protected String settingsFile; // Fully qualified path where the settings file should be created, or NULL for a default 
	// folder and file based on the name argument (see composeMachineFilename()).

	protected String osTypeId; // Guest OS Type ID.
	protected String vmId; // Machine UUID (optional).
	protected boolean forceOverwrite; // If true, an existing machine settings file will be overwritten.

	protected String workingDir;
	protected String originalDiskPath;
	protected String clonedDiskPath;

	// Create disk If the @a format attribute is empty or null then the default
	// storage format specified by ISystemProperties#defaultHardDiskFormat
	String format = "vdi";

	protected int numberOfVirtualMachine ;

	@BeforeClass
	protected void setupConfigurationProperties() {
		//VBOX
		settingsFile = null; // Fully qualified path where the settings file should be created, or NULL for a default 
							// folder and file based on the name argument (see composeMachineFilename()).
		osTypeId = System.getProperty("test." + provider + ".osTypeId", ""); // Guest OS Type ID.
		vmId = System.getProperty("test." + provider + ".vmId", null); // Machine UUID (optional).
		forceOverwrite = true; // If true, an existing machine settings file will be overwritten.

		// OS specific information
		vmName = checkNotNull(System.getProperty("test." + provider + ".vmname"));
		osUsername = System.getProperty("test." + provider + ".osusername", "root");
		osPassword = System.getProperty("test." + provider + ".ospassword", "toortoor");
		controller = System.getProperty("test." + provider + ".controller", "IDE Controller");
		diskFormat = System.getProperty("test." + provider + ".diskformat", "");

		workingDir = checkNotNull(
				System.getProperty("test." + provider + ".workingDir"));
		
		originalDiskPath = workingDir
				+ File.separator
				+ checkNotNull(System.getProperty("test." + provider
						+ ".originalDisk"));
		
		numberOfVirtualMachine = Integer.parseInt(checkNotNull(System.getProperty("test." + provider + ".numberOfVirtualMachine")));
	}
	
	
	@BeforeClass
	protected void setupCredentials() throws RemoteException,
			MalformedURLException {
		identity = System.getProperty("test." + provider + ".identity", "administrator");
		credential = System.getProperty("test." + provider + ".credential", "12345");
		endpoint = System.getProperty("test." + provider + ".endpoint", "http://localhost:18083/");
		apiversion = System.getProperty("test." + provider + ".apiversion");
		
		injector = Guice.createInjector(new JschSshClientModule(),
				new Log4JLoggingModule());
		sshFactory = injector.getInstance(SshClient.Factory.class);
		socketTester = new RetryablePredicate<IPSocket>(
				new InetSocketAddressConnect(), 180, 1, TimeUnit.SECONDS);
		injector.injectMembers(socketTester);
	}
	
	@BeforeMethod
	protected void setupManager() throws RemoteException, MalformedURLException {
		manager.connect(endpoint, identity, credential);
	}

	@AfterMethod
	protected void disconnectAndClenaupManager() throws RemoteException,
			MalformedURLException {
		manager.disconnect();
		manager.cleanup();
	}

	@Test
	public void testStartVirtualMachines() {
		IMedium clonedHd = cloneDisk(MediumType.MultiAttach);
		for (int i = 1; i < numberOfVirtualMachine + 1; i++) {
			createVirtualMachine(i, clonedHd);
		}
	}

	private void createVirtualMachine(int i, IMedium clonedHd) {
		
		String instanceName = vmName + "_" + i;

		IMachine newVM = manager.getVBox().createMachine(settingsFile, instanceName, osTypeId, vmId, forceOverwrite);		
		manager.getVBox().registerMachine(newVM);
		
		ISession session = manager.getSessionObject();
		IMachine machine = manager.getVBox().findMachine(instanceName);
		machine.lockMachine(session, LockType.Write); 
		IMachine mutable = session.getMachine(); 
		
		// disk
		mutable.addStorageController(controller, StorageBus.IDE);
		mutable.attachDevice(controller, 0, 0, DeviceType.HardDisk, clonedHd);
		
		// network
		String hostInterface = null;
	    String command = "vboxmanage list bridgedifs";
	    try {
			Process child = Runtime.getRuntime().exec(command);
				        BufferedReader bufferedReader = new BufferedReader(
	                new InputStreamReader(child.getInputStream()));
				        String line = "";
				        boolean found = false;

			while ( (line = bufferedReader.readLine()) != null && !found){

	        if(line.split(":")[0].contains("Name") ){
	        	hostInterface = line.split(":")[1];
	        }
	        if( line.split(":")[0].contains("Status") && line.split(":")[1].contains("Up") ){
	        	System.out.println("bridge: " + hostInterface.trim());
	        	found = true;
	        }
	        	
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mutable.getNetworkAdapter(new Long(0)).attachToBridgedInterface();
		mutable.getNetworkAdapter(new Long(0)).setHostInterface(hostInterface.trim());		
		mutable.getNetworkAdapter(new Long(0)).setEnabled(true);

		mutable.saveSettings(); 
		session.unlockMachine();
	}


	/**
	 * @param instanceName
	 * @return
	 */
	private IMedium cloneDisk(MediumType mediumType) {

		String clonedDisk = System.getProperty("test." + provider + ".clonedDisk");
		String instanceClonedDisk = clonedDisk.split("\\.")[0] + "." +clonedDisk.split("\\.")[1];
		clonedDiskPath = workingDir + File.separator + instanceClonedDisk;

		// use template disk in multiattach mode 
		IMedium clonedHd = manager.getVBox().openMedium(originalDiskPath, DeviceType.HardDisk, AccessMode.ReadOnly);
		
		System.out.println("cloned HD state: " + clonedHd.getState());
		/* 
		An image in multiattach mode can be attached to more than one virtual machine at the same time,  
		even if these machines are running simultaneously. For each virtual machine to which such an image is attached, a differencing image 
		is created. As a result, data that is written to such a virtual disk by one machine is not 
		seen by the other machines to which the image is attached; each machine creates its own write history of the multiattach image.
		 */
		while(clonedHd.getState().equals(MediumState.NOT_CREATED)) {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		clonedHd.setType(mediumType);
		return clonedHd;
	}

	private void launchVMProcess(IMachine machine, ISession session) {
		IProgress prog = machine.launchVMProcess(session, "gui", "");
		prog.waitForCompletion(-1);
		session.unlockMachine();
	}
	
	protected void checkSSH(IPSocket socket) {
		socketTester.apply(socket);
		SshClient client = sshFactory.create(socket, new Credentials(
				osUsername, osPassword));
		try {
			client.connect();
			ExecResponse exec = client.exec("touch /tmp/hello_" + System.currentTimeMillis());
			exec = client.exec("echo hello");
			System.out.println(exec);
			assertEquals(exec.getOutput().trim(), "hello");
		} finally {
			if (client != null)
				client.disconnect();
		}
	}

	@Test(dependsOnMethods = "testStartVirtualMachines")
	public void testSshLogin() {
		String ipAddress = null;
		for (int i = 1; i < numberOfVirtualMachine +1; i++) {
			String instanceName = vmName + "_" + i;
			IMachine machine = manager.getVBox().findMachine(instanceName);
			
			System.out.println("\nLaunch VM named " + machine.getName() + " ...");
			launchVMProcess(machine, manager.getSessionObject());

			while(ipAddress==null || ipAddress.equals("")){
				try {
					ipAddress = machine.getGuestPropertyValue("/VirtualBox/GuestInfo/Net/0/V4/IP");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("VM " + instanceName + " started with IP " + ipAddress);
			IPSocket socket = new IPSocket(ipAddress, 22);
			
			System.out.println("Check SSH for " + instanceName + " ...");
			checkSSH(socket);
		}		
	}

	@Test(dependsOnMethods = "testSshLogin")
	public void testStopVirtualMachine() {
		for (int i = 1; i < numberOfVirtualMachine + 1; i++) {
			String instanceName = vmName + "_" + i;
			IMachine machine = manager.getVBox().findMachine(instanceName);
	
			try {
				ISession machineSession = manager.openMachineSession(machine);
				IProgress progress = machineSession.getConsole().powerDown();
				progress.waitForCompletion(-1);
				machineSession.unlockMachine();
				
				
				while(!machine.getSessionState().equals(SessionState.Unlocked)){
					try {
						  System.out.println("waiting for unlocking session - session state: " + machine.getSessionState());
						  Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				assertEquals(machine.getState(), MachineState.PoweredOff);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}