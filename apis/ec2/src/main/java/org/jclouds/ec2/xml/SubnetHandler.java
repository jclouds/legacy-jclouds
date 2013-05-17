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

import javax.inject.Inject;

import org.jclouds.ec2.domain.Subnet;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

/**
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/ApiReference-ItemType-SubnetType.html" >xml</a>
 * 
 * @author Adrian Cole
 * @author Andrew Bayer
 */
public class SubnetHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Subnet> {
   private StringBuilder currentText = new StringBuilder();
   private Subnet.Builder builder = Subnet.builder();
   private final TagSetHandler tagSetHandler;
   private boolean inTagSet;

   @Inject
   public SubnetHandler(TagSetHandler tagSetHandler) {
      this.tagSetHandler = tagSetHandler;
   }

   @Override
   public Subnet getResult() {
      try {
         return builder.build();
      } finally {
         builder = Subnet.builder();
      }
   }

   @Override
   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = true;
      }
      if (inTagSet) {
         tagSetHandler.startElement(uri, name, qName, attrs);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = false;
         builder.tags(tagSetHandler.getResult());
      } else if (inTagSet) {
         tagSetHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "subnetId")) {
         builder.subnetId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "state")) {
         builder.subnetState(Subnet.State.fromValue(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "vpcId")) {
         builder.vpcId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "cidrBlock")) {
         builder.cidrBlock(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "availableIpAddressCount")) {
         builder.availableIpAddressCount(Integer.parseInt(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "availabilityZone")) {
         builder.availabilityZone(currentOrNull(currentText));
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inTagSet) {
         tagSetHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
