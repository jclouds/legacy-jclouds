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
package org.jclouds.vcloud.xml.ovf;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ovf.NetworkSection;
import org.jclouds.ovf.xml.NetworkSectionHandler;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkSection;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class VCloudNetworkSectionHandler extends ParseSax.HandlerWithResult<VCloudNetworkSection> {
   private final NetworkSectionHandler networkSectionHandler;

   @Inject
   VCloudNetworkSectionHandler(NetworkSectionHandler networkSectionHandler) {
      this.networkSectionHandler = networkSectionHandler;
   }

   private ReferenceType net;

   public VCloudNetworkSection getResult() {
      NetworkSection system = networkSectionHandler.getResult();
      return new VCloudNetworkSection(net.getType(), net.getHref(), system.getInfo(), system.getNetworks());
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "NetworkSection")) {
         this.net = Utils.newReferenceType(attributes);
      }
      networkSectionHandler.startElement(uri, localName, qName, attrs);
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      networkSectionHandler.endElement(uri, localName, qName);
   }

   public void characters(char ch[], int start, int length) {
      networkSectionHandler.characters(ch, start, length);
   }

}
