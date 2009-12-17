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
package org.jclouds.http.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.base.Function;
import com.google.common.io.Closeables;

/**
 * This object will parse the body of an HttpResponse and return the result of type <T> back to the
 * caller.
 * 
 * @author Adrian Cole
 */
public class ParseSax<T> implements Function<HttpResponse, T> {

   private final XMLReader parser;
   private final HandlerWithResult<T> handler;
   @Resource
   protected Logger logger = Logger.NULL;

   public static interface Factory {
      <T> ParseSax<T> create(HandlerWithResult<T> handler);
   }

   @Inject
   public ParseSax(XMLReader parser, HandlerWithResult<T> handler) {
      this.parser = checkNotNull(parser, "parser");
      this.handler = checkNotNull(handler, "handler");
   }

   public T apply(HttpResponse from) throws HttpException {
      InputStream input = null;
      try {
         input = from.getContent();
         if (input != null) {
            return parse(input);
         } else {
            throw new HttpException("No input to parse");
         }
      } catch (Exception e) {
         Utils.<HttpException> rethrowIfRuntimeOrSameType(e);
         throw new HttpException("Error parsing input for " + from, e);
      }
   }

   public T parse(InputStream xml) throws HttpException {
      parseAndCloseStream(xml, getHandler());
      return getHandler().getResult();
   }

   private void parseAndCloseStream(InputStream xml, ContentHandler handler) throws HttpException {
      try {
         parser.setContentHandler(handler);
         // This method should accept documents with a BOM (Byte-order mark)
         InputSource input = new InputSource(xml);
         parser.parse(input);
      } catch (Exception e) {
         StringBuilder message = new StringBuilder();
         message.append("Error parsing input for ").append(handler);
         logger.error(e, message.toString());
         if (!(e instanceof NullPointerException))
            Utils.<HttpException> rethrowIfRuntimeOrSameType(e);
         throw new HttpException(message.toString(), e);
      } finally {
         Closeables.closeQuietly(xml);
      }
   }

   public HandlerWithResult<T> getHandler() {
      return handler;
   }

   /**
    * Handler that produces a useable domain object accessible after parsing completes.
    * 
    * @author Adrian Cole
    */
   public abstract static class HandlerWithResult<T> extends DefaultHandler {
      public abstract T getResult();
   }
}
