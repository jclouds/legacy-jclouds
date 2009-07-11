/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.http.functions.config;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jclouds.http.functions.ParseSax;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class SaxModule extends AbstractModule {
   private final static TypeLiteral<ParseSax.Factory> parseSaxFactoryLiteral = new TypeLiteral<ParseSax.Factory>() {
   };

   @Provides
   XMLReader provideXMLReader(SAXParserFactory factory) throws ParserConfigurationException,
            SAXException {
      SAXParser saxParser = factory.newSAXParser();
      XMLReader parser = saxParser.getXMLReader();
      return parser;
   }

   @Provides
   SAXParserFactory provideSAXParserFactory() {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(false);
      factory.setValidating(false);
      factory.setXIncludeAware(false);
      return factory;
   }

   protected void configure() {
      bind(parseSaxFactoryLiteral).toProvider(
               FactoryProvider.newFactory(parseSaxFactoryLiteral, new TypeLiteral<ParseSax<?>>() {
               }));
   }
}
