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
import static org.jclouds.util.SaxUtils.cleanseAttributes;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public abstract class AttributeHandler extends ParseSax.HandlerForGeneratedRequestWithResult<String> {

   public static class PoolName extends AttributeHandler {
      public PoolName() {
         super("PoolName");
      }
   }

   private final String attributeName;
   private String attribute = null;

   private AttributeHandler(String attributeName) {
      this.attributeName = checkNotNull(attributeName, "attributeName");
   }

   @Override
   public String getResult() {
      return checkNotNull(attribute, "%s not present in the response", attributeName);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (attribute == null && attributes.containsKey(attributeName)) {
         attribute = attributes.get(attributeName);
      }
   }
}
