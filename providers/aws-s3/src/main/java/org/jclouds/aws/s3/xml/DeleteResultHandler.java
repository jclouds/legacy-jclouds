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
package org.jclouds.aws.s3.xml;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.aws.s3.domain.DeleteResult;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

/**
 * @author Andrei Savu
 */
public class DeleteResultHandler extends ParseSax.HandlerForGeneratedRequestWithResult<DeleteResult> {

   public static final String DELETED_TAG = "Deleted";
   public static final String ERROR_TAG = "Error";

   private final ErrorEntryHandler errorEntryHandler = new ErrorEntryHandler();

   private StringBuilder deletedEntryAccumulator = new StringBuilder();

   /**
    * Accumulator for the set of successfully deleted files
    */
   private final ImmutableSet.Builder<String> deleted = ImmutableSet.builder();

   /**
    * Accumulator for the set of errors
    */
   private final ImmutableMap.Builder<String, DeleteResult.Error> errors = ImmutableMap.builder();

   private boolean parsingDeletedEntry = false;
   private boolean parsingErrorEntry = false;

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String uri, String name, String qName, Attributes attributes)
      throws SAXException {
      if (equalsOrSuffix(qName, DELETED_TAG)) {
         parsingDeletedEntry = true;
      } else if (equalsOrSuffix(qName, ERROR_TAG)) {
         parsingErrorEntry = true;
      }

      if (parsingDeletedEntry) {
         deletedEntryAccumulator = new StringBuilder();
      } else if (parsingErrorEntry) {
         errorEntryHandler.startElement(uri, name, qName, attributes);
      }
   }

   @Override
   public void characters(char[] chars, int start, int length) throws SAXException {
      if (parsingDeletedEntry) {
         deletedEntryAccumulator.append(chars, start, length);
      } else if (parsingErrorEntry) {
         errorEntryHandler.characters(chars, start, length);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, DELETED_TAG)) {
         parsingDeletedEntry = false;
         deleted.add(deletedEntryAccumulator.toString().trim());
      } else if (equalsOrSuffix(qName, ERROR_TAG)) {
         parsingErrorEntry = false;
         errors.put(errorEntryHandler.getResult());
      }

      if (parsingErrorEntry) {
         errorEntryHandler.endElement(uri, name, qName);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DeleteResult getResult() {
      return new DeleteResult(deleted.build(), errors.build());
   }
}
