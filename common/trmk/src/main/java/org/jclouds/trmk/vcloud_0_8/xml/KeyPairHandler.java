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

import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;

/**
 * @author Adrian Cole
 */
public class KeyPairHandler extends HandlerWithResult<KeyPair> {

   @Resource
   protected Logger logger = Logger.NULL;
   private StringBuilder currentText = new StringBuilder();

   private URI location;
   private String name;
   private boolean isDefault;
   private String privateKey;
   private String fingerPrint;

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   @Override
   public KeyPair getResult() {
      return new KeyPair(location, name, isDefault, privateKey, fingerPrint);
   }

   public void endElement(String uri, String name, String qName) {
      String current = currentOrNull();
      if (current != null) {
         if (qName.equals("Href")) {
            location = URI.create(current);
         } else if (qName.equals("Name")) {
            this.name = current;
         } else if (qName.equals("IsDefault")) {
            isDefault = Boolean.parseBoolean(current);
         } else if (qName.equals("PrivateKey")) {
            privateKey = current;
         } else if (qName.equals("FingerPrint")) {
            fingerPrint = current;
         }
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
