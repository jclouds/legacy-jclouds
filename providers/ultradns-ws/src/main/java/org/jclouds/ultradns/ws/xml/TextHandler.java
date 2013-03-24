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
package org.jclouds.ultradns.ws.xml;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.http.functions.ParseSax;

/**
 * 
 * @author Adrian Cole
 */
public abstract class TextHandler extends ParseSax.HandlerForGeneratedRequestWithResult<String> {

   public static class Guid extends TextHandler {
      public Guid() {
         super("guid");
      }
   }

   public static class RRPoolID extends TextHandler {
      public RRPoolID() {
         super("RRPoolID");
      }
   }

   public static class TCPoolID extends TextHandler {
      public TCPoolID() {
         super("TCPoolID");
      }
   }

   public static class PoolRecordID extends TextHandler {
      public PoolRecordID() {
         super("poolRecordID");
      }
   }

   private String textElement;

   private StringBuilder currentText = new StringBuilder();
   private String text = null;

   private TextHandler(String textElement) {
      this.textElement = checkNotNull(textElement, "textElement");
   }

   @Override
   public String getResult() {
      try {
         return checkNotNull(text, "%s not present in the response", textElement);
      } finally {
         text = null;
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, textElement)) {
         text = currentOrNull(currentText);
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
