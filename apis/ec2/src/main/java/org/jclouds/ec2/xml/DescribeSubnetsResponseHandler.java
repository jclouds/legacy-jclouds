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

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.ec2.domain.Subnet;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * @see <a href="http://docs.aws.amazon.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSubnets.html" >xml</a>
 * 
 * @author Adrian Cole
 * @author Andrew Bayer
 */
public class DescribeSubnetsResponseHandler extends
      ParseSax.HandlerForGeneratedRequestWithResult<FluentIterable<Subnet>> {
   private final SubnetHandler subnetHandler;

   private StringBuilder currentText = new StringBuilder();
   private boolean inSubnetSet;
   private boolean inTagSet;
   private Builder<Subnet> subnets = ImmutableSet.<Subnet> builder();

   @Inject
   public DescribeSubnetsResponseHandler(SubnetHandler subnetHandler) {
      this.subnetHandler = subnetHandler;
   }

   @Override
   public FluentIterable<Subnet> getResult() {
      return FluentIterable.from(subnets.build());
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
      if (equalsOrSuffix(qName, "subnetSet")) {
         inSubnetSet = true;
      } else if (inSubnetSet) {
         if (equalsOrSuffix(qName, "tagSet")) {
            inTagSet = true;
         }
         subnetHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "subnetSet")) {
         inSubnetSet = false;
      } else if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = false;
         subnetHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "item") && !inTagSet) {
         subnets.add(subnetHandler.getResult());
      } else if (inSubnetSet) {
         subnetHandler.endElement(uri, name, qName);
      }

      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inSubnetSet) {
         subnetHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
