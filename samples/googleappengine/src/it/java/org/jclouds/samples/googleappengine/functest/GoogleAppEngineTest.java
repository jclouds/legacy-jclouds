/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.samples.googleappengine.functest;

import com.google.appengine.tools.KickStart;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

@Test(groups = "integration", enabled = false, sequential = true, testName = "functionalTests")
public class GoogleAppEngineTest {
    Thread server;
    URL url;

    @BeforeTest
    @Parameters({"warfile", "devappserver.address", "devappserver.port"})
    public void startDevAppServer(final String warfile, final String address, final String port) throws Exception {
        url = new URL(String.format("http://%1s:%2s", address, port));
        Properties props = new Properties();
        props.put("jclouds.http.address", address);
        props.put("jclouds.http.port", port + "");
        props.put("jclouds.http.secure", "false");
        props.store(new FileOutputStream(String.format("%1s/WEB-INF/jclouds.properties", warfile)), "test");
        this.server = new Thread(new Runnable() {
            public void run() {
                KickStart.main(new String[]{
                        "com.google.appengine.tools.development.DevAppServerMain",
                        "--disable_update_check",
                        "-a", address,
                        "-p", port,
                        warfile});

            }

        });
        server.start();
        Thread.sleep(7 * 1000);
    }

    @AfterTest
    public void stopDevAppServer() throws Exception {
        server.stop();
    }

    public void shouldPass() throws InterruptedException, IOException {
        InputStream i = url.openStream();
        String string = IOUtils.toString(i);
        assert string.indexOf("Hello World!") >= 0 : string;
    }

    @Test(invocationCount = 50, enabled = false, threadPoolSize = 10)
    public void testGuiceJCloudsServed() throws InterruptedException, IOException {
        Thread.sleep(10000);
        URL gurl = new URL(url, "/guice/fetch?uri=/");
        InputStream i = gurl.openStream();
        String string = IOUtils.toString(i);
        assert string.indexOf("Hello World!") >= 0 : string;
    }
}
