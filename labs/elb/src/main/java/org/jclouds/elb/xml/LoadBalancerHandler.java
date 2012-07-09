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
import org.jclouds.elb.domain.Scheme;
import org.jclouds.elb.domain.SecurityGroupAndOwner;
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
   protected final DateService dateService;
   protected final HealthCheckHandler healthCheckHandler;
   protected final ListenerWithPoliciesHandler listenerHandler;

   @Inject
   protected LoadBalancerHandler(DateService dateService, HealthCheckHandler healthCheckHandler, ListenerWithPoliciesHandler listenerHandler) {
      this.dateService = dateService;
      this.healthCheckHandler = healthCheckHandler;
      this.listenerHandler = listenerHandler;
   }

   private StringBuilder currentText = new StringBuilder();
   private LoadBalancer.Builder<?> builder = LoadBalancer.builder();
   private SecurityGroupAndOwner.Builder sourceSecurityGroupBuilder;

   private boolean inHealthCheck;
   private boolean inListeners;
   private boolean inAvailabilityZones;
   private boolean inSecurityGroups;
   private boolean inSubnets;

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
      } else if (equalsOrSuffix(qName, "HealthCheck")) {
         inHealthCheck = true;
      } else if (equalsOrSuffix(qName, "ListenerDescriptions")) {
         inListeners = true;
      } else if (equalsOrSuffix(qName, "AvailabilityZones")) {
         inAvailabilityZones = true;
      } else if (equalsOrSuffix(qName, "SecurityGroups")) {
         inSecurityGroups = true;
      } else if (equalsOrSuffix(qName, "Subnets")) {
         inSubnets = true;
      } else if (equalsOrSuffix(qName, "SourceSecurityGroup")) {
         sourceSecurityGroupBuilder = SecurityGroupAndOwner.builder();
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
      } else if (equalsOrSuffix(qName, "AvailabilityZones")) {
         inAvailabilityZones = false;
      } else if (equalsOrSuffix(qName, "SecurityGroups")) {
         inSecurityGroups = false;
      } else if (equalsOrSuffix(qName, "Subnets")) {
         inSubnets = false;         
      } else if (equalsOrSuffix(qName, "HealthCheck")) {
         builder.healthCheck(healthCheckHandler.getResult());
         inHealthCheck = false;
      } else if (equalsOrSuffix(qName, "SourceSecurityGroup")) {
         if (sourceSecurityGroupBuilder != null)
            builder.sourceSecurityGroup(sourceSecurityGroupBuilder.build());
         sourceSecurityGroupBuilder = null;
      } else if (equalsOrSuffix(qName, "LoadBalancerName")) {
         builder.name(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "CreatedTime")) {
         builder.createdTime(dateService.iso8601DateParse(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "DNSName")) {
         builder.dnsName(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "InstanceId")) {
         builder.instanceId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "GroupName")) {
         sourceSecurityGroupBuilder.name(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "OwnerAlias")) {
         sourceSecurityGroupBuilder.owner(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Scheme")) {
         builder.scheme(Scheme.fromValue(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "VPCId")) {
         builder.VPCId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "CanonicalHostedZoneName")) {
         builder.hostedZoneName(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "CanonicalHostedZoneNameID")) {
         builder.hostedZoneId(currentOrNull(currentText));
      } else if (inHealthCheck) {
         healthCheckHandler.endElement(uri, name, qName);
      } else if (inListeners) {
         listenerHandler.endElement(uri, name, qName);
      }
      currentText = new StringBuilder();
   }

   protected void endMember(String uri, String name, String qName) throws SAXException {
      if (inListeners) {
         if (memberDepth == 2)
            builder.listener(listenerHandler.getResult());
         else
            listenerHandler.endElement(uri, name, qName);
      } else if (inAvailabilityZones) {
         builder.availabilityZone(currentOrNull(currentText));
      } else if (inSecurityGroups) {
         builder.securityGroup(currentOrNull(currentText));
      } else if (inSubnets) {
         builder.subnet(currentOrNull(currentText));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inListeners) {
         listenerHandler.characters(ch, start, length);
      } else if (inHealthCheck) {
         healthCheckHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
