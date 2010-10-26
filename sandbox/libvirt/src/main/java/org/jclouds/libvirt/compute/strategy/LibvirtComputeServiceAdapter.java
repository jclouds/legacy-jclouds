package org.jclouds.libvirt.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.libvirt.Datacenter;
import org.jclouds.libvirt.Image;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.jna.Libvirt;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * defines the connection between the {@link Libvirt} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class LibvirtComputeServiceAdapter implements ComputeServiceAdapter<Domain, Domain, Image, Datacenter> {
   private final Connect client;

   @Inject
   public LibvirtComputeServiceAdapter(Connect client) {
      this.client = checkNotNull(client, "client");
   }

   @Override
   public Domain createNodeAndStoreCredentials(String tag, String name, Template template,
         Map<String, Credentials> credentialStore) {
      // create the backend object using parameters from the template.
      // Domain from = client.createDomainInDC(template.getLocation().getId(), name,
      // Integer.parseInt(template.getImage().getProviderId()),
      // Integer.parseInt(template.getHardware().getProviderId()));
      // store the credentials so that later functions can use them
      // credentialStore.put(from.id + "", new Credentials(from.loginUser, from.password));
	   
       String xmlDesc ="<domain type='kvm'>" + "<name>test</name>" + "<uuid>abcf2039-a9f1-a659-7f91-e0f82f59d52e</uuid>" +
       "<memory>524288</memory>" +
       "<currentMemory>524288</currentMemory>" +
       "<vcpu>1</vcpu>" +
       "<os><type arch='i686' machine='pc-0.12'>hvm</type><boot dev='hd'/></os>" +
       "<features><acpi/>              <apic/>              <pae/>            </features>" +
       "<clock offset='utc'/>" +
       "<on_poweroff>destroy</on_poweroff>"+
       "<on_reboot>restart</on_reboot>"+
       "<on_crash>restart</on_crash>"+
       "<devices><emulator>/usr/bin/kvm</emulator><disk type='file' device='disk'><driver name='qemu' type='raw'/><source file='/var/lib/libvirt/images/test.img'/>                <target dev='vda' bus='virtio'/>              </disk> <disk type='block' device='cdrom'>                <driver name='qemu' type='raw'/>                <target dev='hdc' bus='ide'/><readonly/></disk>               <interface type='network'>                <mac address='52:54:00:05:cf:92'/>                <source network='default'/>                <model type='virtio'/>              </interface>              <console type='pty'>                <target port='0'/>              </console>              <console type='pty'>                <target port='0'/>              </console>              <input type='mouse' bus='ps2'/>              <graphics type='vnc' port='-1' autoport='yes'/>              <video>                <model type='cirrus' vram='9216' heads='1'/>              </video> </devices>"+
       "</domain>";
       
       Domain domain = null;
       try {
		client.domainDefineXML(xmlDesc);
		domain = client.domainCreateXML(xmlDesc, 1);	   
	} catch (LibvirtException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return domain;
   }

   @Override
   public Iterable<Domain> listHardware() {
      return listNodes();
   }

   @Override
   public Iterable<Image> listImages() {
      return ImmutableSet.of();
      // TODO
      // return client.listImages();
   }

   @Override
   public Iterable<Domain> listNodes() {
      try {
         List<Domain> domains = Lists.newArrayList();
         for (int domain : client.listDomains()) {
            domains.add(client.domainLookupByID(domain));
         }
         return domains;
      } catch (LibvirtException e) {
         return propogate(e);
      }
   }

//   @Override
//   public Iterable<Domain> listNodes() {
//	      try {
//	         List<Domain> domains = Lists.newArrayList();
//	         for (String domain : client.listDefinedDomains()) {
//	            domains.add(client.domainLookupByName(domain));
//	         }
//	         return domains;
//	      } catch (LibvirtException e) {
//	         return propogate(e);
//	      }
//	   }
   
   protected <T> T propogate(LibvirtException e) {
      Throwables.propagate(e);
      assert false;
      return null;
   }

   @Override
   public Iterable<Datacenter> listLocations() {
      return ImmutableSet.of(new Datacenter(1, "SFO"));
   }

   @Override
   public Domain getNode(String id) {
      try {
         return client.domainLookupByUUIDString(id);
      } catch (LibvirtException e) {
         return propogate(e);
      }
   }

   @Override
   public void destroyNode(String id) {
      try {
         client.domainLookupByUUIDString(id).destroy();
      } catch (LibvirtException e) {
         propogate(e);
      }
   }

   @Override
   public void rebootNode(String id) {
      try {
         client.domainLookupByUUIDString(id).reboot(0);
      } catch (LibvirtException e) {
         propogate(e);
      }
   }
   
   public void createDomain() throws LibvirtException {
       Domain domain = client.domainDefineXML("<domain type='test' id='2'>" + "  <name>deftest</name>"
               + "  <uuid>004b96e1-2d78-c30f-5aa5-f03c87d21e70</uuid>" + "  <memory>8388608</memory>"
               + "  <vcpu>2</vcpu>" + "  <os><type arch='i686'>hvm</type></os>" + "  <on_reboot>restart</on_reboot>"
               + "  <on_poweroff>destroy</on_poweroff>" + "  <on_crash>restart</on_crash>" + "</domain>");
       
       
   }
   
}
