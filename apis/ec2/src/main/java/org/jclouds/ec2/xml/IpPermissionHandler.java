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

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.ec2.domain.IpPermission;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.SAXException;

/**
 *
 * @author Adrian Cole
 */
public class IpPermissionHandler extends ParseSax.HandlerForGeneratedRequestWithResult<IpPermission> {

   private StringBuilder currentText = new StringBuilder();
   private IpPermission.Builder builder = IpPermission.builder();

   /**
    * {@inheritDoc}
    */
   @Override
   public IpPermission getResult() {
      try {
         return builder.build();
      } finally {
         builder = IpPermission.builder();
      }
   }

   private String userId;
   private String groupId;

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "ipProtocol")) {
         // Algorete: ipProtocol can be an empty tag on EC2 clone (e.g.
         // OpenStack EC2)
         builder.ipProtocol(IpProtocol.fromValue(currentOrNegative(currentText)));
      } else if (equalsOrSuffix(qName, "fromPort")) {
         // Algorete: fromPort can be an empty tag on EC2 clone (e.g. OpenStack
         // EC2)
         builder.fromPort(Integer.parseInt(currentOrNegative(currentText)));
      } else if (equalsOrSuffix(qName, "toPort")) {
         // Algorete: toPort can be an empty tag on EC2 clone (e.g. OpenStack
         // EC2)
         builder.toPort(Integer.parseInt(currentOrNegative(currentText)));
      } else if (equalsOrSuffix(qName, "cidrIp")) {
         builder.ipRange(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "userId")) {
         this.userId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "groupName") || equalsOrSuffix(qName, "groupId")) {
         this.groupId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "item")) {
         if (userId != null && groupId != null)
            builder.userIdGroupPair(userId, groupId);
         userId = groupId = null;
      }
      currentText = new StringBuilder();
   }

   private static String currentOrNegative(StringBuilder currentText) {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? "-1" : returnVal;
   }
   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
