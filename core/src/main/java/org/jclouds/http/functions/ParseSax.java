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
package org.jclouds.http.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.Closeables.closeQuietly;
import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.annotation.Resource;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.io.Closeables;

/**
 * This object will parse the body of an HttpResponse and return the result of type <T> back to the
 * caller.
 * 
 * @author Adrian Cole
 */
public class ParseSax<T> implements Function<HttpResponse, T>, InvocationContext<ParseSax<T>> {

   @Resource
   private Logger logger = Logger.NULL;

   private final XMLReader parser;
   private final HandlerWithResult<T> handler;
   private HttpRequest request;

   public static interface Factory {
      <T> ParseSax<T> create(HandlerWithResult<T> handler);
   }

   public ParseSax(XMLReader parser, HandlerWithResult<T> handler) {
      this.parser = checkNotNull(parser, "parser");
      this.handler = checkNotNull(handler, "handler");
   }

   public T apply(HttpResponse from) {
      try {
         checkNotNull(from, "http response");
         checkNotNull(from.getPayload(), "payload in " + from);
      } catch (NullPointerException e) {
         return addDetailsAndPropagate(from, e);
      }
      InputStream is = null;
      try {
         // debug is more normally set, so trace is more appropriate for
         // something heavy like this
         if (from.getStatusCode() >= 300 || logger.isTraceEnabled())
            return convertStreamToStringAndParse(from);
         is = from.getPayload().getInput();
         return parse(new InputSource(is));
      } catch (RuntimeException e) {
         return addDetailsAndPropagate(from, e);
      } finally {
         Closeables.closeQuietly(is);
         from.getPayload().release();
      }
   }

   private T convertStreamToStringAndParse(HttpResponse response) {
      String from = null;
      try {
         from = new String(closeClientButKeepContentStream(response));
         validateXml(from);
         return doParse(new InputSource(new StringReader(from)));
      } catch (Exception e) {
         return addDetailsAndPropagate(response, e, from);
      }
   }

   public T parse(String from) {
      try {
         validateXml(from);
         return doParse(new InputSource(new StringReader(from)));
      } catch (Exception e) {
         return addDetailsAndPropagate(null, e, from);
      }
   }

   private void validateXml(String from) {
      checkNotNull(from, "xml string");
      checkArgument(from.indexOf('<') >= 0, String.format("not an xml document [%s] ", from));
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
         return doParse(from);
      } catch (Exception e) {
         return addDetailsAndPropagate(null, e);
      }
   }

   protected T doParse(InputSource from) throws IOException, SAXException {
      checkNotNull(from, "xml inputsource");
      from.setEncoding("UTF-8");
      parser.setContentHandler(getHandler());
      // This method should accept documents with a BOM (Byte-order mark)
      parser.parse(from);
      return getHandler().getResult();
   }

   public T addDetailsAndPropagate(HttpResponse response, Exception e) {
      return addDetailsAndPropagate(response, e, null);
   }

   public T addDetailsAndPropagate(HttpResponse response, Exception e, @Nullable String text) {
      StringBuilder message = new StringBuilder();
      if (request != null) {
         message.append("request: ").append(request.getRequestLine());
      }
      if (response != null) {
         if (message.length() != 0)
            message.append("; ");
         message.append("response: ").append(response.getStatusLine());
      }
      if (e instanceof SAXParseException) {
         SAXParseException parseException = (SAXParseException) e;
         String systemId = parseException.getSystemId();
         if (systemId == null) {
            systemId = "";
         }
         if (message.length() != 0)
            message.append("; ");
         message.append(String.format("error at %d:%d in document %s", parseException.getColumnNumber(), parseException
                  .getLineNumber(), systemId));
      }
      if (text != null)
         message.append("; source:\n").append(text);
      if (message.length() != 0) {
         message.append("; cause: ").append(e.toString());
         throw new RuntimeException(message.toString(), e);
      } else {
         throw Throwables.propagate(e);
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
   public abstract static class HandlerWithResult<T> extends DefaultHandler implements
            InvocationContext<HandlerWithResult<T>> {
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
      public HandlerForGeneratedRequestWithResult<T> setContext(HttpRequest request) {
         checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest, "note this handler requires a GeneratedHttpRequest");
         super.setContext(request);
         return this;
      }
      
      @Override
      protected GeneratedHttpRequest getRequest() {
         return (GeneratedHttpRequest) super.getRequest();
      }
   }

   @Override
   public ParseSax<T> setContext(HttpRequest request) {
      handler.setContext(request);
      this.request = request;
      return this;
   }
}
