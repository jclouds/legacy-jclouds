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

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.HandlerForGeneratedRequestWithResult;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Parses: DescribeSecurityGroupsResponse
 * xmlns="http://ec2.amazonaws.com/doc/2010-06-15/"
 *
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-query-DescribesecurityGroupInfo.html"
 *      />
 * @author Adrian Cole
 */
public class DescribeSecurityGroupsResponseHandler extends
      ParseSax.HandlerForGeneratedRequestWithResult<Set<SecurityGroup>> {

   private final SecurityGroupHandler securityGroupHandler;

   private StringBuilder currentText = new StringBuilder();
   private Builder<SecurityGroup> securityGroups = ImmutableSet.<SecurityGroup> builder();
   private boolean inSecurityGroupInfo;

   protected int itemDepth;

   @Inject
   public DescribeSecurityGroupsResponseHandler(SecurityGroupHandler securityGroupHandler) {
      this.securityGroupHandler = securityGroupHandler;
   }

   @Override
   public HandlerForGeneratedRequestWithResult<Set<SecurityGroup>> setContext(HttpRequest request) {
      securityGroupHandler.setContext(request);
      return super.setContext(request);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<SecurityGroup> getResult() {
      return securityGroups.build();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "item")) {
         itemDepth++;
      } else if (equalsOrSuffix(qName, "securityGroupInfo")) {
         inSecurityGroupInfo = true;
      }
      if (inSecurityGroupInfo) {
         securityGroupHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "item")) {
         endItem(uri, name, qName);
         itemDepth--;
      } else if (equalsOrSuffix(qName, "securityGroupInfo")) {
         inSecurityGroupInfo = false;
      } else if (inSecurityGroupInfo) {
         securityGroupHandler.endElement(uri, name, qName);
      }
      currentText = new StringBuilder();
   }

   protected void endItem(String uri, String name, String qName) throws SAXException {
      if (inSecurityGroupInfo) {
         if (itemDepth == 1)
            securityGroups.add(securityGroupHandler.getResult());
         else
            securityGroupHandler.endElement(uri, name, qName);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inSecurityGroupInfo) {
         securityGroupHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
