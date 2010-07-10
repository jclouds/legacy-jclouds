/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.io.Closeables;

/**
 * This object will parse the body of an HttpResponse and return the result of
 * type <T> back to the caller.
 * 
 * @author Adrian Cole
 */
public class ParseSax<T> implements Function<HttpResponse, T>,
      InvocationContext {

   private final XMLReader parser;
   private final HandlerWithResult<T> handler;
   @Resource
   protected Logger logger = Logger.NULL;
   private GeneratedHttpRequest<?> request;

   public static interface Factory {
      <T> ParseSax<T> create(HandlerWithResult<T> handler);
   }

   @Inject
   public ParseSax(XMLReader parser, HandlerWithResult<T> handler) {
      this.parser = checkNotNull(parser, "parser");
      this.handler = checkNotNull(handler, "handler");
   }

   public T apply(HttpResponse from) throws HttpException {
      return parse(from.getPayload().getInput());
   }

   public T parse(InputStream from) throws HttpException {
      if (from == null)
         throw new HttpException("No input to parse");
      try {
         parser.setContentHandler(getHandler());
         // This method should accept documents with a BOM (Byte-order mark)
         parser.parse(new InputSource(from));
         return getHandler().getResult();
      } catch (Exception e) {
         if (request != null) {

            StringBuilder message = new StringBuilder();
            message.append("Error parsing input for ").append(
                  request.getRequestLine()).append(": ");
            message.append(e.getMessage());
            logger.error(e, message.toString());
            throw new HttpException(message.toString(), e);
         } else {
            Throwables.propagate(e);
            assert false : "should have propagated: " + e;
            return null;
         }
      } finally {
         Closeables.closeQuietly(from);
      }
   }

   public HandlerWithResult<T> getHandler() {
      return handler;
   }

   /**
    * Handler that produces a useable domain object accessible after parsing
    * completes.
    * 
    * @author Adrian Cole
    */
   public abstract static class HandlerWithResult<T> extends DefaultHandler
         implements InvocationContext {
      protected GeneratedHttpRequest<?> request;

      public abstract T getResult();

      @Override
      public void setContext(GeneratedHttpRequest<?> request) {
         this.request = request;
      }
   }

   @Override
   public void setContext(GeneratedHttpRequest<?> request) {
      handler.setContext(request);
      this.request = request;
   }
}
