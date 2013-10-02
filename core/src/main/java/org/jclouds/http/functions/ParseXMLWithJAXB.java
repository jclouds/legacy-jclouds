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

import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.util.Strings2;
import org.jclouds.xml.XMLParser;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * This object will parse the body of an HttpResponse and return the result of
 * type <T> back to the caller.
 * <p>
 * {@link JAXBContext} works with {@link Class} objects instead of {@link Type}.
 * This could be a limitation if we are trying to parse typed collections of
 * objects. However, when using JAXB we expect to have well formed XML documents
 * with one single root element, so the objects to parse should not be
 * collections but objects that wrap collections of elements, and that should
 * work fine.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class ParseXMLWithJAXB<T> implements Function<HttpResponse, T> {
   @Resource
   protected Logger logger = Logger.NULL;

   protected XMLParser xml;

   protected final TypeLiteral<T> type;

   @Inject
   public ParseXMLWithJAXB(final XMLParser xml, final TypeLiteral<T> type) {
      this.xml = xml;
      this.type = type;
   }

   @Override
   public T apply(final HttpResponse from) {
      InputStream xml = from.getPayload().getInput();
      try {
         return apply(xml);
      } catch (Exception e) {
         StringBuilder message = new StringBuilder();
         message.append("Error parsing input");
         logger.error(e, message.toString());
         throw new HttpResponseException(message.toString() + "\n" + from, null, from, e);
      } finally {
         releasePayload(from);
      }
   }

   @SuppressWarnings("unchecked")
   public T apply(final InputStream stream) throws IOException {
      return (T) apply(stream, type.getRawType());
   }

   public <V> V apply(final InputStream stream, final Class<V> type) throws IOException {
      try {
         return xml.fromXML(Strings2.toStringAndClose(stream), type);
      } finally {
         if (stream != null) {
            stream.close();
         }
      }
   }

}
