/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.gogrid.config.internal;

import com.google.inject.AbstractModule;
import org.jclouds.compute.config.ResolvesImages;
import org.jclouds.compute.strategy.AuthenticateImagesStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.domain.Server;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oleksiy Yarmula
 */
@ResolvesImages
public class GoGridAuthenticationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AuthenticateImagesStrategy.class).to(GoGridAuthenticateImagesStrategy.class);
    }

    public static class GoGridAuthenticateImagesStrategy implements AuthenticateImagesStrategy {
        private final GoGridClient client;

        @Inject
        protected GoGridAuthenticateImagesStrategy(GoGridClient client) {
            this.client = client;
        }

        @Override
        public Credentials execute(Object resourceToAuthenticate) {
            checkNotNull(resourceToAuthenticate);
            checkArgument(resourceToAuthenticate instanceof Server, "Resource must be a server (for GoGrid)");
            Server server = (Server) resourceToAuthenticate;
            return client.getServerServices().getServerCredentialsList().get(
                    server.getName());
        }
    }

}
