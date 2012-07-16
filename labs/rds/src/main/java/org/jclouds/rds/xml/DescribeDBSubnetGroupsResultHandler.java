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

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rds.domain.SubnetGroup;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference/API_DescribeDBSubnetGroups.html"
 *      >docs</a>
 * 
 * @author Adrian Cole
 */
public class DescribeDBSubnetGroupsResultHandler extends
         ParseSax.HandlerForGeneratedRequestWithResult<IterableWithMarker<SubnetGroup>> {

   private final SubnetGroupHandler subnetGroupHander;

   private StringBuilder currentText = new StringBuilder();
   private Builder<SubnetGroup> subnetGroups = ImmutableSet.<SubnetGroup> builder();
   private boolean inSubnetGroups;
   private String marker;

   @Inject
   public DescribeDBSubnetGroupsResultHandler(SubnetGroupHandler subnetGroupHander) {
      this.subnetGroupHander = subnetGroupHander;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IterableWithMarker<SubnetGroup> getResult() {
      return IterableWithMarkers.from(subnetGroups.build(), marker);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "DBSubnetGroups")) {
         inSubnetGroups = true;
      }
      if (inSubnetGroups) {
         subnetGroupHander.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "DBSubnetGroups")) {
         inSubnetGroups = false;
      } else if (equalsOrSuffix(qName, "DBSubnetGroup")) {
         subnetGroups.add(subnetGroupHander.getResult());
      } else if (equalsOrSuffix(qName, "Marker")) {
         marker = currentOrNull(currentText);
      } else if (inSubnetGroups) {
         subnetGroupHander.endElement(uri, name, qName);
      }
      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inSubnetGroups) {
         subnetGroupHander.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
