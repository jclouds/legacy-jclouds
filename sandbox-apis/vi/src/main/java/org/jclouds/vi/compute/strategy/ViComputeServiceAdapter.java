/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vi.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.vi.Image;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.vmware.vim25.CustomizationAdapterMapping;
import com.vmware.vim25.CustomizationDhcpIpGenerator;
import com.vmware.vim25.CustomizationFixedName;
import com.vmware.vim25.CustomizationGlobalIPSettings;
import com.vmware.vim25.CustomizationGuiUnattended;
import com.vmware.vim25.CustomizationIPSettings;
import com.vmware.vim25.CustomizationIdentification;
import com.vmware.vim25.CustomizationIdentitySettings;
import com.vmware.vim25.CustomizationLicenseDataMode;
import com.vmware.vim25.CustomizationLicenseFilePrintData;
import com.vmware.vim25.CustomizationPassword;
import com.vmware.vim25.CustomizationSpec;
import com.vmware.vim25.CustomizationSysprep;
import com.vmware.vim25.CustomizationUserData;
import com.vmware.vim25.CustomizationWinOptions;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostDatastoreBrowser;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * defines the connection between the {@link VI} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class ViComputeServiceAdapter implements ComputeServiceAdapter<VirtualMachine, VirtualMachine, Image, Datacenter> {

	private final ServiceInstance client;
	private String resourcePoolName = "";
	private String vmwareHostName = "";
	private String datastoreName = "";
	private String vmClonedName = "MyWinClone";

	@Inject
	public ViComputeServiceAdapter(ServiceInstance client) {
		this.client = checkNotNull(client, "client");
	}

	@Override
	public VirtualMachine createNodeWithGroupEncodedIntoNameThenStoreCredentials(String tag,
			String name, Template template,
			Map<String, Credentials> credentialStore) {
		try {
			Folder rootFolder = client.getRootFolder();

			VirtualMachine from = (VirtualMachine) new InventoryNavigator(
					rootFolder).searchManagedEntity("VirtualMachine", tag);

			if (from == null) {
				client.getServerConnection().logout();
				return null; 
			}
			
			VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
			VirtualMachineRelocateSpec virtualMachineRelocateSpec = new VirtualMachineRelocateSpec();
			if (!vmwareHostName.equals("") && !datastoreName.equals("") && !resourcePoolName.equals("")) {

				ResourcePool rp = (ResourcePool) new InventoryNavigator(rootFolder)
						.searchManagedEntity("ResourcePool", resourcePoolName);
				
				if(rp == null)
					throw new Exception("The resourcePool specified '" + resourcePoolName + "' doesn't exist");
				virtualMachineRelocateSpec.setPool(rp.getMOR());
	
	
				Datastore ds = (Datastore) new InventoryNavigator(rootFolder)
					.searchManagedEntity("Datastore", datastoreName);
				
				HostSystem host = null;
				host = (HostSystem) new InventoryNavigator(rootFolder)
						.searchManagedEntity("HostSystem", vmwareHostName);
	
				HostDatastoreBrowser hdb = host.getDatastoreBrowser();
	
				if(ds == null)
					throw new Exception("Cannot relocate this cloned machine to the specified datastore '" + datastoreName + "'");
				Datastore dsFound = null;
				Datastore[] dsArray = hdb.getDatastores();
				for (Datastore d : dsArray) {
					if(d.getName().equalsIgnoreCase(ds.getName()))
						dsFound = d;
				}
				if(dsFound == null)
					throw new Exception("Cannot relocate this cloned machine to the specified datastore '" + datastoreName + "'");
				virtualMachineRelocateSpec.setDatastore(dsFound.getMOR());
			}
			
			CustomizationSpec custSpec = new CustomizationSpec();
		
            CustomizationAdapterMapping cam = new CustomizationAdapterMapping();
            CustomizationIPSettings cip = new CustomizationIPSettings();
            cip.setIp(new CustomizationDhcpIpGenerator());
            cam.setAdapter(cip);
            

			// IP customization
//			CustomizationAdapterMapping[] custAdapMapList = new CustomizationAdapterMapping[1];
//			CustomizationAdapterMapping custAdapMap = new CustomizationAdapterMapping();
//          CustomizationIPSettings custIPSettings = new CustomizationIPSettings();
//			CustomizationFixedIp custFixedIp = new CustomizationFixedIp();
//			custFixedIp.setIpAddress(ipAddress);
//			custIPSettings.setIp(custFixedIp);
//			custAdapMap.setAdapter(custIPSettings);
//			custAdapMapList[0] = custAdapMap;
//			custSpec.setNicSettingMap(custAdapMapList);
			
            CustomizationGlobalIPSettings custGlobalIPSetting = new CustomizationGlobalIPSettings();
			

			CustomizationIdentitySettings custIdentitySet = new CustomizationIdentitySettings();

			// sysprep customization
			CustomizationSysprep custSysprep = new CustomizationSysprep();

			CustomizationGuiUnattended guiUnattended = new CustomizationGuiUnattended();
			guiUnattended.setAutoLogon(false);
			guiUnattended.setAutoLogonCount(0);
			guiUnattended.setTimeZone(190);
			

			// user data
			CustomizationPassword custPasswd = new CustomizationPassword();
			custPasswd.setPlainText(true);
			custPasswd.setValue("password");

			CustomizationIdentification custIdentification = new CustomizationIdentification();
			custIdentification.setDomainAdmin("Administrator");
			custIdentification.setDomainAdminPassword(custPasswd);
			custIdentification.setJoinWorkgroup("WORKGROUP");

			CustomizationUserData custUserData = new CustomizationUserData();
			CustomizationFixedName custFixedName = new CustomizationFixedName();
			custFixedName.setName("mycomputer");
			custUserData.setComputerName(custFixedName);
			custUserData.setFullName("sjain");
			custUserData.setOrgName("vmware");
			custUserData.setProductId("PDRXT-M9X8G-898BR-4K427-J2FFY");
			
			///////
			CustomizationWinOptions customizationWinOptions = new CustomizationWinOptions();
			customizationWinOptions.setChangeSID(true);
			customizationWinOptions.setDeleteAccounts(false);
			
			CustomizationLicenseFilePrintData custLPD = new CustomizationLicenseFilePrintData();
	        custLPD.setAutoMode(CustomizationLicenseDataMode.perServer);
	        
	        custSysprep.setLicenseFilePrintData(custLPD);

			custSysprep.setUserData(custUserData);
			custSysprep.setGuiUnattended(guiUnattended);
			custSysprep.setIdentification(custIdentification);
			
			custSpec.setIdentity(custSysprep);
			custSpec.setNicSettingMap(new CustomizationAdapterMapping[] {cam});
			custSpec.setGlobalIPSettings(custGlobalIPSetting);
			custSpec.setOptions(customizationWinOptions);
			

			
			cloneSpec.setCustomization(custSpec);
           
			 //location properties
			 cloneSpec.setLocation(virtualMachineRelocateSpec);
			cloneSpec.setPowerOn(false);
			cloneSpec.setTemplate(false);

			Task task = from.cloneVM_Task((Folder) from.getParent(), vmClonedName,
					cloneSpec);

			String result = task.waitForTask();
			return (VirtualMachine) new InventoryNavigator(
					rootFolder).searchManagedEntity("VirtualMachine", vmClonedName);
		} catch (RemoteException e) {
			return propogate(e);
		} catch (Exception e) {
			return propogate(e);
		}
	
	}

	@Override
	public Iterable<VirtualMachine> listHardwareProfiles() {
		// TODO
		List<VirtualMachine> hardwareProfiles = Lists.newArrayList();
		try {
			
			ManagedEntity[] entities = new InventoryNavigator(
					client.getRootFolder()).searchManagedEntities("VirtualMachine");
			for (ManagedEntity entity : entities) {
				hardwareProfiles.add((VirtualMachine) entity);
			}
			return hardwareProfiles;
		} catch (Exception e) {
			return propogate(e);
		}		
	}

	@Override
	public Iterable<Image> listImages() {
		List<Image> images = Lists.newArrayList();
		try {
			
			ManagedEntity[] entities = new InventoryNavigator(
					client.getRootFolder()).searchManagedEntities("VirtualMachine");
			for (ManagedEntity entity : entities) {
				VirtualMachine vm = (VirtualMachine) entity;
				images.add(new Image(vm.getConfig().getGuestId(), vm.getConfig().getGuestFullName()));
			}
			return images;
		} catch (Exception e) {
			return propogate(e);
		}
	}

	@Override
	public Iterable<VirtualMachine> listNodes() {
		try {
			ManagedEntity[] vmEntities = new InventoryNavigator(client.getRootFolder()).searchManagedEntities("VirtualMachine");
			List<VirtualMachine> vms = Lists.newArrayList();
			for (ManagedEntity entity : vmEntities) {
				VirtualMachine vm = (VirtualMachine) entity;
				vms.add(vm);
			}
			return vms;
		} catch (InvalidProperty e) {
			return propogate(e);
		} catch (RuntimeFault e) {
			return propogate(e);
		} catch (RemoteException e) {
			return propogate(e);
		}

	}

	@Override
	public Iterable<Datacenter> listLocations() {
		ManagedEntity[] datacenterEntities;
		try {
			datacenterEntities = new InventoryNavigator(client.getRootFolder()).searchManagedEntities("Datacenter");
			List<Datacenter> datacenters = Lists.newArrayList();
			for (int i = 0; i< datacenterEntities.length; i++) {
				datacenters.add((Datacenter) datacenterEntities[i]);
			}
			return datacenters;
		} catch (InvalidProperty e) {
			return propogate(e);
		} catch (RuntimeFault e) {
			return propogate(e);
		} catch (RemoteException e) {
			return propogate(e);
		}

	}

	@Override
	public VirtualMachine getNode(String vmName) {
		
		Folder rootFolder = client.getRootFolder();

		try {
			return (VirtualMachine) new InventoryNavigator(
					rootFolder).searchManagedEntity("VirtualMachine", vmName);
		} catch (InvalidProperty e) {
			return propogate(e);
		} catch (RuntimeFault e) {
			return propogate(e);
		} catch (RemoteException e) {
			return propogate(e);
		}
	}

	@Override
	public void destroyNode(String id) {
		/*
		try {
			client.domainLookupByUUIDString(id).destroy();

				XMLBuilder builder = XMLBuilder.parse(new InputSource(new StringReader(
						client.domainLookupByUUIDString(id).getXMLDesc(0)
				)));
				String diskFileName = builder.xpathFind("//devices/disk[@device='disk']/source").getElement().getAttribute("file");
				StorageVol storageVol = client.storageVolLookupByPath(diskFileName);
				storageVol.delete(0);
				client.domainLookupByUUIDString(id).undefine();

		} catch (LibvirtException e) {
			propogate(e);
		} catch (Exception e) {
			propogate(e);
		} 
		*/
	}

	@Override
	public void rebootNode(String id) {
		/*
		try {
			client.domainLookupByUUIDString(id).reboot(0);
		} catch (LibvirtException e) {
			propogate(e);
		}
		*/
	}
	
	@Override
	public void resumeNode(String id) {
		/*
		try {
			client.domainLookupByUUIDString(id).resume();
		} catch (LibvirtException e) {
			propogate(e);
		}      
		*/
	}

	@Override
	public void suspendNode(String id) {
		/*
		try {
			client.domainLookupByUUIDString(id).suspend();
		} catch (LibvirtException e) {
			propogate(e);
		} 
		*/     
	}		

	protected <T> T propogate(Exception e) {
		Throwables.propagate(e);
		assert false;
		return null;
	}

	/*
	private static StorageVol cloneVolume(StoragePool storagePool, StorageVol from) throws LibvirtException,
	XPathExpressionException, ParserConfigurationException, SAXException, IOException, TransformerException {
		return storagePool.storageVolCreateXMLFrom(generateClonedVolumeXML(from.getXMLDesc(0)), from, 0);
	}

	private static String generateClonedVolumeXML(String fromXML) throws ParserConfigurationException, SAXException,
	IOException, XPathExpressionException, TransformerException {

		Properties outputProperties = generateOutputXMLProperties();
		XMLBuilder builder = XMLBuilder.parse(new InputSource(new StringReader(fromXML)));
		String nodeNamingConvention = "%s-%s";
		String tag = "-clone";
		String suffix = String.format(nodeNamingConvention, tag, Integer.toHexString(new SecureRandom().nextInt(4095)));
		builder.xpathFind("//volume/name").t(suffix);
		builder.xpathFind("//volume/key").t(suffix);
		builder.xpathFind("//volume/target/path").t(suffix);

		return builder.asString(outputProperties);
	}

	private static String generateClonedDomainXML(String fromXML, StorageVol clonedVol) throws ParserConfigurationException, SAXException,
	IOException, XPathExpressionException, TransformerException, LibvirtException {

		Properties outputProperties = generateOutputXMLProperties();

		XMLBuilder builder = XMLBuilder.parse(new InputSource(new StringReader(fromXML)));

		String nodeNamingConvention = "%s-%s";
		String tag = "-clone";
		String suffix = String.format(nodeNamingConvention, tag, Integer.toHexString(new SecureRandom().nextInt(4095)));
		builder.xpathFind("//domain/name").t(suffix);
		// change uuid domain
		Element oldChild = builder.xpathFind("//domain/uuid").getElement();
		Node newNode = oldChild.cloneNode(true);
		newNode.getFirstChild().setNodeValue(UUID.randomUUID().toString());
		builder.getDocument().getDocumentElement().replaceChild(newNode, oldChild);

		//String fromVolPath = builder.xpathFind("//domain/devices/disk/source").getElement().getAttribute("file");
		builder.xpathFind("//domain/devices/disk/source").a("file", clonedVol.getPath());
		// generate valid MAC address
		String fromMACaddress = builder.xpathFind("//domain/devices/interface/mac").getElement().getAttribute("address");
		String lastMACoctet = Integer.toHexString(new SecureRandom().nextInt(255));			
		builder.xpathFind("//domain/devices/interface/mac").a("address", 
				fromMACaddress.substring(0, fromMACaddress.lastIndexOf(":")+1) + lastMACoctet
		);
		return builder.asString(outputProperties);
	}
	
	private static Properties generateOutputXMLProperties() {
		Properties outputProperties = new Properties();
		// Explicitly identify the output as an XML document
		outputProperties.put(javax.xml.transform.OutputKeys.METHOD, "xml");
		// Pretty-print the XML output (doesn't work in all cases)
		outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");
		// Get 2-space indenting when using the Apache transformer
		outputProperties.put("{http://xml.apache.org/xslt}indent-amount", "2");
		return outputProperties;
	}
	*/
}