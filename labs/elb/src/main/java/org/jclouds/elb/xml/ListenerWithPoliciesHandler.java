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

import org.jclouds.elb.domain.ListenerWithPolicies;
import org.jclouds.elb.domain.Protocol;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_ListenerDescription.html"
 *      >xml</a>
 * 
 * @author Adrian Cole
 */
public class ListenerWithPoliciesHandler extends ParseSax.HandlerForGeneratedRequestWithResult<ListenerWithPolicies> {

   private StringBuilder currentText = new StringBuilder();
   private ListenerWithPolicies.Builder<?> builder = ListenerWithPolicies.builder();
   
   private boolean inPolicyNames;

   /**
    * {@inheritDoc}
    */
   @Override
   public ListenerWithPolicies getResult() {
      try {
         return builder.build();
      } finally {
         builder = ListenerWithPolicies.builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "PolicyNames")) {
         inPolicyNames = true;
      }
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "PolicyNames")) {
         inPolicyNames = false;
      } else if (equalsOrSuffix(qName, "member") && inPolicyNames) {
         builder.policyName(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "InstancePort")) {
         builder.instancePort(Integer.parseInt(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "InstanceProtocol")) {
         builder.instanceProtocol(Protocol.valueOf(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "LoadBalancerPort")) {
         builder.port(Integer.parseInt(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "Protocol")) {
         builder.protocol(Protocol.valueOf(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "SSLCertificateId")) {
         builder.SSLCertificateId(currentOrNull(currentText));
      }
      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
