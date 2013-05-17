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

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @author Adrian Cole
 */
public class InternetServicesHandler extends HandlerWithResult<Set<InternetService>> {

   private final InternetServiceHandler handler;
   private Builder<InternetService> builder = ImmutableSet.builder();

   @Inject
   public InternetServicesHandler(InternetServiceHandler handler) {
      this.handler = handler;
   }

   @Override
   public Set<InternetService> getResult() {
      try {
         return builder.build();
      } finally {
         builder = ImmutableSet.<InternetService> builder();
      }
   }

   int depth;
   private boolean inInternetService;

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      depth++;
      if (depth == 2) {
         if (equalsOrSuffix(qName, "InternetService")) {
            inInternetService = true;
         }
      } else if (inInternetService) {
         handler.startElement(uri, localName, qName, attrs);
      }
   }

   public void endElement(String uri, String name, String qName) {
      depth--;
      if (depth == 1) {
         if (equalsOrSuffix(qName, "InternetService")) {
            inInternetService = false;
            builder.add(handler.getResult());
         }
      } else if (inInternetService) {
         handler.endElement(uri, name, qName);
      }
   }

   public void characters(char ch[], int start, int length) {
      if (inInternetService) {
         handler.characters(ch, start, length);
      }
   }

}
