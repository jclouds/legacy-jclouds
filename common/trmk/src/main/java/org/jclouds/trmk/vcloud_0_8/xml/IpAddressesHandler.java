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
package org.jclouds.trmk.vcloud_0_8.xml;

import java.util.Set;

import javax.annotation.Resource;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.domain.IpAddress;
import org.jclouds.trmk.vcloud_0_8.domain.IpAddress.Status;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class IpAddressesHandler extends ParseSax.HandlerWithResult<Set<IpAddress>> {
   protected StringBuilder currentText = new StringBuilder();

   @Resource
   protected Logger logger = Logger.NULL;

   private Set<IpAddress> addresses = Sets.newLinkedHashSet();
   private String address;
   private Status status;
   @Nullable
   private String server;
   private boolean skip;

   public Set<IpAddress> getResult() {
      return addresses;
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (attributes.getIndex("xsi:nil") != -1) {
         skip = true;
         return;
      } else {
         skip = false;
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      String current = currentOrNull();
      if (current != null) {
         if (qName.equals("Name")) {
            address = current;
         } else if (qName.equals("Status")) {
            status = IpAddress.Status.fromValue(current);
         } else if (!skip && qName.equals("Server")) {
            server = current;
         }
      } else if (qName.equals("IpAddress")) {
         addresses.add(new IpAddress(address, status, server));
         address = null;
         status = null;
         server = null;
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

}
