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
package org.jclouds.iam.xml;

import java.util.Set;

import org.jclouds.collect.PaginatedSet;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.iam.domain.User;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @see <a href="http://docs.amazonwebservices.com/IAM/latest/APIReference/API_ListUsers.html" />
 *
 * @author Adrian Cole
 */
public class ListUsersResultHandler extends ParseSax.HandlerForGeneratedRequestWithResult<PaginatedSet<User>> {

   private final UserHandler userHandler;

   private StringBuilder currentText = new StringBuilder();
   private Set<User> users = Sets.newLinkedHashSet();
   private boolean inUsers;
   private String marker;

   @Inject
   public ListUsersResultHandler(UserHandler userHandler) {
      this.userHandler = userHandler;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public PaginatedSet<User> getResult() {
      return PaginatedSet.copyOfWithMarker(users, marker);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (SaxUtils.equalsOrSuffix(qName, "Users")) {
         inUsers = true;
      }
      if (inUsers) {
         userHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (inUsers) {
         if (qName.equals("Users")) {
            inUsers = false;
         } else if (qName.equals("member")) {
            users.add(userHandler.getResult());
         } else {
            userHandler.endElement(uri, name, qName);
         }
      } else if (qName.equals("Marker")) {
         marker = SaxUtils.currentOrNull(currentText);
      }

      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inUsers) {
         userHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
