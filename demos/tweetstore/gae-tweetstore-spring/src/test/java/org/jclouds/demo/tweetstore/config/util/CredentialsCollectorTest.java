/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.demo.tweetstore.config.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Properties;

import org.jclouds.demo.tweetstore.config.util.CredentialsCollector;
import org.jclouds.demo.tweetstore.config.util.CredentialsCollector.Credential;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code CredentialsCollector}
 * 
 * @author Andrew Phillips
 */
@Test(groups = "unit")
public class CredentialsCollectorTest {
    private CredentialsCollector collector = new CredentialsCollector();
    
    public void testEmptyProperties() {
        assertTrue(collector.apply(new Properties()).isEmpty(), 
                "Expected returned map to be empty");
    }

    public void testNoCredentials() {
        Properties properties = propertiesOf(ImmutableMap.of("not-an-identity", 
                "v1", "not-a-credential", "v2"));
        assertTrue(collector.apply(properties).isEmpty(), 
                "Expected returned map to be empty");
    }
    
    private static Properties propertiesOf(Map<String, String> entries) {
        Properties properties = new Properties();
        properties.putAll(entries);
        return properties;
    }
    
    public void testNonMatchingCredentials() {
        Properties properties = propertiesOf(ImmutableMap.of("non_matching.identity", "v1",
                "non_matching.credential", "v2"));
        assertTrue(collector.apply(properties).isEmpty(), 
                "Expected returned map to be empty");
    }
    
    public void testIncompleteCredentials() {
        Properties properties = propertiesOf(ImmutableMap.of("acme.identity", "v1",
                "acme-2.credential", "v2"));
        assertTrue(collector.apply(properties).isEmpty(), 
                "Expected returned map to be empty");
    }
    
    public void testCredentials() {
        Properties properties = propertiesOf(ImmutableMap.of("acme.identity", "v1",
                "acme.credential", "v2", "acme-2.identity", "v3",
                "acme-2.credential", "v4"));
        assertEquals(collector.apply(properties), 
                ImmutableMap.of("acme", new Credential("v1", "v2"),
                        "acme-2", new Credential("v3", "v4")));        
    }
}