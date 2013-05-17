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
package org.jclouds.ec2.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.HandlerForGeneratedRequestWithResult;
import org.jclouds.location.Region;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.base.Supplier;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
public class SecurityGroupHandler extends ParseSax.HandlerForGeneratedRequestWithResult<SecurityGroup> {

   protected final IpPermissionHandler ipPermissionHandler;
   protected final Supplier<String> defaultRegion;

   protected StringBuilder currentText = new StringBuilder();
   protected SecurityGroup.Builder<?> builder;
   protected boolean inIpPermissions;

   protected int itemDepth;

   protected String region;

   @Inject
   public SecurityGroupHandler(IpPermissionHandler ipPermissionHandler, @Region Supplier<String> defaultRegion) {
      this.ipPermissionHandler = ipPermissionHandler;
      this.defaultRegion = defaultRegion;
   }

   protected SecurityGroup.Builder<?> builder() {
      return SecurityGroup.builder().region(region);
   }

   @Override
   public HandlerForGeneratedRequestWithResult<SecurityGroup> setContext(HttpRequest request) {
      region = AWSUtils.findRegionInArgsOrNull(GeneratedHttpRequest.class.cast(request));
      if (region == null)
         region = defaultRegion.get();
      builder = builder();
      return super.setContext(request);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SecurityGroup getResult() {
      try {
         return builder.build();
      } finally {
         builder = builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "item")) {
         itemDepth++;
      } else if (equalsOrSuffix(qName, "ipPermissions")) {
         inIpPermissions = true;
      }
      if (inIpPermissions) {
         ipPermissionHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "item")) {
         endItem(uri, name, qName);
         itemDepth--;
      } else if (equalsOrSuffix(qName, "ipPermissions")) {
         inIpPermissions = false;
         itemDepth = 0;
      } else if (inIpPermissions) {
         ipPermissionHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "groupName")) {
         builder.name(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "groupId")) {
         builder.id(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "ownerId")) {
         builder.ownerId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "groupDescription")) {
         builder.description(currentOrNull(currentText));
      }
      currentText = new StringBuilder();
   }

   protected void endItem(String uri, String name, String qName) throws SAXException {
      if (inIpPermissions) {
         if (itemDepth == 2)
            builder.ipPermission(ipPermissionHandler.getResult());
         else
            ipPermissionHandler.endElement(uri, name, qName);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inIpPermissions) {
         ipPermissionHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
