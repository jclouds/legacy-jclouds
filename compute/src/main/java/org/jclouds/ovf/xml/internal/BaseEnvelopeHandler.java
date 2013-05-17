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

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ovf.internal.BaseEnvelope;
import org.jclouds.ovf.internal.BaseVirtualSystem;
import org.jclouds.ovf.xml.DiskSectionHandler;
import org.jclouds.ovf.xml.NetworkSectionHandler;
import org.jclouds.ovf.xml.SectionHandler;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
public class BaseEnvelopeHandler<V extends BaseVirtualSystem<V>, B extends BaseVirtualSystem.Builder<V>, H extends BaseVirtualSystemHandler<V, B>, E extends BaseEnvelope<V, E>, T extends BaseEnvelope.Builder<V, E>>
         extends ParseSax.HandlerWithResult<E> {

   public E getResult() {
      try {
         return builder.build();
      } finally {
         builder = envelopeBuilderProvider.get();
      }
   }

   protected final H virtualSystemHandler;
   protected final DiskSectionHandler diskHandler;
   protected final NetworkSectionHandler networkHandler;
   protected final Provider<T> envelopeBuilderProvider;
   protected T builder;

   @Inject
   public BaseEnvelopeHandler(DiskSectionHandler diskHandler, NetworkSectionHandler networkHandler,
            H virtualSystemHandler, Provider<T> envelopeBuilderProvider) {
      this.virtualSystemHandler = virtualSystemHandler;
      this.diskHandler = diskHandler;
      this.networkHandler = networkHandler;
      this.envelopeBuilderProvider = envelopeBuilderProvider;
      this.builder = envelopeBuilderProvider.get();
   }

   @SuppressWarnings("unchecked")
   protected SectionHandler defaultSectionHandler = SectionHandler.create();

   @SuppressWarnings("unchecked")
   @Inject(optional = true)
   @Named("Envelope")
   Map<String, Provider<? extends SectionHandler>> extensionHandlers = ImmutableMap
            .<String, Provider<? extends SectionHandler>> of();

   @SuppressWarnings("unchecked")
   protected SectionHandler extensionHandler;

   protected boolean inDisk;
   protected boolean inNetwork;
   protected boolean inVirtualSystem;
   protected boolean inSection;
   protected boolean inExtensionSection;

   protected int depth = 0;

   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      depth++;
      if (depth == 2) {
         if (equalsOrSuffix(qName, "DiskSection")) {
            inDisk = true;
         } else if (equalsOrSuffix(qName, "NetworkSection")) {
            inNetwork = true;
         } else if (equalsOrSuffix(qName, "VirtualSystem")) {
            inVirtualSystem = true;
         } else if (extensionHandlers.containsKey(qName) || attributes.containsKey("type")
                  && extensionHandlers.containsKey(attributes.get("type"))) {
            inExtensionSection = true;
            extensionHandler = extensionHandlers.get(qName).get();
         } else if (qName.endsWith("Section")) {
            inSection = true;
         }
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
      depth--;
      if (depth == 1) {
         if (equalsOrSuffix(qName, "DiskSection")) {
            inDisk = false;
            builder.diskSection(diskHandler.getResult());
         } else if (equalsOrSuffix(qName, "NetworkSection")) {
            inNetwork = false;
            builder.networkSection(networkHandler.getResult());
         } else if (equalsOrSuffix(qName, "VirtualSystemCollection")) {
            // http://code.google.com/p/jclouds/issues/detail?id=811
            throw new IllegalArgumentException("this handler cannot currently create envelopes with multiple virtual systems");
         } else if (equalsOrSuffix(qName, "VirtualSystem")) {
            inVirtualSystem = false;
            builder.virtualSystem(virtualSystemHandler.getResult());
         } else if (extensionHandlers.containsKey(qName)) {
            builder.additionalSection(qName, extensionHandler.getResult());
            inExtensionSection = false;
         } else if (qName.endsWith("Section")) {
            builder.additionalSection(qName, defaultSectionHandler.getResult());
            inSection = false;
         }
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
      }
   }

}
