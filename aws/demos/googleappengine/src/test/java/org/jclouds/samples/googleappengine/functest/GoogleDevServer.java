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
package org.jclouds.samples.googleappengine.functest;

import com.google.appengine.tools.KickStart;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.Properties;

/**
 * Basic functionality to start a local google app engine instance.
 *
 * @author Adrian Cole
 */
public class GoogleDevServer {

    Thread server;

    public void writePropertiesAndStartServer(final String address,
                                              final String port, final String warfile, Properties props)
            throws IOException, InterruptedException {
        String filename = String.format(
                "%1$s/WEB-INF/jclouds.properties", warfile);
        System.err.println("file: " + filename);
        props.store(new FileOutputStream(filename), "test");
        assert new File(filename).exists();
        this.server = new Thread(new Runnable() {
            public void run() {
                KickStart
                        .main(new String[]{
                                "com.google.appengine.tools.development.DevAppServerMain",
                                "--disable_update_check", "-a", address, "-p",
                                port, warfile});

            }

        });
        server.start();
        Thread.sleep(15 * 1000);
    }

    @SuppressWarnings("deprecation")
    public void stop() throws Exception {
        server.stop();
    }

}