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
package org.jclouds.demo.tweetstore.integration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;

import org.apache.commons.cli.ParseException;

/**
 * Basic functionality to start a local google app engine instance.
 *
 * @author Adrian Cole
 */
public class RunAtCloudServer {
    protected StaxSdkAppServer2 server;

    public void writePropertiesAndStartServer(final String address, final String port, 
            final String warfile, final String environments, 
            final String serverBaseDirectory, Properties props) throws IOException, InterruptedException, ParseException, ServletException {
        String filename = String.format(
                "%1$s/WEB-INF/jclouds.properties", warfile);
        System.err.println("file: " + filename);
        props.store(new FileOutputStream(filename), "test");
        assert new File(filename).exists();
        server = StaxSdkAppServer2.createServer(new String[] { "-web", warfile, "-port", port, "-env", environments,
                "-dir", serverBaseDirectory }, new String[0], Thread.currentThread().getContextClassLoader());
        server.start();
        TimeUnit.SECONDS.sleep(30);
    }

    public void stop() throws Exception {
        server.stop();
    }

}