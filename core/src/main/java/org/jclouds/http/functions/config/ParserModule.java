/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.util.DateService;
import org.xml.sax.XMLReader;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.ImplementedBy;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Contains logic for parsing objects from Strings.
 * 
 * @author Adrian Cole
 */
public class ParserModule extends AbstractModule {

   protected void configure() {
      bind(ParseSax.Factory.class).to(Factory.class).in(Scopes.SINGLETON);
   }

   private static class Factory implements ParseSax.Factory {
      @Inject
      private SAXParserFactory factory;

      public <T> ParseSax<T> create(HandlerWithResult<T> handler) {
         SAXParser saxParser;
         try {
            saxParser = factory.newSAXParser();
            XMLReader parser = saxParser.getXMLReader();
            return new ParseSax<T>(parser, handler);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }

      }
   }

   private static final Comparator<InetAddress> ADDRESS_COMPARATOR = new Comparator<InetAddress>() {

      @Override
      public int compare(InetAddress o1, InetAddress o2) {
         return (o1 == o2) ? 0 : o1.getHostAddress().compareTo(o2.getHostAddress());
      }

   };

   static class SortedSetOfInetAddressCreator implements InstanceCreator<SortedSet<InetAddress>> {

      @Override
      public SortedSet<InetAddress> createInstance(Type arg0) {
         return Sets.newTreeSet(ADDRESS_COMPARATOR);
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
   @Singleton
   SAXParserFactory provideSAXParserFactory() {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(false);
      factory.setValidating(false);
      factory.setXIncludeAware(false);
      return factory;
   }

   @Provides
   @Singleton
   Gson provideGson(DateAdapter adapter, SortedSetOfInetAddressCreator addressSetCreator) {
      GsonBuilder gson = new GsonBuilder();
      gson.registerTypeAdapter(InetAddress.class, new InetAddressAdapter());
      gson.registerTypeAdapter(Date.class, adapter);
      gson.registerTypeAdapter(new TypeToken<SortedSet<InetAddress>>() {
      }.getType(), addressSetCreator);
      return gson.create();
   }

   @ImplementedBy(Iso8601DateAdapter.class)
   public static interface DateAdapter extends JsonSerializer<Date>, JsonDeserializer<Date> {

   }

   @Singleton
   public static class Iso8601DateAdapter implements DateAdapter {
      private final DateService dateService;

      @Inject
      private Iso8601DateAdapter(DateService dateService) {
         this.dateService = dateService;
      }

      public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(dateService.iso8601DateFormat(src));
      }

      public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         String toParse = json.getAsJsonPrimitive().getAsString();
         try {
            return dateService.iso8601DateParse(toParse);
         } catch (RuntimeException e) {
            return dateService.iso8601SecondsDateParse(toParse);
         }
      }

   }

   @Singleton
   public static class CDateAdapter implements DateAdapter {
      private final DateService dateService;

      @Inject
      private CDateAdapter(DateService dateService) {
         this.dateService = dateService;
      }

      public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(dateService.cDateFormat(src));
      }

      public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         String toParse = json.getAsJsonPrimitive().getAsString();
         Date toReturn = dateService.cDateParse(toParse);
         return toReturn;
      }

   }
}
