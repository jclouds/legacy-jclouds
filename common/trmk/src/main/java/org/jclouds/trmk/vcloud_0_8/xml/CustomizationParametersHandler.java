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
package org.jclouds.trmk.vcloud_0_8.xml;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.trmk.vcloud_0_8.domain.CustomizationParameters;

/**
 * @author Adrian Cole
 */
public class CustomizationParametersHandler extends HandlerWithResult<CustomizationParameters> {

   private StringBuilder currentText = new StringBuilder();
   boolean customizeNetwork;
   boolean customizePassword;
   boolean customizeSSH;

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   @Override
   public CustomizationParameters getResult() {
      return new CustomizationParameters(customizeNetwork, customizePassword, customizeSSH);
   }

   public void endElement(String uri, String name, String qName) {
      String current = currentOrNull();
      if (current != null) {
         if (qName.equals("CustomizeNetwork")) {
            customizeNetwork = Boolean.parseBoolean(current);
         } else if (qName.equals("CustomizePassword")) {
            customizePassword = Boolean.parseBoolean(current);
         } else if (qName.equals("CustomizeSSH")) {
            customizeSSH = Boolean.parseBoolean(current);
         }
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
