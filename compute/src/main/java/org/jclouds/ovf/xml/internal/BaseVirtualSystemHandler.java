/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ovf.xml.internal;

import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.ovf.Section;
import org.jclouds.ovf.internal.BaseVirtualSystem;
import org.jclouds.ovf.xml.OperatingSystemSectionHandler;
import org.jclouds.ovf.xml.ProductSectionHandler;
import org.jclouds.ovf.xml.SectionHandler;
import org.jclouds.ovf.xml.VirtualHardwareSectionHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
public class BaseVirtualSystemHandler<T extends BaseVirtualSystem<T>, B extends BaseVirtualSystem.Builder<T>> extends
         SectionHandler<T, B> {

   private final OperatingSystemSectionHandler osHandler;
   private final VirtualHardwareSectionHandler hardwareHandler;
   private final ProductSectionHandler productHandler;

   @Inject
   public BaseVirtualSystemHandler(Provider<B> builderProvider, OperatingSystemSectionHandler osHandler,
            VirtualHardwareSectionHandler hardwareHandler, ProductSectionHandler productHandler) {
      super(builderProvider);
      this.osHandler = osHandler;
      this.hardwareHandler = hardwareHandler;
      this.productHandler = productHandler;
   }

   @SuppressWarnings("unchecked")
   protected SectionHandler defaultSectionHandler = SectionHandler.create();

   @SuppressWarnings("unchecked")
   protected Map<String, Provider<? extends SectionHandler>> extensionHandlers = ImmutableMap
            .<String, Provider<? extends SectionHandler>> of();

   @SuppressWarnings("unchecked")
   @Inject(optional = true)
   protected void setExtensionHandlers(
            @Named("VirtualSystem") Map<String, Provider<? extends SectionHandler>> extensionHandlers) {
      extensionHandlers = ImmutableMap.<String, Provider<? extends SectionHandler>> builder().putAll(
               this.extensionHandlers).putAll(extensionHandlers).build();
   }

   @SuppressWarnings("unchecked")
   protected SectionHandler extensionHandler;

   private boolean inHardware;
   private boolean inOs;
   private boolean inProduct;
   private boolean inSection;
   private boolean inExtensionSection;
   private int depth;

   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = cleanseAttributes(attrs);
      depth++;
      if (depth == 2) {
         if (equalsOrSuffix(qName, "VirtualHardwareSection")) {
            inHardware = true;
         } else if (equalsOrSuffix(qName, "OperatingSystemSection")) {
            inOs = true;
         } else if (equalsOrSuffix(qName, "ProductSection")) {
            inProduct = true;
         } else if (extensionHandlers.containsKey(qName)) {
            inExtensionSection = true;
            extensionHandler = extensionHandlers.get(qName).get();
         } else if (attributes.containsKey("type") && extensionHandlers.containsKey(attributes.get("type"))) {
            inExtensionSection = true;
            extensionHandler = extensionHandlers.get(attributes.get("type")).get();
         } else if (qName.endsWith("Section")) {
            inSection = true;
         }
      }
      if (inHardware) {
         hardwareHandler.startElement(uri, localName, qName, attrs);
      } else if (inOs) {
         osHandler.startElement(uri, localName, qName, attrs);
      } else if (inProduct) {
         productHandler.startElement(uri, localName, qName, attrs);
      } else if (inExtensionSection) {
         extensionHandler.startElement(uri, localName, qName, attrs);
      } else if (inSection) {
         defaultSectionHandler.startElement(uri, localName, qName, attrs);
      } else if (equalsOrSuffix(qName, "VirtualSystem")) {
         builder.id(attributes.get("id"));
      }

   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      depth--;
      if (depth == 1) {
         if (equalsOrSuffix(qName, "VirtualHardwareSection")) {
            inHardware = false;
            builder.virtualHardwareSection(hardwareHandler.getResult());
         } else if (equalsOrSuffix(qName, "OperatingSystemSection")) {
            inOs = false;
            builder.operatingSystemSection(osHandler.getResult());
         } else if (equalsOrSuffix(qName, "ProductSection")) {
            inProduct = false;
            builder.productSection(productHandler.getResult());
         } else if (extensionHandlers.containsKey(qName)) {
            addAdditionalSection(qName, extensionHandler.getResult());
            inSection = false;
            inExtensionSection = false;
         } else if (qName.endsWith("Section")) {
            addAdditionalSection(qName, inExtensionSection ? extensionHandler.getResult() : defaultSectionHandler
                     .getResult());
            inSection = false;
            inExtensionSection = false;
         }
      }

      if (inHardware) {
         hardwareHandler.endElement(uri, localName, qName);
      } else if (inOs) {
         osHandler.endElement(uri, localName, qName);
      } else if (inProduct) {
         productHandler.endElement(uri, localName, qName);
      } else if (inExtensionSection) {
         extensionHandler.endElement(uri, localName, qName);
      } else if (inSection) {
         defaultSectionHandler.endElement(uri, localName, qName);
      } else {
         if (equalsOrSuffix(qName, "Info")) {
            builder.info(currentOrNull(currentText));
         } else if (equalsOrSuffix(qName, "Name")) {
            builder.name(currentOrNull(currentText));
         }
         super.endElement(uri, localName, qName);
      }
   }

   @SuppressWarnings("unchecked")
   protected void addAdditionalSection(String qName, Section additionalSection) {
      builder.additionalSection(qName, additionalSection);
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inHardware) {
         hardwareHandler.characters(ch, start, length);
      } else if (inOs) {
         osHandler.characters(ch, start, length);
      } else if (inProduct) {
         productHandler.characters(ch, start, length);
      } else if (inExtensionSection) {
         extensionHandler.characters(ch, start, length);
      } else if (inSection) {
         defaultSectionHandler.characters(ch, start, length);
      } else {
         super.characters(ch, start, length);
      }
   }

}
