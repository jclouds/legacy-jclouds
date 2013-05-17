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
package org.jclouds.atmos.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Contains options supported in the REST API for the PUT operations.
 * <p/>
 * <h2>
 * Usage</h2> The recommended way to instantiate a PutOptions object is to statically import
 * PutOptions.Builder.* and invoke a static creation method followed by an instance mutator (if
 * needed):
 * <p/>
 * <code>
 * import org.jclouds.atmos.options.PutOptions.Builder.*
 * import org.jclouds.atmos.AtmosClient;
 * 
 * AtmosClient connection = // get connection
 *  connection.createDirectory("directory", publicRead());
 * <code>
 * 
 * @author Adrian Cole
 * 
 */
public class PutOptions extends BaseHttpRequestOptions {
   public static final PutOptions NONE = new PutOptions();

   /**
    * Add public access to all users
    * 
    */
   public PutOptions publicRead() {
      this.replaceHeader("x-emc-useracl", "root=FULL_CONTROL");
      this.replaceHeader("x-emc-groupacl", "other=READ");
      return this;
   }

   public static class Builder {

      /**
       * @see PutOptions#publicRead
       */
      public static PutOptions publicRead() {
         PutOptions options = new PutOptions();
         return options.publicRead();
      }
   }
}
