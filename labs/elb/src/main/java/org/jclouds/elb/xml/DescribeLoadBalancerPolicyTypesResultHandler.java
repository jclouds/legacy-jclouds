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

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Set;

import org.jclouds.elb.domain.PolicyType;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_DescribeLoadBalancerPolicyTypeTypes.html"
 *      >docs</a>
 * 
 * @author Adrian Cole
 */
public class DescribeLoadBalancerPolicyTypesResultHandler extends
         ParseSax.HandlerForGeneratedRequestWithResult<Set<PolicyType>> {

   private final PolicyTypeHandler policyTypeHandler;

   private StringBuilder currentText = new StringBuilder();
   private Builder<PolicyType> policyTypes = ImmutableSet.<PolicyType> builder();
   private boolean inPolicyTypeTypes;

   protected int memberDepth;

   @Inject
   public DescribeLoadBalancerPolicyTypesResultHandler(PolicyTypeHandler policyTypeHandler) {
      this.policyTypeHandler = policyTypeHandler;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<PolicyType> getResult() {
      return policyTypes.build();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "member")) {
         memberDepth++;
      } else if (equalsOrSuffix(qName, "PolicyTypeDescriptions")) {
         inPolicyTypeTypes = true;
      }
      if (inPolicyTypeTypes) {
         policyTypeHandler.startElement(url, name, qName, attributes);
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
      } else if (equalsOrSuffix(qName, "PolicyTypeDescriptions")) {
         inPolicyTypeTypes = false;
      } else if (inPolicyTypeTypes) {
         policyTypeHandler.endElement(uri, name, qName);
      }

      currentText = new StringBuilder();
   }

   protected void endMember(String uri, String name, String qName) throws SAXException {
      if (inPolicyTypeTypes) {
         if (memberDepth == 1)
            policyTypes.add(policyTypeHandler.getResult());
         else
            policyTypeHandler.endElement(uri, name, qName);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inPolicyTypeTypes) {
         policyTypeHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
