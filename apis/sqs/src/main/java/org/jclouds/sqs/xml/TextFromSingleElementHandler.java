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
package org.jclouds.sqs.xml;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.SaxUtils.currentOrNull;

import org.jclouds.http.functions.ParseSax;

import com.google.common.base.Function;

/**
 * looks for a single value in the xml
 * 
 * @author Adrian Cole
 */
public abstract class TextFromSingleElementHandler<V> extends ParseSax.HandlerForGeneratedRequestWithResult<V>
      implements Function<String, V> {
   private final String elementName;

   protected TextFromSingleElementHandler(String elementName) {
      this.elementName = checkNotNull(elementName, "elementName");
   }

   private StringBuilder currentText = new StringBuilder();
   private String text;

   @Override
   public V getResult() {
      return apply(text);
   }

   // this could be done with regex, if we had an unescaper
   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals(elementName)) {
         text = currentOrNull(currentText);
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
