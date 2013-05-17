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
package org.jclouds.route53.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;

import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 */
public class InvalidChangeBatchHandler extends ParseSax.HandlerForGeneratedRequestWithResult<ImmutableList<String>> {

   private StringBuilder currentText = new StringBuilder();
   private ImmutableList.Builder<String> builder = ImmutableList.builder();

   @Override
   public ImmutableList<String> getResult() {
      return builder.build();
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Message")) {
         builder.add(currentOrNull(currentText));
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
