/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.mezeo.pcs.options;

import static com.google.common.base.Preconditions.checkArgument;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.Multimap;

/**
 * Contains options supported in the REST API for the PUT file operation. <h2>
 * Usage</h2> The recommended way to instantiate a PutFileOptions object is to statically import
 * PutFileOptions.Builder.* and invoke a static creation method followed by an instance mutator (if
 * needed):
 * <p/>
 * <code>
 * import static org.jclouds.mezeo.pcs2.options.PutFileOptions.Builder.*
 * import org.jclouds.mezeo.pcs2.PCSClient;
 * <p/>
 * PCSClient connection = // get connection
 * Future<Void> added = connection.appendFile("container",range(0,3));
 * <code>
 * 
 * @author Adrian Cole
 */
public class PutBlockOptions extends BaseHttpRequestOptions {
   public static final PutBlockOptions NONE = new PutBlockOptions();
   private String range;

   @Override
   public Multimap<String, String> buildRequestHeaders() {
      Multimap<String, String> headers = super.buildRequestHeaders();
      String range = getRange();
      if (range != null)
         headers.put("Content-Range", this.getRange());
      return headers;
   }

   /**
    * For use in the header Content-Range
    * <p />
    * 
    * @see PutBlockOptions#range(long, long)
    */
   public String getRange() {
      return range;
   }

   /**
    * download the specified range of the object.
    */
   public PutBlockOptions range(long start, long end) {
      checkArgument(start >= 0, "start must be >= 0");
      checkArgument(end >= 0, "end must be >= 0");
      range = String.format("bytes %d-%d/*", start, end);
      return this;
   }

   public static class Builder {
      /**
       * @see PutBlockOptions#range(long, long)
       */
      public static PutBlockOptions range(long start, long end) {
         PutBlockOptions options = new PutBlockOptions();
         return options.range(start, end);
      }
   }
}
