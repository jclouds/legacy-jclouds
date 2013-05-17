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
package org.jclouds.http.functions.config;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.xml.sax.XMLReader;

import com.google.common.base.Throwables;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Contains logic for parsing objects from Strings.
 * 
 * @author Adrian Cole
 */
public class SaxParserModule extends AbstractModule {

   protected void configure() {
      bind(ParseSax.Factory.class).to(Factory.class).in(Scopes.SINGLETON);
   }

   static class Factory implements ParseSax.Factory {
      private final SAXParserFactory factory;
      private final Injector i;

      @Inject
      Factory(SAXParserFactory factory, Injector i) {
         this.factory = factory;
         this.i = i;
      }

      public <T> ParseSax<T> create(HandlerWithResult<T> handler) {
         SAXParser saxParser;
         try {
            saxParser = factory.newSAXParser();
            XMLReader parser = saxParser.getXMLReader();
            // TODO: switch to @AssistedInject
            ParseSax<T> returnVal = new ParseSax<T>(parser, handler);
            i.injectMembers(returnVal);
            return returnVal;
         } catch (Exception e) {
            throw Throwables.propagate(e);
         }

      }
   }

   @Provides
   @Singleton
   SAXParserFactory provideSAXParserFactory() {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(false);
      factory.setValidating(false);
      return factory;
   }

}
