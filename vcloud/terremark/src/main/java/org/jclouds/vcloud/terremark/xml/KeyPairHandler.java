/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.terremark.xml;

import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.terremark.domain.KeyPair;

/**
 * @author Adrian Cole
 */
public class KeyPairHandler extends HandlerWithResult<KeyPair> {

   @Resource
   protected Logger logger = Logger.NULL;
   private StringBuilder currentText = new StringBuilder();

   private int id;
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
      return new KeyPair(id, location, name, isDefault, privateKey, fingerPrint);
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Id")) {
         id = Integer.parseInt(currentOrNull());
      } else if (qName.equals("Href") && currentOrNull() != null) {
         location = URI.create(currentOrNull());
      } else if (qName.equals("Name")) {
         this.name = currentOrNull();
      } else if (qName.equals("IsDefault")) {
         isDefault = Boolean.parseBoolean(currentOrNull());
      } else if (qName.equals("PrivateKey")) {
         privateKey = currentOrNull();
      } else if (qName.equals("FingerPrint")) {
         fingerPrint = currentOrNull();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}