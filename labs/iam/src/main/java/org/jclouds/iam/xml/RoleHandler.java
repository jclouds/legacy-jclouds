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
import static org.jclouds.util.Strings2.urlDecode;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.iam.domain.Role;
import org.xml.sax.Attributes;

/**
 * @see <a href="http://docs.aws.amazon.com/IAM/latest/APIReference/API_GetRole.html" />
 * 
 * @author Adrian Cole
 */
public class RoleHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Role> {
   private final DateService dateService;

   @Inject
   protected RoleHandler(DateService dateService) {
      this.dateService = dateService;
   }

   private StringBuilder currentText = new StringBuilder();
   private Role.Builder builder = Role.builder();

   @Override
   public Role getResult() {
      try {
         return builder.build();
      } finally {
         builder = Role.builder();
      }
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Path")) {
         builder.path(currentOrNull(currentText));
      } else if (qName.equals("RoleName")) {
         builder.name(currentOrNull(currentText));
      } else if (qName.equals("RoleId")) {
         builder.id(currentOrNull(currentText));
      } else if (qName.equals("Arn")) {
         builder.arn(currentOrNull(currentText));
      } else if (qName.equals("AssumeRolePolicyDocument")) {
         builder.assumeRolePolicy(urlDecode(currentOrNull(currentText)));
      } else if (qName.equals("CreateDate")) {
         try {
            builder.createDate(dateService.iso8601SecondsDateParse(currentOrNull(currentText)));
         } catch (IllegalArgumentException e) {
            // on create, milliseconds are present
            builder.createDate(dateService.iso8601DateParse(currentOrNull(currentText)));
         }
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
