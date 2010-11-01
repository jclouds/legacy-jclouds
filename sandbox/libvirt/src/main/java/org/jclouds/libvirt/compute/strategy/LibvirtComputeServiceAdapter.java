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

package org.jclouds.libvirt.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.libvirt.Datacenter;
import org.jclouds.libvirt.Image;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.StoragePool;
import org.libvirt.StorageVol;
import org.libvirt.jna.Libvirt;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.jamesmurty.utils.XMLBuilder;

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
   public Domain runNodeWithTagAndNameAndStoreCredentials(String tag, String name, Template template,
            Map<String, Credentials> credentialStore) {
      // create the backend object using parameters from the template.
      // Domain from = client.createDomainInDC(template.getLocation().getId(), name,
      // Integer.parseInt(template.getImage().getProviderId()),
      // Integer.parseInt(template.getHardware().getProviderId()));
	   // store the credentials so that later functions can use them
      // credentialStore.put(from.id + "", new Credentials(from.loginUser, from.password));

      //String[] domains;
      try {
         //domains = client.listDefinedDomains();
         String xmlDesc  = "";
         Domain domain = null;
         //for (String domainName : domains) {
         //   domain = client.domainLookupByName(domainName);
         //   if (domainName.equals(tag)) {
         String domainName = tag;
         domain = client.domainLookupByName(domainName);
         System.out.println("domain name " + domain.getName());
         XMLBuilder builder = XMLBuilder.parse(new InputSource(new StringReader(domain.getXMLDesc(0))));
         Document doc = builder.getDocument();
         XPathExpression expr = null;
               NodeList nodes = null;
               String xpathString = "//devices/disk[@device='disk']/source/@file"; // +
               expr = XPathFactory.newInstance().newXPath().compile(xpathString);
               nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
               String diskFileName = nodes.item(0).getNodeValue();
               StorageVol storageVol = client.storageVolLookupByPath(diskFileName);

               // cloning volume
               String poolName = "default";
               StoragePool storagePool = client.storagePoolLookupByName(poolName);
               StorageVol clonedVol = cloneVolume(storagePool, storageVol);

               System.out.println(clonedVol.getXMLDesc(0));
               // define Domain
               String xmlFinal = generateClonedDomainXML(domain.getXMLDesc(0));
               domain = client.domainDefineXML(xmlFinal);

        	   // store the credentials so that later functions can use them
               //credentialStore.put(domain.getUUIDString() + "", new Credentials("identity", "credential"));

            //}
         //}
         return domain;
      } catch (LibvirtException e) {
         return propogate(e);
      } catch (Exception e) {
         return propogate(e);
      }
   }

   @Override
   public Iterable<Domain> listHardwareProfiles() {
      return listNodes();
   }

   @Override
   public Iterable<Image> listImages() {
      // return ImmutableSet.of();
      // TODO
      // return client.listImages();
	   int i = 1;
	   try {
	   String[] domains = client.listDefinedDomains();
	   List<Image> images = Lists.newArrayList();
	   for (String domainName : domains) {
		   images.add(new Image(i++, domainName));
	   }
	   return images;
	   } catch (Exception e) {
		   return propogate(e);
	   }
   }

   @Override
   public Iterable<Domain> listNodes() {
      try {
         List<Domain> domains = Lists.newArrayList();
         for (String domain : client.listDefinedDomains()) {
            domains.add(client.domainLookupByName(domain));
         }
         return domains;
      } catch (LibvirtException e) {
         return propogate(e);
      }
   }

   protected <T> T propogate(LibvirtException e) {
      Throwables.propagate(e);
      assert false;
      return null;
   }

   protected <T> T propogate(Exception e) {
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

   private static StorageVol cloneVolume(StoragePool storagePool, StorageVol from) throws LibvirtException,
            XPathExpressionException, ParserConfigurationException, SAXException, IOException, TransformerException {
      String fromXML = from.getXMLDesc(0);
      String clonedXML = generateClonedVolumeXML(fromXML);
      return storagePool.storageVolCreateXMLFrom(clonedXML, from, 0);
   }

   private static String generateClonedVolumeXML(String fromXML) throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException, TransformerException {

      Properties outputProperties = new Properties();
      // Explicitly identify the output as an XML document
      outputProperties.put(javax.xml.transform.OutputKeys.METHOD, "xml");
      // Pretty-print the XML output (doesn't work in all cases)
      outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");
      // Get 2-space indenting when using the Apache transformer
      outputProperties.put("{http://xml.apache.org/xslt}indent-amount", "2");

      XMLBuilder builder = XMLBuilder.parse(new InputSource(new StringReader(fromXML)));

      String cloneAppend = "-clone";
      builder.xpathFind("//volume/name").t(cloneAppend);
      builder.xpathFind("//volume/key").t(cloneAppend);
      builder.xpathFind("//volume/target/path").t(cloneAppend);

      return builder.asString(outputProperties);
   }

   private static String generateClonedDomainXML(String fromXML) throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException, TransformerException {

      Properties outputProperties = new Properties();
      // Explicitly identify the output as an XML document
      outputProperties.put(javax.xml.transform.OutputKeys.METHOD, "xml");
      // Pretty-print the XML output (doesn't work in all cases)
      outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");
      // Get 2-space indenting when using the Apache transformer
      outputProperties.put("{http://xml.apache.org/xslt}indent-amount", "2");

      XMLBuilder builder = XMLBuilder.parse(new InputSource(new StringReader(fromXML)));

      String cloneAppend = "-clone";

      builder.xpathFind("//domain/name").t(cloneAppend);
      // change uuid domain
      Element oldChild = builder.xpathFind("//domain/uuid").getElement();
      Node newNode = oldChild.cloneNode(true);
      newNode.getFirstChild().setNodeValue(UUID.randomUUID().toString());
      builder.getDocument().getDocumentElement().replaceChild(newNode, oldChild);

      builder.xpathFind("//domain/devices/disk/source").a("file", "/var/lib/libvirt/images/ubuntu.img-clone");
      // TODO generate valid MAC address
      builder.xpathFind("//domain/devices/interface/mac").a("address", "52:54:00:5c:dd:eb");
      return builder.asString(outputProperties);
   }

   @Override
   public void resumeNode(String id) {
      try {
         client.domainLookupByUUIDString(id).resume();
      } catch (LibvirtException e) {
         propogate(e);
      }      
   }

   @Override
   public void suspendNode(String id) {
      try {
         client.domainLookupByUUIDString(id).suspend();
      } catch (LibvirtException e) {
         propogate(e);
      }      
   }

}
