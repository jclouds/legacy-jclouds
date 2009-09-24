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

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.DateService;
import org.joda.time.DateTime;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javax.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * Contains logic for parsing objects from Strings.
 * 
 * @author Adrian Cole
 */
public class ParserModule extends AbstractModule {
   private final static TypeLiteral<ParseSax.Factory> parseSaxFactoryLiteral = new TypeLiteral<ParseSax.Factory>() {
   };

   protected void configure() {
      bind(parseSaxFactoryLiteral).toProvider(
               FactoryProvider.newFactory(parseSaxFactoryLiteral, new TypeLiteral<ParseSax<?>>() {
               }));
   }

   static class DateTimeAdapter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
      private final static DateService dateService = new DateService();

      public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(dateService.iso8601DateFormat(src));
      }

      public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         String toParse = json.getAsJsonPrimitive().getAsString();
         DateTime toReturn = dateService.jodaIso8601DateParse(toParse);
         return toReturn;
      }

   }

   static class InetAddressAdapter implements JsonSerializer<InetAddress>,
            JsonDeserializer<InetAddress> {
      public JsonElement serialize(InetAddress src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(src.getHostAddress());
      }

      public InetAddress deserialize(JsonElement json, Type typeOfT,
               JsonDeserializationContext context) throws JsonParseException {
         try {
            return InetAddress.getByName(json.getAsJsonPrimitive().getAsString());
         } catch (UnknownHostException e) {
            throw new JsonParseException(e);
         }
      }

   }

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

   @Provides
   @Singleton
   Gson provideGson() {
      GsonBuilder gson = new GsonBuilder();
      gson.registerTypeAdapter(InetAddress.class, new InetAddressAdapter());
      gson.registerTypeAdapter(DateTime.class, new DateTimeAdapter());
      return gson.create();
   }

}
