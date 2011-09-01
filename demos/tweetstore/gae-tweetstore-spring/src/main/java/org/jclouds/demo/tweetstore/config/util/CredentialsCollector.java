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
package org.jclouds.demo.tweetstore.config.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Maps.filterValues;
import static org.jclouds.util.Maps2.fromKeys;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.demo.tweetstore.config.util.CredentialsCollector.Credential;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Reads provider credentials from a {@link Properties} bag.
 * 
 * @author Andrew Phillips
 *
 */
public class CredentialsCollector implements Function<Properties, Map<String, Credential>> {
    private static final String IDENTITY_PROPERTY_SUFFIX = ".identity";
    private static final String CREDENTIAL_PROPERTY_SUFFIX = ".credential";
    
    // using the identity for provider name extraction
    private static final Pattern IDENTITY_PROPERTY_PATTERN =
        Pattern.compile("([a-zA-Z0-9-]+)" + Pattern.quote(IDENTITY_PROPERTY_SUFFIX));
    
    @Override
    public Map<String, Credential> apply(final Properties properties) {
        Collection<String> providerNames = transform(
                filter(properties.stringPropertyNames(), MatchesPattern.matches(IDENTITY_PROPERTY_PATTERN)), 
                new Function<String, String>() {
                    @Override
                    public String apply(String input) {
                        Matcher matcher = IDENTITY_PROPERTY_PATTERN.matcher(input);
                        // as a side-effect, sets the matching group!
                        checkState(matcher.matches(), "'%s' should match '%s'", input, IDENTITY_PROPERTY_PATTERN);
                        return matcher.group(1);
                    }
                });
        /*
         * Providers without a credential property result in null values, which are
         * removed from the returned map.
         */
        return filterValues(fromKeys(copyOf(providerNames), new Function<String, Credential>() {
            @Override
            public Credential apply(String providerName) {
                String identity = properties.getProperty(providerName + IDENTITY_PROPERTY_SUFFIX);
                String credential = properties.getProperty(providerName + CREDENTIAL_PROPERTY_SUFFIX);
                return (((identity != null) && (credential != null)) 
                        ? new Credential(identity, credential) 
                        : null);
            }
        }), notNull());
    }
    
    public static class Credential {
        private final String identity;
        private final String credential;
        
        public Credential(String identity, String credential) {
            this.identity = checkNotNull(identity, "identity");
            this.credential = checkNotNull(credential, "credential");
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((credential == null) ? 0 : credential.hashCode());
            result = prime * result
                    + ((identity == null) ? 0 : identity.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Credential other = (Credential) obj;
            if (credential == null) {
                if (other.credential != null)
                    return false;
            } else if (!credential.equals(other.credential))
                return false;
            if (identity == null) {
                if (other.identity != null)
                    return false;
            } else if (!identity.equals(other.identity))
                return false;
            return true;
        }

        public String getIdentity() {
            return identity;
        }

        public String getCredential() {
            return credential;
        }
    }
    
    @GwtIncompatible(value = "java.util.regex.Pattern")
    private static class MatchesPattern implements Predicate<String> {
        private final Pattern pattern;
        
        private MatchesPattern(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean apply(String input) {
            return pattern.matcher(input).matches();
        }
        
        private static MatchesPattern matches(Pattern pattern) {
            return new MatchesPattern(pattern);
        }
    }
}
