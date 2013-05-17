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

import static org.jclouds.util.SaxUtils.currentOrNull;

import java.net.URI;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.trmk.vcloud_0_8.domain.ComputePoolReference;
import org.jclouds.trmk.vcloud_0_8.domain.NetworkAdapter;
import org.jclouds.trmk.vcloud_0_8.domain.Subnet;
import org.jclouds.trmk.vcloud_0_8.domain.VAppExtendedInfo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * @author Seshu Pasam, Adrian Cole
 */
public class VAppExtendedInfoHandler extends HandlerWithResult<VAppExtendedInfo> {

   private StringBuilder currentText = new StringBuilder();

   private VAppExtendedInfo.Builder builder = VAppExtendedInfo.builder();
   private NetworkAdapter.Builder adapterBuilder = NetworkAdapter.builder();
   private Subnet.Builder subnetBuilder = Subnet.builder();
   private ComputePoolReference.Builder poolBuilder = ComputePoolReference.builder();
   private boolean inAdapters;
   private boolean inSubnet;
   private boolean inComputePool;

   @Override
   public VAppExtendedInfo getResult() {
      return builder.build();
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (qName.equals("NetworkAdapters")) {
         inAdapters = true;
      } else if (qName.equals("Subnet")) {
         inSubnet = true;
      } else if (qName.equals("ComputePoolReference")) {
         inComputePool = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      String current = currentOrNull(currentText);
      if (current != null) {
         if (qName.equals("Id")) {
            builder.id(current);
         } else if (qName.equals("Tags")) {
            builder.tags(ImmutableList.copyOf(Splitter.on(',').split(current)));
         } else if (qName.equals("LongName")) {
            builder.longName(current);
         } else if (qName.equals("Href")) {
            URI href = URI.create(current);
            if (inSubnet) {
               subnetBuilder.href(href);
            } else if (inComputePool) {
               poolBuilder.href(href);
            } else {
               builder.href(href);
            }
         } else if (qName.equals("Name")) {
            if (inSubnet) {
               subnetBuilder.name(current);
            } else if (inAdapters) {
               adapterBuilder.name(current);
            } else if (inComputePool) {
               poolBuilder.name(current);
            } else {
               builder.name(current);
            }
         } else if (qName.equals("MacAddress")) {
            adapterBuilder.macAddress(current);
         }
      } else if (qName.equals("NetworkAdapters")) {
         inAdapters = false;
      } else if (qName.equals("NetworkAdapter")) {
         builder.networkAdapter(adapterBuilder.build());
         adapterBuilder = NetworkAdapter.builder();
      } else if (qName.equals("Subnet")) {
         adapterBuilder.subnet(subnetBuilder.build());
         subnetBuilder = Subnet.builder();
         inSubnet = false;
      } else if (qName.equals("ComputePoolReference")) {
         builder.computePoolReference(poolBuilder.build());
         poolBuilder = ComputePoolReference.builder();
         inComputePool = false;
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
