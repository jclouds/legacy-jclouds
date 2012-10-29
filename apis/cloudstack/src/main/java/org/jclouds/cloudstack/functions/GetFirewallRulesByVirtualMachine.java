package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.FirewallRule;
import org.jclouds.cloudstack.options.ListFirewallRulesOptions;

@Singleton
public class GetFirewallRulesByVirtualMachine extends CacheLoader<String, Set<FirewallRule>> {
   private final CloudStackClient client;

   @Inject
   public GetFirewallRulesByVirtualMachine(CloudStackClient client) {
      this.client = checkNotNull(client, "client");
   }

   /**
    * @throws org.jclouds.rest.ResourceNotFoundException
    *          when there is no ip forwarding rule available for the VM
    */
   @Override
   public Set<FirewallRule> load(String input) {
      String publicIPId = client.getVirtualMachineClient().getVirtualMachine(input).getPublicIPId();
      Set<FirewallRule> rules = client.getFirewallClient()
         .listFirewallRules(ListFirewallRulesOptions.Builder.ipAddressId(publicIPId));
      return rules != null ? rules : ImmutableSet.<FirewallRule>of();
   }
}
