/*
 * @(#)StaxSdkAppServer2.java     25 May 2011
 *
 * Copyright © 2010 Andrew Phillips.
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.demo.tweetstore.integration;

import static com.google.common.base.Predicates.instanceOf;
import static java.util.Arrays.asList;
import static org.jclouds.demo.tweetstore.integration.utils.ObjectFields.set;
import static org.jclouds.demo.tweetstore.integration.utils.ObjectFields.valueOf;

import java.io.File;
import java.io.IOException;
import java.util.Timer;

import javax.servlet.ServletException;

import net.stax.appserver.webapp.RequestMonitorValve;
import net.stax.appserver.webapp.WebAppEngine;

import org.apache.catalina.Engine;
import org.apache.commons.cli.ParseException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.staxnet.appserver.IAppServerConfiguration;
import com.staxnet.appserver.IEngineFactory;
import com.staxnet.appserver.ServerCallbackClient;
import com.staxnet.appserver.StaxAppServerBase;
import com.staxnet.appserver.StaxSdkAppServer;
import com.staxnet.appserver.StaxSdkAppServerCLI;
import com.staxnet.appserver.TomcatServerBase;
import com.staxnet.appserver.WarBasedServerConfiguration;
import com.staxnet.appserver.config.AppServerConfig;

class StaxSdkAppServer2 {
    // code more or less exactly from StaxSdkAppServer.java
    public static StaxSdkAppServer2 createServer(String[] args, String[] classPaths, 
            ClassLoader cl) throws ParseException, ServletException {
        StaxSdkAppServerCLI cli = StaxSdkAppServerCLI.parse(args);
        if (cli.getMissingOptions().length > 0) {
            throw new ParseException("Missing required options: " + cli.formatMissingOptions(", "));
        }

        String[] environments = cli.getEnvironment();
        File serverConfig = cli.getServerConfigFile();
        File baseDir = new File(cli.getBaseDir());
        File webRoot = new File(cli.getWebdir());
        File workDir = new File(baseDir, "work");

        File staxWebXml = new File(webRoot, "WEB-INF/cloudbees-web.xml");
        if (!(staxWebXml.exists()))
            staxWebXml = new File(webRoot, "WEB-INF/stax-web.xml");
        IAppServerConfiguration config = WarBasedServerConfiguration.load(
                serverConfig, webRoot, staxWebXml, environments);
        // force the RequestMonitorValve to sleep for only a short period
        set("statusInterval", StaxReflect.getAppServerConfig(config), 5);
        StaxSdkAppServer server = new StaxSdkAppServer(
                baseDir.getAbsolutePath(), workDir.getAbsolutePath(), cl,
                classPaths, cli.getPort(), config, cli.getRepositoryPath());
        return new StaxSdkAppServer2(server);
    }
    
    private final StaxSdkAppServer server;
    private Thread serverThread;
    
    private StaxSdkAppServer2(StaxSdkAppServer server) {
        this.server = server;
        serverThread = new Thread(new Runnable() {
            public void run() {
                try {
                    StaxSdkAppServer2.this.server.start();
                } catch (ServletException exception) {
                    System.err.println("exception starting server: " + exception);
                }
            }
        });
    }
    
    void start() throws ServletException {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                stop();
            }
        }));
        serverThread.start();
    }
    
    void stop() {
        server.stop();
        serverThread.interrupt();
        StaxReflect.getStaxAppQueryTimer(server).cancel();
        KillerCallback requestMonitorAssassin = new KillerCallback(StaxReflect.getRequestMonitorTimerCallback(server));
        /*
         * Hoping for the best here in terms of visibility - we're setting a variable in a
         * different thread which isn't guaranteed to see the change. 
         * But we can't set the callbackClient before serverThread starts (which would create
         * a happens-before relationship) because the objects on which the callbackClient is 
         * set have not been created yet at that point.
         */
        set("callbackClient", StaxReflect.getRequestMonitorTimer(server), requestMonitorAssassin);
        requestMonitorAssassin.setToKill();
    }
    
    private class KillerCallback extends ServerCallbackClient {
        private final ServerCallbackClient delegate;
        private volatile boolean killCaller = false;
        
        private KillerCallback(ServerCallbackClient delegate) {
            super("", "");
            this.delegate = delegate;
        }
        
        @Override
        public AuthenticationResult getApplicationTicket(String username,
                String password) throws IOException {
            return delegate.getApplicationTicket(username, password);
        }

        @Override
        public AuthenticationResult renewApplicationTicket(String userAuthTicket)
                throws IOException {
            return delegate.renewApplicationTicket(userAuthTicket);
        }

        @Override
        public void updateStatus(State state) throws ServletException,
                IOException {
            if (killCaller) {
                throw new ThreadDeath();
            }
            delegate.updateStatus(state);
        }
        
        private void setToKill() {
            killCaller = true;
        }
    }
    
    private static class StaxReflect {
        private static WebAppEngine getWebAppEngine(StaxSdkAppServer server) {
            return (WebAppEngine) Iterables.find(asList((IEngineFactory[]) 
                    valueOf("engineFactories", server, StaxAppServerBase.class)), 
                    instanceOf(WebAppEngine.class));
        }
        
        private static Timer getStaxAppQueryTimer(StaxSdkAppServer server) {
            return (Timer) valueOf("timer", getWebAppEngine(server));
        }
        
        private static AppServerConfig getAppServerConfig(IAppServerConfiguration config) {
            return (AppServerConfig) valueOf("appServerConfig", config);            
        }
        
        private static Engine getLocalEngine(StaxSdkAppServer server) {
            return (Engine) Iterables.find(asList((Engine[]) 
                    valueOf("engines", valueOf("container", server, TomcatServerBase.class))), 
                    new Predicate<Engine>() {
                        @Override
                        public boolean apply(Engine input) {
                            return input.getName().equals("localEngine");
                        }
                    });
        }
        
        private static Runnable getRequestMonitorTimer(StaxSdkAppServer server) {
            return (Runnable) valueOf("idleTimer", Iterables.find(
                    asList(getLocalEngine(server).getPipeline().getValves()),
                    instanceOf(RequestMonitorValve.class)));
        }
        
        private static ServerCallbackClient getRequestMonitorTimerCallback(
                StaxSdkAppServer server) {
            return (ServerCallbackClient) valueOf("callbackClient", 
                    getRequestMonitorTimer(server));
        }
    }
}
