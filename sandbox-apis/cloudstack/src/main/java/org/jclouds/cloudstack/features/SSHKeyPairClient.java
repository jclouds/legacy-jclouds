package org.jclouds.cloudstack.features;

import org.jclouds.concurrent.Timeout;

import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to CloudStack SSHKeyPair features.
 * <p/>
 *
 * @author Adrian Cole
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.8/user/listSSHKeyPairs.html" />
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)

public interface SSHKeyPairClient {

    String listSSHKeyPairs();

    String createSSHKeyPair(String name);

}
