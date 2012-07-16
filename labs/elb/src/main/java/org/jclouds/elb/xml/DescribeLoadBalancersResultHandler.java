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
package org.jclouds.elb.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Set;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_DescribeLoadBalancers.html"
 *      >docs</a>
 * 
 * @author Adrian Cole
 */
public class DescribeLoadBalancersResultHandler extends
         ParseSax.HandlerForGeneratedRequestWithResult<IterableWithMarker<LoadBalancer>> {

   private final LoadBalancerHandler loadBalancerHandler;

   private StringBuilder currentText = new StringBuilder();
   private Set<LoadBalancer> loadBalancers = Sets.newLinkedHashSet();
   private boolean inLoadBalancers;
   private String marker;

   protected int memberDepth;

   @Inject
   public DescribeLoadBalancersResultHandler(LoadBalancerHandler loadBalancerHandler) {
      this.loadBalancerHandler = loadBalancerHandler;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IterableWithMarker<LoadBalancer> getResult() {
      return IterableWithMarkers.from(loadBalancers, marker);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "member")) {
         memberDepth++;
      } else if (equalsOrSuffix(qName, "LoadBalancerDescriptions")) {
         inLoadBalancers = true;
      }
      if (inLoadBalancers) {
         loadBalancerHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "member")) {
         endMember( uri,  name,  qName);
         memberDepth--;
      } else if (equalsOrSuffix(qName, "LoadBalancerDescriptions")) {
         inLoadBalancers = false;
      } else if (equalsOrSuffix(qName, "Marker")) {
         marker = currentOrNull(currentText);
      } else if (inLoadBalancers) {
         loadBalancerHandler.endElement(uri, name, qName);
      }

      currentText = new StringBuilder();
   }

   protected void endMember(String uri, String name, String qName) throws SAXException {
      if (inLoadBalancers) {
         if (memberDepth == 1)
            loadBalancers.add(loadBalancerHandler.getResult());
         else
            loadBalancerHandler.endElement(uri, name, qName);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inLoadBalancers) {
         loadBalancerHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
