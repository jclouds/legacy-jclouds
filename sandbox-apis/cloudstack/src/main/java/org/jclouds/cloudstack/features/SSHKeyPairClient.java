package org.jclouds.cloudstack.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.cloudstack.domain.SSHKeyPair;
import org.jclouds.cloudstack.options.ListSSHKeyPairsOptions;
import org.jclouds.concurrent.Timeout;

/**
 * Provides synchronous access to CloudStack SSHKeyPair features.
 * <p/>
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.8/user/listSSHKeyPairs.html"
 *      />
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface SSHKeyPairClient {

   Set<SSHKeyPair> listSSHKeyPairs(ListSSHKeyPairsOptions... options);

   SSHKeyPair createSSHKeyPair(String name);

   SSHKeyPair getSSHKeyPair(String name);
}