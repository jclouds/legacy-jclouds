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
package org.jclouds.sqs.xml;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.HandlerForGeneratedRequestWithResult;
import org.jclouds.sqs.domain.BatchError;
import org.jclouds.sqs.domain.BatchResult;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference/Query_QueryDeleteMessageBatch.html"
 *      >docs</a>
 * 
 * @author Adrian Cole
 */
public class BatchResponseHandler<V> extends ParseSax.HandlerForGeneratedRequestWithResult<BatchResult<V>> {

   private final String resultElement;
   private final ParseSax.HandlerForGeneratedRequestWithResult<Map.Entry<String, V>> resultHandler;
   private final BatchErrorHandler errorHandler;

   private ImmutableMap.Builder<String, V> results = ImmutableMap.<String,V> builder();
   private Builder<BatchError> errors = ImmutableSet.<BatchError> builder();

   private boolean inResult;
   private boolean inError;

   protected BatchResponseHandler(String resultElement, HandlerForGeneratedRequestWithResult<Map.Entry<String, V>> resultHandler,
         BatchErrorHandler errorHandler) {
      this.resultElement = checkNotNull(resultElement, "resultElement");
      this.resultHandler = checkNotNull(resultHandler, "resultHandler");
      this.errorHandler = checkNotNull(errorHandler, "errorHandler");
   }

   @Override
   public BatchResult<V> getResult() {
      return BatchResult.<V> builder().putAll(results.build()).errors(errors.build())
            .build();
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (qName.equals(resultElement)) {
         inResult = true;
      } else if (qName.equals("BatchResultErrorEntry")) {
         inError = true;
      }
      if (inResult) {
         resultHandler.startElement(url, name, qName, attributes);
      } else if (inError) {
         errorHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (qName.equals(resultElement)) {
         results.put(resultHandler.getResult());
         inResult = false;
      } else if (qName.equals("BatchResultErrorEntry")) {
         errors.add(errorHandler.getResult());
         inError = false;
      } else if (inResult) {
         resultHandler.endElement(uri, name, qName);
      } else if (inError) {
         errorHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(char ch[], int start, int length) throws SAXException {
      if (inResult) {
         resultHandler.characters(ch, start, length);
      } else if (inError) {
         errorHandler.characters(ch, start, length);
      }
   }

}
