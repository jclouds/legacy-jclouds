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

package org.jclouds.ovf.xml;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ovf.Envelope;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
public class EnvelopeHandler extends ParseSax.HandlerWithResult<Envelope> {

   protected StringBuilder currentText = new StringBuilder();
   protected Envelope.Builder builder = Envelope.builder();

   public Envelope getResult() {
      try {
         return builder.build();
      } finally {
         builder = Envelope.builder();
      }
   }

   private final VirtualSystemHandler virtualSystemHandler;
   private final DiskSectionHandler diskHandler;
   private final NetworkSectionHandler networkHandler;

   @Inject
   public EnvelopeHandler(DiskSectionHandler diskHandler, NetworkSectionHandler networkHandler,
            VirtualSystemHandler osHandler) {
      this.virtualSystemHandler = osHandler;
      this.diskHandler = diskHandler;
      this.networkHandler = networkHandler;
   }

   @SuppressWarnings("unchecked")
   protected SectionHandler defaultSectionHandler = SectionHandler.create();

   @SuppressWarnings("unchecked")
   @Inject(optional = true)
   @Named("VirtualSystem")
   Map<String, Provider<? extends SectionHandler>> extensionHandlers = ImmutableMap
            .<String, Provider<? extends SectionHandler>> of();

   @SuppressWarnings("unchecked")
   protected SectionHandler extensionHandler;

   private boolean inDisk;
   private boolean inNetwork;
   private boolean inVirtualSystem;
   private boolean inSection;
   private boolean inExtensionSection;

   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      if (equalsOrSuffix(qName, "DiskSection")) {
         inDisk = true;
      } else if (equalsOrSuffix(qName, "NetworkSection")) {
         inNetwork = true;
      } else if (equalsOrSuffix(qName, "VirtualSystem")) {
         inVirtualSystem = true;
      } else if (extensionHandlers.containsKey(qName)) {
         inExtensionSection = true;
         extensionHandler = extensionHandlers.get(qName).get();
      } else if (qName.endsWith("Section")) {
         inSection = true;
      }

      if (inDisk) {
         diskHandler.startElement(uri, localName, qName, attrs);
      } else if (inNetwork) {
         networkHandler.startElement(uri, localName, qName, attrs);
      } else if (inVirtualSystem) {
         virtualSystemHandler.startElement(uri, localName, qName, attrs);
      } else if (inExtensionSection) {
         extensionHandler.startElement(uri, localName, qName, attrs);
      } else if (inSection) {
         defaultSectionHandler.startElement(uri, localName, qName, attrs);
      }

   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (equalsOrSuffix(qName, "DiskSection")) {
         inDisk = false;
         builder.diskSection(diskHandler.getResult());
      } else if (equalsOrSuffix(qName, "NetworkSection")) {
         inNetwork = false;
         builder.networkSection(networkHandler.getResult());
      } else if (equalsOrSuffix(qName, "VirtualSystem")) {
         inVirtualSystem = false;
         builder.virtualSystem(virtualSystemHandler.getResult());
      } else if (extensionHandlers.containsKey(qName)) {
         builder.additionalSection(extensionHandler.getResult());
         inExtensionSection = false;
      } else if (qName.endsWith("Section")) {
         builder.additionalSection(defaultSectionHandler.getResult());
         inSection = false;
      }

      if (inDisk) {
         diskHandler.endElement(uri, localName, qName);
      } else if (inNetwork) {
         networkHandler.endElement(uri, localName, qName);
      } else if (inVirtualSystem) {
         virtualSystemHandler.endElement(uri, localName, qName);
      } else if (inExtensionSection) {
         extensionHandler.endElement(uri, localName, qName);
      } else if (inSection) {
         defaultSectionHandler.endElement(uri, localName, qName);
      } else {
         currentText = new StringBuilder();
      }
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inDisk) {
         diskHandler.characters(ch, start, length);
      } else if (inNetwork) {
         networkHandler.characters(ch, start, length);
      } else if (inVirtualSystem) {
         virtualSystemHandler.characters(ch, start, length);
      } else if (inExtensionSection) {
         extensionHandler.characters(ch, start, length);
      } else if (inSection) {
         defaultSectionHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
