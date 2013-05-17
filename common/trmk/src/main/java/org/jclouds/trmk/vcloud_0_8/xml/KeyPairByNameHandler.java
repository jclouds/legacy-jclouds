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

import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
public class KeyPairByNameHandler extends ParseSax.HandlerForGeneratedRequestWithResult<KeyPair> {

   @Resource
   protected Logger logger = Logger.NULL;
   private final KeyPairsHandler handler;

   @Inject
   public KeyPairByNameHandler(KeyPairsHandler handler) {
      this.handler = handler;
   }

   @Override
   public KeyPair getResult() {
      final String name = getRequest().getInvocation().getArgs().get(1).toString();
      try {
         return Iterables.find(handler.getResult(), new Predicate<KeyPair>() {

            @Override
            public boolean apply(KeyPair input) {
               return input.getName().equals(name);
            }

         });
      } catch (NoSuchElementException e) {
         logger.debug("keypair %s/%s not found in %s", getRequest().getInvocation().getArgs().get(0), name,
               handler.getResult());
         return null;
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      handler.startElement(uri, localName, qName, attributes);
   }

   public void endElement(String uri, String name, String qName) {
      handler.endElement(uri, name, qName);
   }

   public void characters(char ch[], int start, int length) {
      handler.characters(ch, start, length);
   }

}
