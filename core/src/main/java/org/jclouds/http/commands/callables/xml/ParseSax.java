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
package org.jclouds.http.commands.callables.xml;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.util.Utils;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import static org.jclouds.http.HttpConstants.PROPERTY_SAX_DEBUG;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * This object will parse the body of an HttpResponse and return the result of type <T> back to the
 * caller.
 * 
 * @author Adrian Cole
 */
public class ParseSax<T> extends HttpFutureCommand.ResponseCallable<T> {

   private final XMLReader parser;
   private final HandlerWithResult<T> handler;
   @Inject(optional = true)
   @Named(PROPERTY_SAX_DEBUG)
   private boolean suckFirst = false;

   @Inject
   public ParseSax(XMLReader parser, @Assisted HandlerWithResult<T> handler) {
      super();
      this.parser = checkNotNull(parser, "parser");
      this.handler = checkNotNull(handler, "handler");
   }

   public T call() throws HttpException {
      InputStream input = null;
      try {
         input = getResponse().getContent();
         if (input != null) {
            return parse(input);
         } else {
            throw new HttpException("No input to parse");
         }
      } catch (Exception e) {
         Utils.<HttpException> rethrowIfRuntimeOrSameType(e);
         throw new HttpException("Error parsing input for " + getResponse(), e);
      }
   }

   public T parse(InputStream xml) throws HttpException {
      parseAndCloseStream(xml, getHandler());
      return getHandler().getResult();
   }

   private void parseAndCloseStream(InputStream xml, ContentHandler handler) throws HttpException {
      parser.setContentHandler(handler);
      String response = null;
      try {
         if (suckFirst) {
            response = IOUtils.toString(xml);
            logger.trace("received content %n%s", response);
            IOUtils.closeQuietly(xml);
            xml = IOUtils.toInputStream(response);
         }
         InputSource input = new InputSource(new InputStreamReader(xml,"UTF-8"));
         parser.parse(input);
      } catch (Exception e) {
         StringBuilder message = new StringBuilder();
         message.append("Error parsing input for ").append(handler);
         if (response != null) {
            message.append("\n").append(response);
         }
         logger.error(e, message.toString());
         if (!(e instanceof NullPointerException))
            Utils.<HttpException> rethrowIfRuntimeOrSameType(e);
         throw new HttpException(message.toString(), e);
      } finally {
         IOUtils.closeQuietly(xml);
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
