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
package org.jclouds.sts.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.aws.xml.TemporaryCredentialsHandler;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.sts.domain.User;
import org.jclouds.sts.domain.UserAndTemporaryCredentials;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
public class UserAndTemporaryCredentialsHandler extends ParseSax.HandlerForGeneratedRequestWithResult<UserAndTemporaryCredentials> {

   private final TemporaryCredentialsHandler credsHandler;

   private StringBuilder currentText = new StringBuilder();
   private UserAndTemporaryCredentials.Builder builder = UserAndTemporaryCredentials.builder();

   @Inject
   public UserAndTemporaryCredentialsHandler(TemporaryCredentialsHandler credsHandler) {
      this.credsHandler = credsHandler;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public UserAndTemporaryCredentials getResult() {
      try {
         return builder.build();
      } finally {
         builder = UserAndTemporaryCredentials.builder();
      }
   }

   private boolean inCreds;

   private String arn;
   private String id;

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "Credentials")) {
         inCreds = true;
      }
      if (inCreds) {
         credsHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (inCreds) {
         if (qName.equals("Credentials")) {
            inCreds = false;
            builder.credentials(credsHandler.getResult());
         } else {
            credsHandler.endElement(uri, name, qName);
         }
      } else if (qName.equals("Arn")) {
         arn = currentOrNull(currentText);
      } else if (qName.endsWith("Id")) {// FederatedUserId or AssumedRoleId
         id = currentOrNull(currentText);
      } else if (qName.endsWith("User")) {// FederatedUser or AssumedRoleUser
         builder.user(User.fromIdAndArn(id, arn));
         id = arn = null;
      } else if (qName.equals("PackedPolicySize")) {
         builder.packedPolicySize(Integer.parseInt(currentOrNull(currentText)));
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inCreds) {
         credsHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
