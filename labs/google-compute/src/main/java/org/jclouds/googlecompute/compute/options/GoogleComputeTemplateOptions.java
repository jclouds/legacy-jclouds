package org.jclouds.googlecompute.compute.options;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.googlecompute.domain.Instance;
import org.jclouds.scriptbuilder.domain.Statement;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Optional.fromNullable;
import static org.jclouds.googlecompute.domain.Instance.ServiceAccount;

/**
 * Instance options specific to Google Compute Engine.
 *
 * @author David Alves
 */
public class GoogleComputeTemplateOptions extends TemplateOptions {

   private Optional<URI> network = Optional.absent();
   private Optional<String> networkName = Optional.absent();
   private Set<Instance.ServiceAccount> serviceAccounts = Sets.newLinkedHashSet();
   private boolean enableNat = true;

   @Override
   public GoogleComputeTemplateOptions clone() {
      GoogleComputeTemplateOptions options = new GoogleComputeTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof GoogleComputeTemplateOptions) {
         GoogleComputeTemplateOptions eTo = GoogleComputeTemplateOptions.class.cast(to);
         eTo.network(getNetwork().orNull());
         eTo.network(getNetworkName().orNull());
         eTo.serviceAccounts(getServiceAccounts());
         eTo.enableNat(isEnableNat());
      }
   }

   /**
    * @see #getNetworkName()
    */
   public GoogleComputeTemplateOptions network(String networkName) {
      this.networkName = fromNullable(networkName);
      return this;
   }

   /**
    * @see #getNetwork()
    */
   public GoogleComputeTemplateOptions network(URI network) {
      this.network = fromNullable(network);
      return this;
   }

   /**
    * @see #getServiceAccounts()
    * @see ServiceAccount
    */
   public GoogleComputeTemplateOptions addServiceAccount(ServiceAccount serviceAccout) {
      this.serviceAccounts.add(serviceAccout);
      return this;
   }

   /**
    * @see #getServiceAccounts()
    * @see ServiceAccount
    */
   public GoogleComputeTemplateOptions serviceAccounts(Set<ServiceAccount> serviceAccounts) {
      this.serviceAccounts = Sets.newLinkedHashSet(serviceAccounts);
      return this;
   }

   /**
    * @see #isEnableNat()
    */
   public GoogleComputeTemplateOptions enableNat(boolean enableNat) {
      this.enableNat = enableNat;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions blockOnPort(int port, int seconds) {
      return GoogleComputeTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions inboundPorts(int... ports) {
      return GoogleComputeTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions authorizePublicKey(String publicKey) {
      return GoogleComputeTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions installPrivateKey(String privateKey) {
      return GoogleComputeTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return GoogleComputeTemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions dontAuthorizePublicKey() {
      return GoogleComputeTemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions nameTask(String name) {
      return GoogleComputeTemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions runAsRoot(boolean runAsRoot) {
      return GoogleComputeTemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions runScript(Statement script) {
      return GoogleComputeTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return GoogleComputeTemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions overrideLoginPassword(String password) {
      return GoogleComputeTemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions overrideLoginPrivateKey(String privateKey) {
      return GoogleComputeTemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions overrideLoginUser(String loginUser) {
      return GoogleComputeTemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return GoogleComputeTemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return GoogleComputeTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions userMetadata(String key, String value) {
      return GoogleComputeTemplateOptions.class.cast(super.userMetadata(key, value));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions tags(Iterable<String> tags) {
      return GoogleComputeTemplateOptions.class.cast(super.tags(tags));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions wrapInInitScript(boolean wrapInInitScript) {
      return GoogleComputeTemplateOptions.class.cast(super.wrapInInitScript(wrapInInitScript));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions runScript(String script) {
      return GoogleComputeTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeTemplateOptions blockOnComplete(boolean blockOnComplete) {
      return GoogleComputeTemplateOptions.class.cast(super.blockOnComplete(blockOnComplete));
   }

   /**
    * @return the ServiceAccounts to enable in the instances.
    */
   public Set<Instance.ServiceAccount> getServiceAccounts() {
      return serviceAccounts;
   }

   /**
    * @return the URI of an existing network the instances will be attached to. If no network URI or network name are
    *         provided a new network will be created for the project.
    */
   public Optional<URI> getNetwork() {
      return network;
   }

   /**
    * @return the name of an existing network the instances will be attached to, the network is assumed to belong to
    *         user's project. If no network URI network name are provided a new network will be created for the project.
    */
   public Optional<String> getNetworkName() {
      return networkName;
   }

   /**
    * @return whether an AccessConfig with Type ONE_TO_ONE_NAT should be enabled in the instances. When true
    *         instances will have a NAT address that will be publicly accessible.
    */
   public boolean isEnableNat() {
      return enableNat;
   }
}
