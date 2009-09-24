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
package org.jclouds.aws.s3.domain;

import java.util.List;
import java.util.SortedSet;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ArrayListBucketResponse extends org.jclouds.rest.ArrayBoundedList<ObjectMetadata>
         implements ListBucketResponse {
   /** The serialVersionUID */
   private static final long serialVersionUID = -4475709781001190244L;
   private final String bucketName;
   private final String delimiter;
   private final SortedSet<String> commonPrefixes;
   private final boolean truncated;

   public ArrayListBucketResponse(String bucketName, List<ObjectMetadata> contents, String prefix,
            String marker, int maxResults, String delimiter, boolean isTruncated,
            SortedSet<String> commonPrefixes) {
      super(contents, prefix, marker, maxResults);
      this.delimiter = delimiter;
      this.bucketName = bucketName;
      this.commonPrefixes = commonPrefixes;
      this.truncated = isTruncated;
   }

   /**
    * {@inheritDoc}
    */
   public SortedSet<String> getCommonPrefixes() {
      return commonPrefixes;
   }

   public String getBucketName() {
      return bucketName;
   }

   public String getDelimiter() {
      return delimiter;
   }

   public boolean isTruncated() {
      return truncated;
   }

}
