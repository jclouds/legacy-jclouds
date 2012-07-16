package org.jclouds.smartos.compute.domain;

import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HostAndPort;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.Provider;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.ssh.SshClient;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A host machine that runs smartOS
 */
@Singleton
public class SmartOSHost {
    protected final String hostname;
    protected final String username;
    protected final String password;

    protected SshClient.Factory sshClientFactory;

    private SshClient _connection;

    public static class HostException extends RuntimeException {
        public HostException(String s, Throwable throwable) {
            super(s, throwable);
        }

        public HostException(String s) {
            super(s);
        }
    }

    public static class NoResponseException extends Exception {
        public NoResponseException() {
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return builder().fromSmartOSHost(this);
    }

    public static class Builder {
        protected String hostname;
        protected String username;
        protected String password;
        protected SshClient.Factory sshFactory;

        public Builder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder sshFactory(SshClient.Factory sshFactory) {
            this.sshFactory = sshFactory;
            return this;
        }

        public SmartOSHost build() {
            return new SmartOSHost(hostname, username, password, sshFactory);
        }

        public Builder fromSmartOSHost(SmartOSHost in) {
            return this.hostname  ( in.getHostname() )
                       .username  ( in.getHostname() )
                       .password  ( in.getPassword() )
                       .sshFactory( in.getSshClientFactory() );
        }
    }

    @Inject
    protected SmartOSHost(@Provider Supplier<URI> provider,
                          @Nullable @Identity String identity,
                          @Nullable @Credential String credential,
                          SshClient.Factory sshFactory) {

        this.hostname = provider.get().getHost();
        this.username = identity;
        this.password = credential;
        this.sshClientFactory = sshFactory;
    }

    protected SmartOSHost(String hostname, String username, String password, SshClient.Factory sshClientFactory) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.sshClientFactory = sshClientFactory;
    }

    public String getDescription() {
        return "SmartOS@" + hostname;
    }

    public String getHostname() {
        return hostname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public SshClient.Factory getSshClientFactory() {
        return sshClientFactory;
    }

    protected SshClient getConnection() {
        if (_connection == null) {

            LoginCredentials credentials = new LoginCredentials.Builder()
                    .user(username)
                    .password(password)
                    .build();

            _connection = getSshClientFactory().create(
                    HostAndPort.fromParts(hostname, 22),
                    credentials
            );

            _connection.connect();

        }
        return _connection;
    }


    public String exec(String cmd) {
        return getConnection().exec(cmd).getOutput();
    }

    public String vmList()  {
        return exec("vmadm list -p");
    }


    public Map<String, String> getVMIpAddresses(UUID vmId)
    {
        ImmutableMap.Builder<String, String> netMapBuilder = ImmutableMap.builder();

        String response = getConnection().exec("zlogin " + vmId.toString() + " ifconfig -a4").getOutput();

        if( response.length() == 0)
            return ImmutableMap.of();

        Iterable<String> strings = Splitter.on("\n").split(response);
        Pattern inetMatcher = Pattern.compile("inet [0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");

        String iface = "";
        for(String line : strings )
        {
            if( line.length() > 0 && Character.isLetterOrDigit(line.charAt(0)) )
            {
                iface = line.substring(0, line.indexOf(":") );
            }
            else
            {
                Matcher matcher = inetMatcher.matcher(line);
                if( matcher.find() )
                    netMapBuilder.put(iface, matcher.group().substring(5));
            }
        }

        return netMapBuilder.build();

    }

    /**
     * What remotely available images are there in the cloud?
     *
     * @return Collection of datasets
     */
    public Iterable<DataSet> getAvailableImages() {
        return toSpecList(exec("dsadm avail"));
    }

    public Iterable<DataSet> getLocalDatasets() {
        return toSpecList(exec("dsadm list"));
    }

    public Iterable<VM> getVMs() {
        return toVMList(exec("vmadm list -p"));
    }

    public VM createVM(VmSpecification specification) {

        String response = getConnection().exec("(cat <<END\n" +
                specification.toJSONSpecification() + "\nEND\n) | vmadm create").getOutput();

        Pattern uuidPattern = Pattern.compile("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
        Matcher matcher = uuidPattern.matcher(response);
        if (matcher.find()) {
            String uuid = matcher.group();
            return getVM( UUID.fromString(uuid) );
        } else {
            throw new HostException("Error creating Host: response = " + response + "\n source = " + specification.toJSONSpecification());
        }

    }

    private Iterable<DataSet> toSpecList(String string) {

        try {
            BufferedReader r = new BufferedReader(new StringReader(string));
            r.readLine(); // Skip
            String line;
            ImmutableList.Builder resultBuilder = ImmutableList.builder();
            while ((line = r.readLine()) != null) {
                DataSet dataset = DataSet.builder().fromDsadmString(line).build();

                resultBuilder.add(dataset);
            }
            return resultBuilder.build();
        } catch (IOException e) {
            throw new HostException("Error parsing response when building spec list", e);
        }
    }

    private Iterable<VM> toVMList(String string) {
        try {
            BufferedReader r = new BufferedReader(new StringReader(string));
            String line;
            ImmutableList.Builder resultBuilder = ImmutableList.builder();
            while ((line = r.readLine()) != null) {
                VM vm = VM.builder().host(this).fromVmadmString(line).build();

                resultBuilder.add(vm);
            }
            return resultBuilder.build();
        } catch (IOException e) {
            throw new HostException("Error parsing response when building VM list", e);
        }
    }

    public VM getVM(UUID serverId) {
        for (VM vm : getVMs())
            if (vm.uuid.equals(serverId))
                return vm;
        return null;
    }

    public DataSet getDataSet(UUID imageId) {
        for (DataSet ds : getLocalDatasets()) {
            if (ds.getUuid().equals(imageId))
                return ds;
        }
        return null;
    }

    public void destroyHost(UUID uuid) {
        exec("vmadm delete " + uuid.toString());
    }

    public void rebootHost(UUID uuid) {
        exec("vmadm reboot " + uuid.toString());
    }

    public void stopHost(UUID uuid) {
        exec("vmadm stop -p");
    }

    public void startHost(UUID uuid) {
        exec("vmadm start " + uuid.toString());
    }
}
