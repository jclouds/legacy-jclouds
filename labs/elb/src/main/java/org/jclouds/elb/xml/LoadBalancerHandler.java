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

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.domain.LoadBalancer.Scheme;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_GetLoadBalancer.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class LoadBalancerHandler extends ParseSax.HandlerForGeneratedRequestWithResult<LoadBalancer> {
   private final DateService dateService;
   private final ListenerWithPoliciesHandler listenerHandler;

   @Inject
   protected LoadBalancerHandler(DateService dateService, ListenerWithPoliciesHandler listenerHandler) {
      this.dateService = dateService;
      this.listenerHandler = listenerHandler;
   }

   private StringBuilder currentText = new StringBuilder();
   private LoadBalancer.Builder<?> builder = LoadBalancer.builder();

   private boolean inListeners;

   protected int memberDepth;

   /**
    * {@inheritDoc}
    */
   @Override
   public LoadBalancer getResult() {
      try {
         return builder.build();
      } finally {
         builder = LoadBalancer.builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "member")) {
         memberDepth++;
      } else if (equalsOrSuffix(qName, "ListenerDescriptions")) {
         inListeners = true;
      }
      if (inListeners) {
         listenerHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "member")) {
         endMember(uri, name, qName);
         memberDepth--;
      } else if (equalsOrSuffix(qName, "ListenerDescriptions")) {
         inListeners = false;
      } else if (equalsOrSuffix(qName, "LoadBalancerName")) {
         builder.name(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "CreatedTime")) {
         builder.createdTime(dateService.iso8601DateParse(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "DNSName")) {
         builder.dnsName(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "InstanceId")) {
         builder.instanceId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Scheme")) {
         builder.scheme(Scheme.fromValue(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "VPCId")) {
         builder.VPCId(currentOrNull(currentText));
      } else if (inListeners) {
         listenerHandler.endElement(uri, name, qName);
      }
      currentText = new StringBuilder();
   }

   protected void endMember(String uri, String name, String qName) throws SAXException {
      if (inListeners) {
         builder.listener(listenerHandler.getResult());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inListeners) {
         listenerHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
