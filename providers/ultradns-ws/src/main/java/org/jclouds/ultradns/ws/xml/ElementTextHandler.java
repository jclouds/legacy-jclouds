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
package org.jclouds.ultradns.ws.xml;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.http.functions.ParseSax;

/**
 * 
 * @author Adrian Cole
 */
public abstract class ElementTextHandler extends ParseSax.HandlerForGeneratedRequestWithResult<String> {

   public static class Guid extends ElementTextHandler {
      public Guid() {
         super("guid");
      }
   }

   public static class RRPoolID extends ElementTextHandler {
      public RRPoolID() {
         super("RRPoolID");
      }
   }

   public static class TCPoolID extends ElementTextHandler {
      public TCPoolID() {
         super("TCPoolID");
      }
   }

   public static class PoolRecordID extends ElementTextHandler {
      public PoolRecordID() {
         super("poolRecordID");
      }
   }

   public static class DirPoolID extends ElementTextHandler {
      public DirPoolID() {
         super("DirPoolID");
      }
   }

   public static class DirectionalPoolRecordID extends ElementTextHandler {
      public DirectionalPoolRecordID() {
         super("DirectionalPoolRecordID");
      }
   }

   private final String textElement;

   private StringBuilder currentText = new StringBuilder();
   private String text = null;

   private ElementTextHandler(String textElement) {
      this.textElement = checkNotNull(textElement, "textElement");
   }

   @Override
   public String getResult() {
      return checkNotNull(text, "%s not present in the response", textElement);
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
