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
package org.jclouds.demo.tweetstore.reference;

/**
 * Configuration properties and constants used in TweetStore connections.
 * 
 * @author Adrian Cole
 */
public interface TweetStoreConstants {
    static final String PROPERTY_TWEETSTORE_BLOBSTORES = "jclouds.tweetstore.blobstores";
    static final String PROPERTY_TWEETSTORE_CONTAINER = "jclouds.tweetstore.container";
    /**
     * Note that this has to conform to restrictions of all blobstores. for
     * example, azure doesn't support periods.
     */
    static final String SENDER_NAME = "sendername";
}
