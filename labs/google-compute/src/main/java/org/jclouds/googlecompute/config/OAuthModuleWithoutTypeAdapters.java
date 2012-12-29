/*
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

package org.jclouds.googlecompute.config;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.TypeLiteral;
import org.jclouds.oauth.v2.config.OAuthModule;
import org.jclouds.oauth.v2.domain.OAuthCredentials;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.oauth.v2.functions.BuildTokenRequest;
import org.jclouds.oauth.v2.functions.FetchToken;
import org.jclouds.oauth.v2.functions.OAuthCredentialsSupplier;
import org.jclouds.oauth.v2.functions.SignOrProduceMacForToken;
import org.jclouds.rest.internal.GeneratedHttpRequest;

/**
 * Overrides OAuthModule leaving TypeAdapters bindings out.
 * <p/>
 * TODO overcome this by using multibindings on GSonModule?
 *
 * @author David Alves
 */
public class OAuthModuleWithoutTypeAdapters extends OAuthModule {

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<byte[], byte[]>>() {}).to(SignOrProduceMacForToken.class);
      bind(new TypeLiteral<Supplier<OAuthCredentials>>() {}).to(OAuthCredentialsSupplier.class);
      bind(new TypeLiteral<Function<GeneratedHttpRequest, TokenRequest>>() {}).to(BuildTokenRequest.class);
      bind(new TypeLiteral<Function<TokenRequest, Token>>() {}).to(FetchToken.class);
   }
}
