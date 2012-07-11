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
import org.jclouds.rds.domain.SubnetGroup;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference/API_DBSubnetGroup.html"
 *      >xml</a>
 * 
 * @author Adrian Cole
 */
public class SubnetGroupHandler extends ParseSax.HandlerForGeneratedRequestWithResult<SubnetGroup> {
   protected final SubnetHandler subnetHandler;

   @Inject
   protected SubnetGroupHandler(SubnetHandler subnetHandler) {
      this.subnetHandler = subnetHandler;
   }

   private StringBuilder currentText = new StringBuilder();
   private SubnetGroup.Builder<?> builder = SubnetGroup.builder();

   private boolean inSubnets;

   /**
    * {@inheritDoc}
    */
   @Override
   public SubnetGroup getResult() {
      try {
         return builder.build();
      } finally {
         builder = SubnetGroup.builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "Subnets")) {
         inSubnets = true;
      }
      if (inSubnets) {
         subnetHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "Subnets")) {
         inSubnets = false;
      } else if (equalsOrSuffix(qName, "DBSubnetGroupName")) {
         builder.name(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "DBSubnetGroupDescription")) {
         builder.description(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "SubnetGroupStatus")) {
         builder.status(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Subnet")) {
         builder.subnet(subnetHandler.getResult());
      } else if (equalsOrSuffix(qName, "VpcId")) {
         builder.vpcId(currentOrNull(currentText));
      } else if (inSubnets) {
         subnetHandler.endElement(uri, name, qName);
      }
      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inSubnets) {
         subnetHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
