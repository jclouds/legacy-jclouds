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

import java.util.Set;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rds.domain.Instance;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference/API_DescribeInstances.html"
 *      >docs</a>
 * 
 * @author Adrian Cole
 */
public class DescribeDBInstancesResultHandler extends
         ParseSax.HandlerForGeneratedRequestWithResult<IterableWithMarker<Instance>> {

   private final InstanceHandler instanceHandler;

   private StringBuilder currentText = new StringBuilder();
   private Set<Instance> instances = Sets.newLinkedHashSet();
   private boolean inInstances;
   private String marker;

   @Inject
   public DescribeDBInstancesResultHandler(InstanceHandler instanceHandler) {
      this.instanceHandler = instanceHandler;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IterableWithMarker<Instance> getResult() {
      return IterableWithMarkers.from(instances, marker);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "DBInstances")) {
         inInstances = true;
      }
      if (inInstances) {
         instanceHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "DBInstances")) {
         inInstances = false;
      } else if (equalsOrSuffix(qName, "DBInstance")) {
         instances.add(instanceHandler.getResult());
      } else if (equalsOrSuffix(qName, "Marker")) {
         marker = currentOrNull(currentText);
      } else if (inInstances) {
         instanceHandler.endElement(uri, name, qName);
      }

      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inInstances) {
         instanceHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
