/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.xml;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.annotation.Resource;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.terremark.domain.Node;

/**
 * @author Adrian Cole
 */
public class NodeHandler extends HandlerWithResult<Node> {

   @Resource
   protected Logger logger = Logger.NULL;
   private StringBuilder currentText = new StringBuilder();

   private String id;
   private URI location;
   private String serviceName;
   private InetAddress address;
   private int port;
   private String description;
   private boolean enabled;

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   @Override
   public Node getResult() {
      return new Node(id, serviceName, location, address, port, enabled, description);
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Id")) {
         id = currentOrNull();
      } else if (qName.equals("Href") && currentOrNull() != null) {
         location = URI.create(currentOrNull());
      } else if (qName.equals("Name")) {
         serviceName = currentOrNull();
      } else if (qName.equals("Port")) {
         port = Integer.parseInt(currentOrNull());
      } else if (qName.equals("Enabled")) {
         enabled = Boolean.parseBoolean(currentOrNull());
      } else if (qName.equals("IpAddress")) {
         address = parseInetAddress(currentOrNull());
      } else if (qName.equals("Description")) {
         description = currentOrNull();
      }
      currentText = new StringBuilder();
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
         throw new RuntimeException(e);
      }
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}