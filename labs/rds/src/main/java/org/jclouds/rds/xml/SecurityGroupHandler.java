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
package org.jclouds.rds.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.rds.domain.SecurityGroup;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference/API_DBSecurityGroup.html"
 *      >xml</a>
 * 
 * @author Adrian Cole
 */
public class SecurityGroupHandler extends ParseSax.HandlerForGeneratedRequestWithResult<SecurityGroup> {
   protected final EC2SecurityGroupHandler ec2SecurityGroupHandler;
   protected final IPRangeHandler ipRangeHandler;

   @Inject
   protected SecurityGroupHandler(EC2SecurityGroupHandler ec2SecurityGroupHandler, IPRangeHandler ipRangeHandler) {
      this.ec2SecurityGroupHandler = ec2SecurityGroupHandler;
      this.ipRangeHandler = ipRangeHandler;
   }

   private StringBuilder currentText = new StringBuilder();
   private SecurityGroup.Builder<?> builder = SecurityGroup.builder();

   private boolean inEC2SecurityGroups;
   private boolean inIPRanges;

   /**
    * {@inheritDoc}
    */
   @Override
   public SecurityGroup getResult() {
      try {
         return builder.build();
      } finally {
         builder = SecurityGroup.builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "EC2SecurityGroups")) {
         inEC2SecurityGroups = true;
      } else if (equalsOrSuffix(qName, "IPRanges")) {
         inIPRanges = true;
      }
      if (inEC2SecurityGroups) {
         ec2SecurityGroupHandler.startElement(url, name, qName, attributes);
      } else if (inIPRanges) {
         ipRangeHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "EC2SecurityGroups")) {
         inEC2SecurityGroups = false;
      } else if (equalsOrSuffix(qName, "IPRanges")) {
         inIPRanges = false;
      } else if (equalsOrSuffix(qName, "DBSecurityGroupName")) {
         builder.name(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "DBSecurityGroupDescription")) {
         builder.description(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "EC2SecurityGroup")) {
         builder.ec2SecurityGroup(ec2SecurityGroupHandler.getResult());
      } else if (equalsOrSuffix(qName, "IPRange")) {
         builder.ipRange(ipRangeHandler.getResult());
      } else if (equalsOrSuffix(qName, "OwnerId")) {
         builder.ownerId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "VpcId")) {
         builder.vpcId(currentOrNull(currentText));
      } else if (inEC2SecurityGroups) {
         ec2SecurityGroupHandler.endElement(uri, name, qName);
      } else if (inIPRanges) {
         ipRangeHandler.endElement(uri, name, qName);
      }
      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inEC2SecurityGroups) {
         ec2SecurityGroupHandler.characters(ch, start, length);
      } else if (inIPRanges) {
         ipRangeHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
