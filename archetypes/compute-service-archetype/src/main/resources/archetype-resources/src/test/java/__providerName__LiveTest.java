#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package};

import java.io.IOException;
import java.net.URI;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * End to end live test for ${providerName}
 *
 * @author ${author}
 */
@Test(groups = "live", testName = "${lcaseProviderName}.${providerName}LiveTest")
public class ${providerName}LiveTest {
    private ${providerName}Client client;

    @BeforeGroups(groups = { "live" })
    public void setupClient() {
        String endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"), 
                "jclouds.test.endpoint");        
        String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
        String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
        
        client = new ${providerName}ContextBuilder(
                new ${providerName}PropertiesBuilder(URI.create(endpoint), user, password).build())
                .withModules(new Log4JLoggingModule()).buildContext().getApi();
    }

    /**
     * Tests server start, reboot and deletion.
     * TODO: describe additional services tested
     */
    @Test(enabled=true)
    public void testServerLifecycle() {
        /*
         * TODO: implement
         */
    }

    /**
     * Tests common server image operations.
     */
    @Test(enabled=true)
    public void testImageLifecycle() {
        /*
         * TODO: implement
         */
    }

    @Test(enabled=true)
    public void testShellAccess() throws IOException {
        /*
         * TODO: implement
         */
    }

    /**
     * In case anything went wrong during the tests, removes the objects
     * created in the tests.
     */
    @AfterTest
    public void cleanup() {
        /*
         * TODO: implement
         */
    }

}
