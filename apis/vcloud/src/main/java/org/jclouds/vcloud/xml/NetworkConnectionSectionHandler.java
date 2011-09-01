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
package org.jclouds.vcloud.xml;

import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.NetworkConnection;
import org.jclouds.vcloud.domain.NetworkConnectionSection;
import org.jclouds.vcloud.domain.ReferenceType;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class NetworkConnectionSectionHandler extends ParseSax.HandlerWithResult<NetworkConnectionSection> {
   protected StringBuilder currentText = new StringBuilder();

   private final NetworkConnectionHandler networkConnectionHandler;

   @Inject
   public NetworkConnectionSectionHandler(NetworkConnectionHandler networkConnectionHandler) {
      this.networkConnectionHandler = networkConnectionHandler;
   }

   protected String info;
   protected Set<NetworkConnection> connections = Sets.newLinkedHashSet();
   protected ReferenceType section;
   protected ReferenceType edit;
   protected Integer primaryNetworkConnectionIndex;
   protected boolean inConnections;

   public NetworkConnectionSection getResult() {
      return new NetworkConnectionSection(section.getType(), section.getHref(), info, primaryNetworkConnectionIndex,
               connections, edit);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.endsWith("NetworkConnection")) {
         inConnections = true;
      }
      if (inConnections) {
         networkConnectionHandler.startElement(uri, localName, qName, attrs);
      } else if (qName.endsWith("NetworkConnectionSection")) {
         section = newReferenceType(attributes);
      } else if (qName.endsWith("Link") && "edit".equals(attributes.get("rel"))) {
         edit = newReferenceType(attributes);
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (qName.endsWith("NetworkConnection")) {
         inConnections = false;
         connections.add(networkConnectionHandler.getResult());
      }
      if (inConnections) {
         networkConnectionHandler.endElement(uri, localName, qName);
      } else if (qName.endsWith("Info")) {
         this.info = currentOrNull();
      } else if (qName.endsWith("PrimaryNetworkConnectionIndex")) {
         this.primaryNetworkConnectionIndex = new Integer(currentOrNull());
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inConnections)
         networkConnectionHandler.characters(ch, start, length);
      else
         currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }
}
