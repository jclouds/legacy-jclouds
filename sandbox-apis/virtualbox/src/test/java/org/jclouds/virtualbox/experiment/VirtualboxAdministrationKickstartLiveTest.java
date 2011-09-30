/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.virtualbox.experiment;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.scriptbuilder.statements.login.DefaultConfiguration;
import org.jclouds.ssh.SshException;
import org.jclouds.virtualbox.experiment.settings.KeyboardScancodes;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.virtualbox_4_1.AccessMode;
import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IMedium;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.IStorageController;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.NATProtocol;
import org.virtualbox_4_1.NetworkAdapterType;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.StorageBus;
import org.virtualbox_4_1.VirtualBoxManager;
import org.virtualbox_4_1.jaxws.MediumVariant;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

@Test(groups = "live", testName = "virtualbox.VirtualboxAdministrationKickstartTest")
public class VirtualboxAdministrationKickstartLiveTest {

	protected String provider = "virtualbox";
	protected String identity;
	protected String credential;
	protected URI endpoint;
	protected String apiVersion;
	protected String vmName;

	VirtualBoxManager manager = VirtualBoxManager.createInstance("");

	protected Predicate<IPSocket> socketTester;

	protected String settingsFile; // Fully qualified path where the settings
	protected String osTypeId; // Guest OS Type ID.
	protected String vmId; // Machine UUID (optional).
	protected boolean forceOverwrite;
	protected String diskFormat;

	protected String workingDir;
	protected String adminDisk;

	protected String guestAdditionsDvd;
	private URI gaIsoUrl;

	private String gaIsoName;
	private URI distroIsoUrl;
	private String distroIsoName;
	private String controllerIDE;
	private String controllerSATA;
	private String keyboardSequence;
	private String preseedUrl;

	private ComputeServiceContext context;
	private String hostId = "host";
	private String guestId = "guest";
	private String majorVersion;
	private String minorVersion;
	private URI vboxDmg;
	private String vboxVersionName;
	private String snapshotDescription;

	protected void setupCredentials() {
		identity = System.getProperty("test." + provider + ".identity",
				"administrator");
		credential = System.getProperty("test." + provider + ".credential",
				"12345");
		endpoint = URI.create(System.getProperty("test." + provider
				+ ".endpoint", "http://localhost:18083/"));
		apiVersion = System.getProperty("test." + provider + ".apiversion",
				"4.1.2r73507");
		majorVersion = Iterables.get(Splitter.on('r').split(apiVersion), 0);
		minorVersion = Iterables.get(Splitter.on('r').split(apiVersion), 1);
	}

	protected Logger logger() {
		return context.utils().loggerFactory().getLogger("jclouds.compute");
	}

	protected void setupConfigurationProperties() {

		controllerIDE = System.getProperty("test." + provider
				+ ".controllerIde", "IDE Controller");
		controllerSATA = System.getProperty("test." + provider
				+ ".controllerSata", "SATA Controller");
		diskFormat = System.getProperty("test." + provider + ".diskformat", "");

		// VBOX
		settingsFile = null;
		osTypeId = System.getProperty("test." + provider + ".osTypeId", "");
		vmId = System.getProperty("test." + provider + ".vmId", null);
		forceOverwrite = true;
		vmName = System.getProperty("test." + provider + ".vmname",
				"jclouds-virtualbox-kickstart-admin");

		workingDir = System.getProperty("user.home")
				+ File.separator
				+ System.getProperty("test." + provider + ".workingDir",
						"jclouds-virtualbox-test");
		if (new File(workingDir).mkdir());
		gaIsoName = System.getProperty("test." + provider + ".gaIsoName",
				"VBoxGuestAdditions_" + majorVersion + ".iso");
		gaIsoUrl = URI.create(System.getProperty("test." + provider
				+ ".gaIsoUrl", "http://download.virtualbox.org/virtualbox/"
						+ majorVersion + "/" + gaIsoName));

		distroIsoName = System.getProperty("test." + provider
				+ ".distroIsoName", "ubuntu-11.04-server-i386.iso");
		distroIsoUrl = URI
				.create(System
						.getProperty("test." + provider + ".distroIsoUrl",
								"http://releases.ubuntu.com/11.04/ubuntu-11.04-server-i386.iso"));
		vboxDmg = URI.create(System.getProperty("test." + provider + ".vboxDmg","http://download.virtualbox.org/virtualbox/4.1.2/VirtualBox-4.1.2-73507-OSX.dmg"));
		vboxVersionName = System.getProperty("test" + provider + ".vboxVersionName", "VirtualBox-4.1.2-73507-OSX.dmg");

		adminDisk = workingDir
				+ File.separator
				+ System.getProperty("test." + provider + ".adminDisk",
						"admin.vdi");
		guestAdditionsDvd = workingDir
				+ File.separator
				+ System.getProperty("test." + provider + ".guestAdditionsDvd",
						"VBoxGuestAdditions_" + majorVersion + ".iso");

		preseedUrl = System.getProperty("test." + provider + ".preseedurl",
				"http://dl.dropbox.com/u/693111/preseed.cfg");

		snapshotDescription = System.getProperty("test." + provider + "snapshotdescription", "jclouds-virtualbox-snaphot");

		keyboardSequence = System
				.getProperty(
						"test." + provider + ".keyboardSequence",
						"<Esc><Esc><Enter> "
								+ "/install/vmlinuz noapic preseed/url=http://10.0.2.2:8080/src/test/resources/preseed.cfg "
								+ "debian-installer=en_US auto locale=en_US kbd-chooser/method=us "
								+ "hostname="
								+ vmName
								+ " "
								+ "fb=false debconf/frontend=noninteractive "
								+ "keyboard-configuration/layout=USA keyboard-configuration/variant=USA console-setup/ask_detect=false "
								+ "initrd=/install/initrd.gz -- <Enter>");


	}

	@BeforeGroups(groups = "live")
	protected void setupClient() throws Exception {
		context = TestUtils.computeServiceForLocalhostAndGuest();
		socketTester = new RetryablePredicate<IPSocket>(
				new InetSocketAddressConnect(), 130, 10, TimeUnit.SECONDS);
		setupCredentials();
		setupConfigurationProperties();
		downloadFileUnlessPresent(distroIsoUrl, workingDir, distroIsoName);
		downloadFileUnlessPresent(gaIsoUrl, workingDir, gaIsoName);

		installVbox();
		checkVboxVersionExpected();
		if (!new InetSocketAddressConnect().apply(new IPSocket(endpoint
				.getHost(), endpoint.getPort())))
			startupVboxWebServer();
		configureJettyServer();
	}

	private void configureJettyServer() throws Exception {
		Server server = new Server(8080);

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "index.html" });

		resource_handler.setResourceBase(".");
		logger().info("serving " + resource_handler.getBaseResource());

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resource_handler,
				new DefaultHandler() });
		server.setHandler(handlers);

		server.start();
	}

	void installVbox() throws Exception {
		if (runScriptOnNode(hostId, "VBoxManage --version", runAsRoot(false).wrapInInitScript(false)).getExitCode() != 0) {
			logger().debug("installing virtualbox");
			if (isOSX(hostId)) {
				downloadFileUnlessPresent(vboxDmg, workingDir, vboxVersionName);
				runScriptOnNode(hostId, "hdiutil attach " + workingDir + "/" + vboxVersionName);
				runScriptOnNode(hostId, "installer -pkg /Volumes/VirtualBox/VirtualBox.mpkg -target /Volumes/Macintosh\\ HD");
			} else {
				// TODO other platforms
				runScriptOnNode(hostId, "cat > /etc/apt/sources.list.d/TODO");
				runScriptOnNode(
						hostId,
						"wget -q http://download.virtualbox.org/virtualbox/debian/oracle_vbox.asc -O- | apt-key add -");
				runScriptOnNode(hostId, "apt-get update");
				runScriptOnNode(hostId, "apt-get --yes install virtualbox-4.1");
			}
		}
	}

	void checkVboxVersionExpected() throws IOException, InterruptedException {
		logger().debug("checking virtualbox version");
		assertEquals(runScriptOnNode(hostId, "VBoxManage -version").getOutput()
				.trim(), apiVersion);
	}

	/**
	 * 
	 * @param command
	 *            absolute path to command. For ubuntu 10.04:
	 *            /usr/bin/vboxwebsrv
	 * @throws IOException
	 * @throws InterruptedException
	 */
	void startupVboxWebServer() {
		logger().debug("disabling password access");
		runScriptOnNode(hostId, "VBoxManage setproperty websrvauthlibrary null", runAsRoot(false).wrapInInitScript(false));
		logger().debug("starting vboxwebsrv");
		String vboxwebsrv = "vboxwebsrv -t 10000 -v -b";
		if (isOSX(hostId))
			vboxwebsrv = "cd /Applications/VirtualBox.app/Contents/MacOS/ && "
					+ vboxwebsrv;

		runScriptOnNode(
				hostId,
				vboxwebsrv,
				runAsRoot(false).wrapInInitScript(false)
				.blockOnPort(endpoint.getPort(), 10)
				.blockOnComplete(false)
				.nameTask("vboxwebsrv"));
	}

	protected boolean isOSX(String id) {
		return context.getComputeService().getNodeMetadata(hostId)
				.getOperatingSystem().getDescription().equals("Mac OS X");
	}

	protected boolean isUbuntu(String id) {
		return context.getComputeService().getNodeMetadata(id)
				.getOperatingSystem().getDescription().contains("ubuntu");
	}

	@BeforeMethod
	protected void setupManager() {
		manager.connect(endpoint.toASCIIString(), identity, credential);
	}

	@AfterMethod
	protected void disconnectAndClenaupManager() throws RemoteException,
	MalformedURLException {
		manager.disconnect();
		manager.cleanup();
	}

	@Test
	public void testCreateVirtualMachine() {
		IMachine newVM = manager.getVBox().createMachine(settingsFile, vmName,
				osTypeId, vmId, forceOverwrite);
		manager.getVBox().registerMachine(newVM);
		assertNotNull(newVM.getName());
	}

	@Test(dependsOnMethods = "testCreateVirtualMachine")
	public void testChangeRAM() {
		Long memorySize = new Long(1024);
		ISession session = manager.getSessionObject();
		IMachine machine = manager.getVBox().findMachine(vmName);
		machine.lockMachine(session, LockType.Write);
		IMachine mutable = session.getMachine();
		mutable.setMemorySize(memorySize);
		mutable.saveSettings();
		session.unlockMachine();
		assertEquals(manager.getVBox().findMachine(vmName).getMemorySize(),
				memorySize);
	}

	@Test(dependsOnMethods = "testChangeRAM")
	public void testCreateIdeController() {
		ISession session = manager.getSessionObject();
		IMachine machine = manager.getVBox().findMachine(vmName);
		machine.lockMachine(session, LockType.Write);
		IMachine mutable = session.getMachine();
		mutable.addStorageController(controllerIDE, StorageBus.IDE);
		mutable.saveSettings();
		session.unlockMachine();
		assertEquals(manager.getVBox().findMachine(vmName)
				.getStorageControllers().size(), 1);
	}

	@Test(dependsOnMethods = "testCreateIdeController")
	public void testAttachIsoDvd() {
		IMedium distroMedium = manager.getVBox().openMedium(
				workingDir + "/" + distroIsoName, DeviceType.DVD,
				AccessMode.ReadOnly, forceOverwrite);

		ISession session = manager.getSessionObject();
		IMachine machine = manager.getVBox().findMachine(vmName);
		machine.lockMachine(session, LockType.Write);
		IMachine mutable = session.getMachine();
		mutable.attachDevice(controllerIDE, 0, 0, DeviceType.DVD, distroMedium);
		mutable.saveSettings();
		session.unlockMachine();
		assertEquals(distroMedium.getId().equals(""), false);
	}

	@Test(dependsOnMethods = "testAttachIsoDvd")
	public void testCreateAndAttachHardDisk() throws InterruptedException {
		IMedium hd = null;
		if (new File(adminDisk).exists()) {
			new File(adminDisk).delete();
		}
		hd = manager.getVBox().createHardDisk(diskFormat, adminDisk);
		long size = 4L * 1024L * 1024L * 1024L - 4L;
		IProgress progress = hd.createBaseStorage(new Long(size), new Long(
					MediumVariant.STANDARD.ordinal()));

		ISession session = manager.getSessionObject();
		IMachine machine = manager.getVBox().findMachine(vmName);
		machine.lockMachine(session, LockType.Write);
		IMachine mutable = session.getMachine();
		mutable.attachDevice(controllerIDE, 0, 1, DeviceType.HardDisk, hd);
		mutable.saveSettings();
		session.unlockMachine();
		assertEquals(hd.getId().equals(""), false);
	}

	@Test(dependsOnMethods = "testCreateAndAttachHardDisk")
	public void testConfigureNIC() {
		ISession session = manager.getSessionObject();
		IMachine machine = manager.getVBox().findMachine(vmName);
		machine.lockMachine(session, LockType.Write);
		IMachine mutable = session.getMachine();

		// NAT
		mutable.getNetworkAdapter(new Long(0)).setAttachmentType(
				NetworkAttachmentType.NAT);
		machine.getNetworkAdapter(new Long(0))
		.getNatDriver()
		.addRedirect("guestssh", NATProtocol.TCP, "127.0.0.1", 2222,
				"", 22);
		mutable.getNetworkAdapter(new Long(0)).setEnabled(true);

		mutable.saveSettings();
		session.unlockMachine();
	}

	@Test(dependsOnMethods = "testConfigureNIC")
	public void testAttachGuestAdditions() {
		ISession session = manager.getSessionObject();
		IMachine machine = manager.getVBox().findMachine(vmName);

		IMedium distroMedium = manager.getVBox().openMedium(guestAdditionsDvd, DeviceType.DVD,
				AccessMode.ReadOnly, forceOverwrite);
		machine.lockMachine(session, LockType.Write);
		IMachine mutable = session.getMachine();
		mutable.attachDevice(controllerIDE, 1, 1, DeviceType.DVD, distroMedium);
		mutable.saveSettings(); // write settings to xml
		session.unlockMachine();
		assertEquals(distroMedium.getId().equals(""), false);
	}


	@Test(dependsOnMethods = "testAttachGuestAdditions")
	public void testStartVirtualMachine() throws InterruptedException {
		IMachine machine = manager.getVBox().findMachine(vmName);
		ISession session = manager.getSessionObject();
		launchVMProcess(machine, session);
		assertEquals(machine.getState(), MachineState.Running);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			propagate(e);
		}

		sendKeyboardSequence(keyboardSequence);

		// test if the sshd on the guest is ready and meanwhile apply AdminAccess
		boolean sshDeamonIsRunning = false;
		while(!sshDeamonIsRunning) {
			try {
				AdminAccess.standard().init(new DefaultConfiguration()).render(OsFamily.UNIX);
				if(runScriptOnNode(guestId, "id").getExitCode() == 0)
					sshDeamonIsRunning = true;
			} catch(SshException e) {
				System.err.println("connection reset");
			}
		}
	}

	@Test(dependsOnMethods = "testStartVirtualMachine")
	public void testConfigureGuestAdditions() {
		// TODO generalize
		if(isUbuntu(guestId)) {
			/*
			runScriptOnNode(guestId,
					"m-a prepare -i");
			runScriptOnNode(guestId,
				"mount -o loop /dev/dvd /media/cdrom");
			runScriptOnNode(guestId,
				"sh /media/cdrom/VBoxLinuxAdditions.run");
			
			runScriptOnNode(guestId, "/etc/init.d/vboxadd setup");
			*/
			runScriptOnNode(guestId, "rm /etc/udev/rules.d/70-persistent-net.rules");
			runScriptOnNode(guestId, "mkdir /etc/udev/rules.d/70-persistent-net.rules");
			runScriptOnNode(guestId, "rm -rf /dev/.udev/");
			runScriptOnNode(guestId, "rm /lib/udev/rules.d/75-persistent-net-generator.rules");
		}
	}

	@Test(dependsOnMethods = "testConfigureGuestAdditions")
	public void testStopVirtualMachine() {
		IMachine machine = manager.getVBox().findMachine(vmName);
		powerDownMachine(machine);
		assertEquals(machine.getState(), MachineState.PoweredOff);
	}

	@Test(dependsOnMethods = "testStopVirtualMachine")
	public void testChangeNICtoBridged() {
		ISession session = manager.getSessionObject();
		IMachine adminNode = manager.getVBox().findMachine(vmName);
		adminNode.lockMachine(session, LockType.Write);
		IMachine mutable = session.getMachine();
		// network
		String hostInterface = null;
		String command = "vboxmanage list bridgedifs";
		try {
			Process child = Runtime.getRuntime().exec(command);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(child.getInputStream()));
			String line = "";
			boolean found = false;

			while ((line = bufferedReader.readLine()) != null && !found) {
				if (line.split(":")[0].contains("Name")) {
					hostInterface = line.split(":")[1];
				}
				if (line.split(":")[0].contains("Status") && line.split(":")[1].contains("Up")) {
					System.out.println("bridge: " + hostInterface.trim());
					found = true;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mutable.getNetworkAdapter(new Long(0)).setAttachmentType(NetworkAttachmentType.Bridged);
		mutable.getNetworkAdapter(new Long(0)).setAdapterType(NetworkAdapterType.Am79C973);
		mutable.getNetworkAdapter(new Long(0)).setMACAddress(manager.getVBox().getHost().generateMACAddress());
		mutable.getNetworkAdapter(new Long(0)).setBridgedInterface(hostInterface.trim());
		mutable.getNetworkAdapter(new Long(0)).setEnabled(true);
		mutable.saveSettings();
		session.unlockMachine();
	}

	@Test(dependsOnMethods = "testChangeNICtoBridged")
	public void testTakeAdminNodeSnapshot() {
		ISession session = manager.getSessionObject();
		IMachine adminNode = manager.getVBox().findMachine(vmName);
		adminNode.lockMachine(session, LockType.Write);
		if(adminNode.getCurrentSnapshot() == null || !adminNode.getCurrentSnapshot().getDescription().equals(snapshotDescription)) {
			manager.getSessionObject().getConsole().takeSnapshot(adminNode.getId(), snapshotDescription);
		}
		session.unlockMachine();
	}

	@AfterClass
		void stopVboxWebServer() throws IOException {
			runScriptOnNode(guestId, "pidof vboxwebsrv | xargs kill");
		}

		protected ExecResponse runScriptOnNode(String nodeId, String command,
				RunScriptOptions options) {
			ExecResponse toReturn = context.getComputeService().runScriptOnNode(
					nodeId, command, options);
			assert toReturn.getExitCode() == 0 : toReturn;
			return toReturn;
		}

		protected ExecResponse runScriptOnNode(String nodeId, String command) {
			return runScriptOnNode(nodeId, command, wrapInInitScript(false));
		}

		private File downloadFileUnlessPresent(URI sourceURL,
				String destinationDir, String filename) throws Exception {

			File iso = new File(destinationDir, filename);

			if (!iso.exists()) {
				InputStream is = context.utils().http().get(sourceURL);
				checkNotNull(is, "%s not found", sourceURL);
				try {
					ByteStreams.copy(is, new FileOutputStream(iso));
				} finally {
					Closeables.closeQuietly(is);
				}
			}
			return iso;
		}

		private void sendKeyboardSequence(String keyboardSequence)
				throws InterruptedException {
			String[] sequenceSplited = keyboardSequence.split(" ");
			StringBuilder sb = new StringBuilder();
			for (String line : sequenceSplited) {
				String converted = stringToKeycode(line);
				for (String word : converted.split("  ")) {
					sb.append("vboxmanage controlvm " + vmName
							+ " keyboardputscancode " + word + "; ");

					if (word.endsWith(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP.get("<Enter>"))) {
						runScriptOnNode(hostId, sb.toString(), runAsRoot(false)
								.wrapInInitScript(false));
						sb.delete(0, sb.length()-1);
					}


					if (word.endsWith(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP.get("<Return>"))) {
						runScriptOnNode(hostId, sb.toString(), runAsRoot(false)
								.wrapInInitScript(false));
						sb.delete(0, sb.length()-1);
					}

				}
			}
		}

		private String stringToKeycode(String s) {
			StringBuilder keycodes = new StringBuilder();
			if (s.startsWith("<")) {
				String[] specials = s.split("<");
				for (int i = 1; i < specials.length; i++) {
					keycodes.append(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP
							.get("<" + specials[i]) + "  ");	
				}
				return keycodes.toString();
			}


			int i = 0;
			while (i < s.length()) {
				String digit = s.substring(i, i + 1);
				String hex = KeyboardScancodes.NORMAL_KEYBOARD_BUTTON_MAP
						.get(digit);
				keycodes.append(hex + " ");
				if (i != 0 && i % 14 == 0)
					keycodes.append(" ");
				i++;
			}
			keycodes.append(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP
					.get("<Spacebar>") + " ");

			return keycodes.toString();
		}

		/**
		 * 
		 * @param machine
		 * @param session
		 */
		private void launchVMProcess(IMachine machine, ISession session) {
			IProgress prog = machine.launchVMProcess(session, "gui", "");
			prog.waitForCompletion(-1);
			session.unlockMachine();
		}

		/**
		 * @param machine
		 */
		private void powerDownMachine(IMachine machine) {
			try {
				ISession machineSession = manager.openMachineSession(machine);
				IProgress progress = machineSession.getConsole().powerDown();
				progress.waitForCompletion(-1);
				machineSession.unlockMachine();

				while (!machine.getSessionState().equals(SessionState.Unlocked)) {
					try {
						System.out
						.println("waiting for unlocking session - session state: "
								+ machine.getSessionState());
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				e.printStackTrace();
			}
		}

	}