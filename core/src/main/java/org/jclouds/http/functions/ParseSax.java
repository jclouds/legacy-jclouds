/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.Closeables.closeQuietly;

import java.io.InputStream;
import java.io.StringReader;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Utils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.base.Function;

/**
 * This object will parse the body of an HttpResponse and return the result of
 * type <T> back to the caller.
 * 
 * @author Adrian Cole
 */
public class ParseSax<T> implements Function<HttpResponse, T>, InvocationContext {

   private final XMLReader parser;
   private final HandlerWithResult<T> handler;
   @Resource
   protected Logger logger = Logger.NULL;
   private HttpRequest request;

   public static interface Factory {
      <T> ParseSax<T> create(HandlerWithResult<T> handler);
   }

   @Inject
   public ParseSax(XMLReader parser, HandlerWithResult<T> handler) {
      this.parser = checkNotNull(parser, "parser");
      this.handler = checkNotNull(handler, "handler");
   }

   public T apply(HttpResponse from) {
      try {
         checkNotNull(from, "http response");
         checkNotNull(from.getPayload(), "payload in " + from);
      } catch (NullPointerException e) {
         return addRequestDetailsToException(e);
      }
      if (from.getStatusCode() >= 300)
         return convertStreamToStringAndParse(from);
      return parse(from.getPayload().getInput());
   }

   private T convertStreamToStringAndParse(HttpResponse from) {
      try {
         return parse(Utils.toStringAndClose(from.getPayload().getInput()));
      } catch (Exception e) {
         return addRequestDetailsToException(e);
      }
   }

   public T parse(String from) {
      try {
         checkNotNull(from, "xml string");
         checkArgument(from.indexOf('<') >= 0, String.format("not an xml document [%s] ", from));
      } catch (RuntimeException e) {
         return addRequestDetailsToException(e);
      }
      return parse(new InputSource(new StringReader(from)));
   }

   public T parse(InputStream from) {
      try {
         return parse(new InputSource(from));
      } finally {
         closeQuietly(from);
      }
   }

   public T parse(InputSource from) {
      try {
         checkNotNull(from, "xml inputsource");
         parser.setContentHandler(getHandler());
         // This method should accept documents with a BOM (Byte-order mark)
         parser.parse(from);
         return getHandler().getResult();
      } catch (Exception e) {
         return addRequestDetailsToException(e);
      }
   }

   private T addRequestDetailsToException(Exception e) {
      if (request != null) {
         StringBuilder message = new StringBuilder();
         message.append("Error parsing input for ").append(request.getRequestLine()).append(": ");
         message.append(e.getMessage());
         logger.error(e, message.toString());
         throw new HttpException(message.toString(), e);
      } else {
         propagate(e);
         assert false : "should have propagated: " + e;
         return null;
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
   public abstract static class HandlerWithResult<T> extends DefaultHandler implements InvocationContext {
      private HttpRequest request;

      protected HttpRequest getRequest() {
         return request;
      }

      public abstract T getResult();

      @Override
      public HandlerWithResult<T> setContext(HttpRequest request) {
         this.request = request;
         return this;
      }
   }

   public abstract static class HandlerForGeneratedRequestWithResult<T> extends HandlerWithResult<T> {
      @Override
      protected GeneratedHttpRequest<?> getRequest() {
         return (GeneratedHttpRequest<?>) super.getRequest();
      }

      @Override
      public HandlerForGeneratedRequestWithResult<T> setContext(HttpRequest request) {
         checkArgument(request instanceof GeneratedHttpRequest<?>, "note this handler requires a GeneratedHttpRequest");
         super.setContext(request);
         return this;
      }
   }

   @Override
   public ParseSax<T> setContext(HttpRequest request) {
      handler.setContext(request);
      this.request = request;
      return this;
   }
}
