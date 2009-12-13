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
package org.jclouds.blobstore.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contains options supported for HTTP GET operations. <h2>
 * Usage</h2> The recommended way to instantiate a GetObjectOptions object is to statically import
 * GetObjectOptions.Builder.* and invoke a static creation method followed by an instance mutator
 * (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.http.options.GetOptions.Builder.*
 * 
 * 
 * // this will get the first megabyte of an object, provided it wasn't modified since yesterday
 * Future<S3Object> object = client.get("objectName",range(0,1024).ifUnmodifiedSince(new Date().minusDays(1)));
 * <code>
 * 
 * @author Adrian Cole
 * 
 */
public class GetOptions {
   private final List<String> ranges = new ArrayList<String>();
   private Date ifModifiedSince;
   private Date ifUnmodifiedSince;
   private String ifMatch;
   private String ifNoneMatch;

   /**
    * download the specified range of the object.
    */
   public GetOptions range(long start, long end) {
      checkArgument(start >= 0, "start must be >= 0");
      checkArgument(end >= 0, "end must be >= 0");
      getRanges().add(String.format("%d-%d", start, end));
      return this;
   }

   /**
    * download the specified range of the object.
    */
   public GetOptions startAt(long start) {
      checkArgument(start >= 0, "start must be >= 0");
      getRanges().add(String.format("%d-", start));
      return this;
   }

   /**
    * Only return the object if it has changed since this time.
    * <p />
    * Not compatible with {@link #ifETagMatches(String)} or {@link #ifUnmodifiedSince(Date)}
    */
   public GetOptions ifModifiedSince(Date ifModifiedSince) {
      checkArgument(getIfMatch() == null,
               "ifETagMatches() is not compatible with ifModifiedSince()");
      checkArgument(getIfUnmodifiedSince() == null,
               "ifUnmodifiedSince() is not compatible with ifModifiedSince()");
      this.ifModifiedSince = checkNotNull(ifModifiedSince, "ifModifiedSince");
      return this;
   }

   /**
    * For use in the header If-Modified-Since
    * <p />
    * Return the object only if it has been modified since the specified time, otherwise return a
    * 304 (not modified).
    * 
    * @see GetOptions#ifModifiedSince(Date)
    */
   public Date getIfModifiedSince() {
      return this.ifModifiedSince;
   }

   /**
    * Only return the object if it hasn't changed since this time.
    * <p />
    * Not compatible with {@link #ifETagDoesntMatch(String)} or {@link #ifModifiedSince(Date)}
    */
   public GetOptions ifUnmodifiedSince(Date ifUnmodifiedSince) {
      checkArgument(getIfNoneMatch() == null,
               "ifETagDoesntMatch() is not compatible with ifUnmodifiedSince()");
      checkArgument(getIfModifiedSince() == null,
               "ifModifiedSince() is not compatible with ifUnmodifiedSince()");
      this.ifUnmodifiedSince = checkNotNull(ifUnmodifiedSince, "ifUnmodifiedSince");
      return this;
   }

   /**
    * For use in the header If-Unmodified-Since
    * <p />
    * Return the object only if it has not been modified since the specified time, otherwise return
    * a 412 (precondition failed).
    * 
    * @see GetOptions#ifUnmodifiedSince(Date)
    */
   public Date getIfUnmodifiedSince() {
      return this.ifUnmodifiedSince;
   }

   /**
    * The object's eTag hash should match the parameter <code>eTag</code>.
    * 
    * <p />
    * Not compatible with {@link #ifETagDoesntMatch(String)} or {@link #ifModifiedSince(Date)}
    * 
    * @param eTag
    *           hash representing the entity
    */
   public GetOptions ifETagMatches(String eTag) {
      checkArgument(getIfNoneMatch() == null,
               "ifETagDoesntMatch() is not compatible with ifETagMatches()");
      checkArgument(getIfModifiedSince() == null,
               "ifModifiedSince() is not compatible with ifETagMatches()");
      this.ifMatch = checkNotNull(eTag, "eTag");
      return this;
   }

   /**
    * For use in the request header: If-Match
    * <p />
    * Return the object only if its entity tag (ETag) is the same as the eTag specified, otherwise
    * return a 412 (precondition failed).
    * 
    * @see GetOptions#ifETagMatches(String)
    */
   public String getIfMatch() {
      return this.ifMatch;
   }

   /**
    * The object should not have a eTag hash corresponding with the parameter <code>eTag</code>.
    * <p />
    * Not compatible with {@link #ifETagMatches(String)} or {@link #ifUnmodifiedSince(Date)}
    * 
    * @param eTag
    *           hash representing the entity
    */
   public GetOptions ifETagDoesntMatch(String eTag) {
      checkArgument(getIfMatch() == null,
               "ifETagMatches() is not compatible with ifETagDoesntMatch()");
      checkArgument(getIfUnmodifiedSince() == null,
               "ifUnmodifiedSince() is not compatible with ifETagDoesntMatch()");
      this.ifNoneMatch = checkNotNull(eTag, "eTag");
      return this;
   }

   /**
    * For use in the request header: If-None-Match
    * <p />
    * Return the object only if its entity tag (ETag) is different from the one specified, otherwise
    * return a 304 (not modified).
    * 
    * @see GetOptions#ifETagDoesntMatch(String)
    */
   public String getIfNoneMatch() {
      return this.ifNoneMatch;
   }

   public List<String> getRanges() {
      return ranges;
   }

   public static class Builder {

      /**
       * @see GetOptions#range(long, long)
       */
      public static GetOptions range(long start, long end) {
         GetOptions options = new GetOptions();
         return options.range(start, end);
      }

      /**
       * @see GetOptions#getIfModifiedSince()
       */
      public static GetOptions ifModifiedSince(Date ifModifiedSince) {
         GetOptions options = new GetOptions();
         return options.ifModifiedSince(ifModifiedSince);
      }

      /**
       * @see GetOptions#ifUnmodifiedSince(Date)
       */
      public static GetOptions ifUnmodifiedSince(Date ifUnmodifiedSince) {
         GetOptions options = new GetOptions();
         return options.ifUnmodifiedSince(ifUnmodifiedSince);
      }

      /**
       * @see GetOptions#ifETagMatches(String)
       */
      public static GetOptions ifETagMatches(String eTag) throws UnsupportedEncodingException {
         GetOptions options = new GetOptions();
         return options.ifETagMatches(eTag);
      }

      /**
       * @see GetOptions#ifETagDoesntMatch(String)
       */
      public static GetOptions ifETagDoesntMatch(String eTag) throws UnsupportedEncodingException {
         GetOptions options = new GetOptions();
         return options.ifETagDoesntMatch(eTag);
      }

   }
}
