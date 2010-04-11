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
package org.jclouds.compute.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;

import java.io.ByteArrayInputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.concurrent.ConcurrentUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

/**
 *
 * @author Adrian Cole
 */
public class ComputeUtils {
    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;
    @Inject(optional = true)
    private SshClient.Factory sshFactory;
    protected final Predicate<SshClient> runScriptNotRunning;
    private final Predicate<InetSocketAddress> socketTester;
    private final ExecutorService executor;

    @Inject
    public ComputeUtils(Predicate<InetSocketAddress> socketTester,
                        @Named("NOT_RUNNING") Predicate<SshClient> runScriptNotRunning,
                        @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
        this.socketTester = socketTester;
        this.runScriptNotRunning = runScriptNotRunning;
        this.executor = executor;
    }

    public static Iterable<? extends ComputeMetadata> filterByName(
            Iterable<? extends ComputeMetadata> nodes, final String name) {
        return Iterables.filter(nodes, new Predicate<ComputeMetadata>() {
            @Override
            public boolean apply(ComputeMetadata input) {
                return input.getName().equalsIgnoreCase(name);
            }
        });
    }

    public static final Comparator<InetAddress> ADDRESS_COMPARATOR = new Comparator<InetAddress>() {

        @Override
        public int compare(InetAddress o1, InetAddress o2) {
            return (o1 == o2) ? 0 : o1.getHostAddress().compareTo(o2.getHostAddress());
        }

    };

    public void runOptionsOnNode(NodeMetadata node, TemplateOptions options) {
        List<SshCallable<?>> callables = Lists.newArrayList();
        if (options.getRunScript() != null) {
            callables.add(runScriptOnNode(node, "runscript.sh", options.getRunScript()));
        }
        if (options.getPublicKey() != null) {
            callables.add(authorizeKeyOnNode(node, options.getPublicKey()));
        }
        // changing the key "MUST" come last or else the other commands may fail.
        if (callables.size() > 0 || options.getPrivateKey() != null) {
            runCallablesOnNode(node, callables, options.getPrivateKey() != null ? installKeyOnNode(
                    node, options.getPrivateKey()) : null);
        }
    }

    public InstallRSAPrivateKey installKeyOnNode(NodeMetadata node, String privateKey) {
        return new InstallRSAPrivateKey(node, privateKey);
    }

    public AuthorizeRSAPublicKey authorizeKeyOnNode(NodeMetadata node, String publicKey) {
        return new AuthorizeRSAPublicKey(node, publicKey);
    }

    public RunScriptOnNode runScriptOnNode(NodeMetadata node, String scriptName, byte[] script) {
        return new RunScriptOnNode(runScriptNotRunning, node, scriptName, script);
    }

    public Map<SshCallable<?>, ?> runCallablesOnNode(NodeMetadata node, Iterable<? extends SshCallable<?>> parallel,
                                   @Nullable SshCallable<?> last) {
        checkState(this.sshFactory != null, "runScript requested, but no SshModule configured");

        InetSocketAddress socket = new InetSocketAddress(Iterables.get(node.getPublicAddresses(), 0),
                22);
        socketTester.apply(socket);
        SshClient ssh = isKeyAuth(node) ? sshFactory.create(socket, node.getCredentials().account,
                node.getCredentials().key.getBytes()) : sshFactory.create(socket, node
                .getCredentials().account, node.getCredentials().key);
        for (int i = 0; i < 3; i++) {
            try {
                ssh.connect();
                Map<SshCallable<?>, ListenableFuture<?>> responses = Maps.newHashMap();

                for (SshCallable<?> callable : parallel) {
                    callable.setConnection(ssh, logger);
                    responses.put(callable, ConcurrentUtils.makeListenable(executor.submit(callable),
                            executor));
                }

                Map<SshCallable<?>, Exception> exceptions = awaitCompletion(responses, executor, null,
                        logger, "ssh");
                if (exceptions.size() > 0)
                    throw new RuntimeException(String.format("error invoking callables on host %s: %s",
                            socket, exceptions));
                if (last != null) {
                    last.setConnection(ssh, logger);
                    try {
                        last.call();
                    } catch (Exception e) {
                        Throwables.propagate(e);
                    }
                }
                return transform(responses);
            } catch (RuntimeException from) {
                if (Iterables.size(Iterables.filter(Throwables.getCausalChain(from),
                        ConnectException.class)) >= 1// auth fail sometimes happens in EC2
                        || Throwables.getRootCause(from).getMessage().indexOf("Auth fail") != -1
                        || Throwables.getRootCause(from).getMessage().indexOf("invalid privatekey") != -1) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw Throwables.propagate(e);
                    }
                    continue;
                }
                throw Throwables.propagate(from);
            } finally {
                if (ssh != null)
                    ssh.disconnect();
            }
        }
        throw new RuntimeException(String.format("Couldn't connect to node %s and run the script", node.getId()));
    }

    public <T> Map<SshCallable<?>, T> transform(Map<SshCallable<?>, ListenableFuture<?>> responses) {
        Map<SshCallable<?>, T> actualResponses = Maps.newHashMap();
        for(Map.Entry<SshCallable<?>, ListenableFuture<?>> entry : responses.entrySet()) {
            try {
            actualResponses.put(entry.getKey(), (T) entry.getValue().get());
            } catch(InterruptedException e) {
                throw Throwables.propagate(e);
            } catch(ExecutionException e) {
                throw Throwables.propagate(e);
            }
        }
        return actualResponses;
    }

    public static interface SshCallable<T> extends Callable<T> {
        void setConnection(SshClient ssh, Logger logger);
    }

    public static class RunScriptOnNode implements SshCallable<ExecResponse> {
        private SshClient ssh;
        protected final Predicate<SshClient> runScriptNotRunning;
        private final NodeMetadata node;
        private final String scriptName;
        private final byte[] script;
        private Logger logger = Logger.NULL;

        RunScriptOnNode(@Named("NOT_RUNNING") Predicate<SshClient> runScriptNotRunning,
                        NodeMetadata node, String scriptName, byte[] script) {
            this.runScriptNotRunning = runScriptNotRunning;
            this.node = checkNotNull(node, "node");
            this.scriptName = checkNotNull(scriptName, "scriptName");
            this.script = new InitBuilder("runscript", "/tmp", "/tmp", ImmutableMap
                    .<String, String> of(), Iterables.toArray(Splitter.on("\n").split(
                    new String(checkNotNull(script, "script"))), String.class)).build(OsFamily.UNIX)
                    .getBytes();
        }

        @Override
        public ExecResponse call() throws Exception {
            ssh.put(scriptName, new ByteArrayInputStream(script));
            ExecResponse returnVal = ssh.exec("chmod 755 " + scriptName);
            returnVal = ssh.exec("./" + scriptName + " init");
            if (node.getCredentials().account.equals("root")) {
                logger.debug(">> running %s as %s@%s", scriptName, node.getCredentials().account,
                        Iterables.get(node.getPublicAddresses(), 0).getHostAddress());
                returnVal = ssh.exec("./" + scriptName + " start");
            } else if (isKeyAuth(node)) {
                logger.debug(">> running sudo %s as %s@%s", scriptName, node.getCredentials().account,
                        Iterables.get(node.getPublicAddresses(), 0).getHostAddress());
                returnVal = ssh.exec("sudo ./" + scriptName + " start");
            } else {
                logger.debug(">> running sudo -S %s as %s@%s", scriptName,
                        node.getCredentials().account, Iterables.get(node.getPublicAddresses(), 0)
                                .getHostAddress());
                returnVal = ssh.exec(String.format("echo %s|sudo -S ./%s", node.getCredentials().key,
                        scriptName + " start"));
            }
            runScriptNotRunning.apply(ssh);
            logger.debug("<< complete(%d)", returnVal.getExitCode());
            return returnVal;
        }

        @Override
        public void setConnection(SshClient ssh, Logger logger) {
            this.logger = checkNotNull(logger, "logger");
            this.ssh = checkNotNull(ssh, "ssh");
        }
    }

    public static class InstallRSAPrivateKey implements SshCallable<ExecResponse> {
        private SshClient ssh;
        private final NodeMetadata node;
        private final String privateKey;

        private Logger logger = Logger.NULL;

        InstallRSAPrivateKey(NodeMetadata node, String privateKey) {
            this.node = checkNotNull(node, "node");
            this.privateKey = checkNotNull(privateKey, "privateKey");
        }

        @Override
        public ExecResponse call() throws Exception {
            ssh.exec("mkdir .ssh");
            ssh.put(".ssh/id_rsa", new ByteArrayInputStream(privateKey.getBytes()));
            logger.debug(">> installing rsa key for %s@%s", node.getCredentials().account, Iterables
                    .get(node.getPublicAddresses(), 0).getHostAddress());
            return ssh.exec("chmod 600 .ssh/id_rsa");
        }

        @Override
        public void setConnection(SshClient ssh, Logger logger) {
            this.logger = checkNotNull(logger, "logger");
            this.ssh = checkNotNull(ssh, "ssh");
        }

    }

    public static class AuthorizeRSAPublicKey implements SshCallable<ExecResponse> {
        private SshClient ssh;
        private final NodeMetadata node;
        private final String publicKey;

        private Logger logger = Logger.NULL;

        AuthorizeRSAPublicKey(NodeMetadata node, String publicKey) {
            this.node = checkNotNull(node, "node");
            this.publicKey = checkNotNull(publicKey, "publicKey");
        }

        @Override
        public ExecResponse call() throws Exception {
            ssh.exec("mkdir .ssh");
            ssh.put(".ssh/id_rsa.pub", new ByteArrayInputStream(publicKey.getBytes()));
            logger.debug(">> authorizing rsa public key for %s@%s", node.getCredentials().account,
                    Iterables.get(node.getPublicAddresses(), 0).getHostAddress());
            ExecResponse returnVal = ssh.exec("cat .ssh/id_rsa.pub >> .ssh/authorized_keys");
            returnVal = ssh.exec("chmod 600 .ssh/authorized_keys");
            logger.debug("<< complete(%d)", returnVal.getExitCode());
            return returnVal;
        }

        @Override
        public void setConnection(SshClient ssh, Logger logger) {
            this.logger = checkNotNull(logger, "logger");
            this.ssh = checkNotNull(ssh, "ssh");
        }

    }

    public static boolean isKeyAuth(NodeMetadata createdNode) {
        return createdNode.getCredentials().key != null
                && createdNode.getCredentials().key.startsWith("-----BEGIN RSA PRIVATE KEY-----");
    }

    /**
     * Given the instances of {@link NodeMetadata} (immutable)
     * and {@link Credentials} (immutable), returns a new instance of {@link NodeMetadata}
     * that has new credentials
     */
    public static NodeMetadata installNewCredentials(NodeMetadata node, Credentials newCredentials) {
        return new NodeMetadataImpl(node.getId(), node.getName(), node.getLocationId(), node.getUri(),
                node.getUserMetadata(), node.getTag(), node.getState(), node. getPublicAddresses(),
                node.getPrivateAddresses(), node.getExtra(), newCredentials);
    }
}
