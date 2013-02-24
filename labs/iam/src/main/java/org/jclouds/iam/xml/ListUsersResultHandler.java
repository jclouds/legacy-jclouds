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

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.iam.domain.User;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.inject.Inject;

/**
 * @see <a href="http://docs.amazonwebservices.com/IAM/latest/APIReference/API_ListUsers.html" />
 *
 * @author Adrian Cole
 */
public class ListUsersResultHandler extends ParseSax.HandlerForGeneratedRequestWithResult<IterableWithMarker<User>> {

   private final UserHandler userHandler;

   private StringBuilder currentText = new StringBuilder();
   private Builder<User> users = ImmutableList.<User> builder();
   private boolean inUsers;
   private String afterMarker;

   @Inject
   public ListUsersResultHandler(UserHandler userHandler) {
      this.userHandler = userHandler;
   }

   @Override
   public IterableWithMarker<User> getResult() {
      try {
         return IterableWithMarkers.from(users.build(), afterMarker);
      } finally {
         users = ImmutableList.<User> builder();
      }
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
      if (equalsOrSuffix(qName, "Users")) {
         inUsers = true;
      }
      if (inUsers) {
         userHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (inUsers) {
         if (qName.equals("Users")) {
            inUsers = false;
         } else if (qName.equals("member")) {
            users.add(userHandler.getResult());
         } else {
            userHandler.endElement(uri, name, qName);
         }
      } else if (qName.equals("Marker")) {
         afterMarker = currentOrNull(currentText);
      }

      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inUsers) {
         userHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
