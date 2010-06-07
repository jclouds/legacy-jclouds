/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.twitter;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Properties;

import org.jclouds.rest.RestContextBuilder;
import org.jclouds.twitter.config.TwitterContextModule;
import org.jclouds.twitter.config.TwitterRestClientModule;
import org.jclouds.twitter.reference.TwitterConstants;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class TwitterContextBuilder extends RestContextBuilder<TwitterClient, TwitterAsyncClient> {

   public TwitterContextBuilder(String providerName, Properties props) {
      super(providerName, TwitterClient.class, TwitterAsyncClient.class, props);
      checkNotNull(properties.getProperty(TwitterConstants.PROPERTY_TWITTER_USER));
      checkNotNull(properties.getProperty(TwitterConstants.PROPERTY_TWITTER_PASSWORD));
   }

   protected void addClientModule(List<Module> modules) {
      modules.add(new TwitterRestClientModule());
   }

   @Override
   protected void addContextModule(String providerName, List<Module> modules) {
      modules.add(new TwitterContextModule(providerName));
   }

}
