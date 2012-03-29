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
package org.jclouds.ec2.xml;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.PasswordData;
import org.jclouds.http.functions.ParseSax;

/**
 * @author Richard Downer
 */
public class GetPasswordDataResponseHandler extends ParseSax.HandlerWithResult<PasswordData> {

   private StringBuilder currentText = new StringBuilder();
   private PasswordData.Builder builder = PasswordData.builder();
   @Inject protected DateService dateService;

   @Override
   public PasswordData getResult() {
      return builder.build();
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("requestId")) {
         builder.requestId(currentText.toString().trim());
      } else if (qName.equals("instanceId")) {
         builder.instanceId(currentText.toString().trim());
      } else if (qName.equals("timestamp")) {
         builder.timestamp(dateService.iso8601DateParse(currentText.toString().trim()));
      } else if (qName.equals("passwordData")) {
         builder.passwordData(currentText.toString().trim());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
