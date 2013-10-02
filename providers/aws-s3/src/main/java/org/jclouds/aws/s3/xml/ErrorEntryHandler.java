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
package org.jclouds.aws.s3.xml;

import com.google.common.collect.Maps;
import org.jclouds.aws.s3.domain.DeleteResult;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.SAXException;

import java.util.Map;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

/**
 * @author Andrei Savu
 */
public class ErrorEntryHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Map.Entry<String, DeleteResult.Error>> {

   private StringBuilder accumulator = new StringBuilder();

   private String key;
   private String code;
   private String message;

   @Override
   public void characters(char[] chars, int start, int length) throws SAXException {
      accumulator.append(chars, start, length);
   }

   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "Key")) {
         key = accumulator.toString().trim();
      } else if (equalsOrSuffix(qName, "Code")) {
         code = accumulator.toString().trim();
      } else if (equalsOrSuffix(qName, "Message")) {
         message = accumulator.toString().trim();
      }
      accumulator = new StringBuilder();
   }

   @Override
   public Map.Entry<String, DeleteResult.Error> getResult() {
      return Maps.immutableEntry(key, new DeleteResult.Error(code, message));
   }
}
