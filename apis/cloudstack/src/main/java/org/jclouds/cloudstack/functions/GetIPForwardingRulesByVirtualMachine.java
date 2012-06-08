package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.IPForwardingRule;

import com.google.common.cache.CacheLoader;

@Singleton
public class GetIPForwardingRulesByVirtualMachine extends CacheLoader<String, Set<IPForwardingRule>> {
   private final CloudStackClient client;

   @Inject
   public GetIPForwardingRulesByVirtualMachine(CloudStackClient client) {
      this.client = checkNotNull(client, "client");
   }

   /**
    * @throws org.jclouds.rest.ResourceNotFoundException
    *          when there is no ip forwarding rule available for the VM
    */
   @Override
   public Set<IPForwardingRule> load(String input) {
      return client.getNATClient().getIPForwardingRulesForVirtualMachine(input);
   }
}
