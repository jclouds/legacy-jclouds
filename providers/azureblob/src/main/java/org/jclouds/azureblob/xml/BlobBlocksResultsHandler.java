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
package org.jclouds.azureblob.xml;

import java.util.List;

import org.jclouds.azureblob.domain.BlobBlockProperties;
import org.jclouds.azureblob.domain.ListBlobBlocksResponse;
import org.jclouds.azureblob.domain.internal.BlobBlockPropertiesImpl;
import org.jclouds.azureblob.domain.internal.ListBlobBlocksResponseImpl;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

/**
 * Parses the following document:
 * <pre>
 * <?xml version="1.0" encoding="utf-8"?>
 * <BlockList>
 * <CommittedBlocks>
 * <Block>
 * <Name>base64-encoded-block-id</Name>
 * <Size>size-in-bytes</Size>
 * </Block>
 * <CommittedBlocks>
 * </BlockList>
 * </pre>
 */
public class BlobBlocksResultsHandler extends ParseSax.HandlerWithResult<ListBlobBlocksResponse> {

   private StringBuilder currentText = new StringBuilder();
   private boolean inCommitted = false;
   private boolean inBlock = false;
   private boolean inName = false;
   private boolean inSize = false;
   private String blockName;
   private long size;
   private List<BlobBlockProperties> blocks = Lists.newArrayList();

   @Override
   public ListBlobBlocksResponse getResult() {
      return new ListBlobBlocksResponseImpl(blocks);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
         throws SAXException {
      if ("CommittedBlocks".equals(qName)) {
         inCommitted = true;
      } else if ("UncommittedBlocks".equals(qName)) {
         inCommitted = false;
      } else if ("Block".equals(qName)) {
         inBlock = true;
      } else if ("Name".equals(qName)) {
         inName = true;
      } else if ("Size".equals(qName)) {
         inSize = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      if ("CommittedBlocks".equals(qName)) {
         inCommitted = false;
      } else if ("UncommittedBlocks".equals(qName)) {
         inCommitted = false;
      } else if ("Block".equals(qName)) {
         BlobBlockProperties block = new BlobBlockPropertiesImpl(blockName, size, inCommitted);
         blocks.add(block);
         inBlock = false;
      } else if ("Name".equals(qName)) {
         blockName = currentText.toString().trim();
         inName = false;
      } else if ("Size".equals(qName)) {
         size = Long.parseLong(currentText.toString().trim());
         inSize = false;
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
