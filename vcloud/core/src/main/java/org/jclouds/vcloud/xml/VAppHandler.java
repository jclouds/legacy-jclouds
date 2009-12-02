package org.jclouds.vcloud.xml;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.SortedSet;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.VirtualSystem;
import org.jclouds.vcloud.domain.internal.VAppImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class VAppHandler extends ParseSax.HandlerWithResult<VApp> {

   private final VirtualSystemHandler systemHandler;
   private final ResourceAllocationHandler allocationHandler;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public VAppHandler(VirtualSystemHandler systemHandler,
            ResourceAllocationHandler allocationHandler) {
      this.systemHandler = systemHandler;
      this.allocationHandler = allocationHandler;
   }

   protected VirtualSystem system;
   protected SortedSet<ResourceAllocation> allocations = Sets.newTreeSet();
   protected VAppStatus status;
   protected final ListMultimap<String, InetAddress> networkToAddresses = ArrayListMultimap.create();
   protected StringBuilder currentText = new StringBuilder();
   protected String operatingSystemDescription;
   protected boolean inOs;
   protected String networkName;
   protected String name;
   protected String id;
   protected URI location;

   public VApp getResult() {
      return new VAppImpl(id, name, location, status, networkToAddresses,
               operatingSystemDescription, system, allocations);
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("VApp")) {
         name = id = attributes.getValue(attributes.getIndex("name"));
         location = URI.create(attributes.getValue(attributes.getIndex("href")));
         status = VAppStatus.fromValue(attributes.getValue(attributes.getIndex("status")));
      } else if (qName.equals("ovf:OperatingSystemSection")) {
         inOs = true;
      } else if (qName.equals("NetworkConfig")) {
         networkName = attributes.getValue(attributes.getIndex("name"));
      } else {
         systemHandler.startElement(uri, localName, qName, attributes);
         allocationHandler.startElement(uri, localName, qName, attributes);
      }

   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equals("ovf:OperatingSystemSection")) {
         inOs = false;
      } else if (inOs && qName.equals("Description")) {
         operatingSystemDescription = currentText.toString().trim();
      } else if (qName.equals("IPAddress")) {
         networkToAddresses.put(networkName, parseInetAddress(currentText.toString().trim()));
      } else if (qName.equals("System")) {
         systemHandler.endElement(uri, localName, qName);
         system = systemHandler.getResult();
      } else if (qName.equals("Item")) {
         allocationHandler.endElement(uri, localName, qName);
         allocations.add(allocationHandler.getResult());
      } else {
         systemHandler.endElement(uri, localName, qName);
         allocationHandler.endElement(uri, localName, qName);
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
      systemHandler.characters(ch, start, length);
      allocationHandler.characters(ch, start, length);
   }

   private InetAddress parseInetAddress(String string) {
      String[] byteStrings = string.split("\\.");
      byte[] bytes = new byte[4];
      for (int i = 0; i < 4; i++) {
         bytes[i] = (byte) Integer.parseInt(byteStrings[i]);
      }
      try {
         return InetAddress.getByAddress(bytes);
      } catch (UnknownHostException e) {
         logger.warn(e, "error parsing ipAddress", currentText);
      }
      return null;
   }

}
