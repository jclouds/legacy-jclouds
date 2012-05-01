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
 * Configuration properties and constants used in Twitter connections.
 * 
 * @author Andrew Phillips
 */
public interface TwitterConstants {
    static final String PROPERTY_TWITTER_CONSUMER_KEY = "twitter.consumer.identity";
    static final String PROPERTY_TWITTER_CONSUMER_SECRET = "twitter.consumer.credential";
    static final String PROPERTY_TWITTER_ACCESSTOKEN = "twitter.access.identity";
    static final String PROPERTY_TWITTER_ACCESSTOKEN_SECRET = "twitter.access.credential";
}
