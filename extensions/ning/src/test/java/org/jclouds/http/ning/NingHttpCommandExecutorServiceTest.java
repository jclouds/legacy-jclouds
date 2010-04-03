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
package org.jclouds.http.ning;

import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.*;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.testng.Assert.assertEquals;

import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.ImmutableMap;
import org.jclouds.http.BaseHttpCommandExecutorServiceIntegrationTest;
import org.jclouds.http.ning.config.NingHttpCommandExecutorServiceModule;
import org.jclouds.http.options.GetOptions;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * Tests the functionality of the {@link ApacheHCHttpCommandExecutorService}
 *
 * @author Adrian Cole
 */
@Test
public class NingHttpCommandExecutorServiceTest extends BaseHttpCommandExecutorServiceIntegrationTest {
    static {
        System.setProperty("http.conn-manager.timeout", 1000 + "");
    }

    protected Module createConnectionModule() {
        return new NingHttpCommandExecutorServiceModule();
    }

    protected void addConnectionProperties(Properties props) {
        props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, 20 + "");
        props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, 0 + "");
        props.setProperty(PROPERTY_CONNECTION_TIMEOUT, 100 + "");
        props.setProperty(PROPERTY_SO_TIMEOUT, 100 + "");
        props.setProperty(PROPERTY_IO_WORKER_THREADS, 3 + "");
        props.setProperty(PROPERTY_USER_THREADS, 0 + "");
    }

    @Test(invocationCount = 1, timeOut = 50000)
    public void testSpaceInUri() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
        assertEquals(client.synch("sp ace").trim(), XML);
    }

    @Override
    public void testGetBigFile() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
        //don't run it
    }
}