/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.environment;

import static com.google.common.collect.Iterables.find;
import static org.jclouds.abiquo.reference.AbiquoTestConstants.PREFIX;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

import org.jclouds.abiquo.AbiquoContext;
import org.jclouds.abiquo.config.AbiquoEdition;
import org.jclouds.abiquo.domain.config.License;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.enterprise.Limits;
import org.jclouds.abiquo.domain.enterprise.Role;
import org.jclouds.abiquo.domain.enterprise.User;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.infrastructure.Datastore;
import org.jclouds.abiquo.domain.infrastructure.Machine;
import org.jclouds.abiquo.domain.infrastructure.ManagedRack;
import org.jclouds.abiquo.domain.infrastructure.Rack;
import org.jclouds.abiquo.domain.infrastructure.RemoteService;
import org.jclouds.abiquo.domain.infrastructure.StorageDevice;
import org.jclouds.abiquo.domain.infrastructure.StorageDeviceMetadata;
import org.jclouds.abiquo.domain.infrastructure.StoragePool;
import org.jclouds.abiquo.domain.infrastructure.Tier;
import org.jclouds.abiquo.domain.network.ExternalNetwork;
import org.jclouds.abiquo.domain.network.PublicNetwork;
import org.jclouds.abiquo.domain.network.UnmanagedNetwork;
import org.jclouds.abiquo.features.AdminApi;
import org.jclouds.abiquo.features.ConfigApi;
import org.jclouds.abiquo.features.EnterpriseApi;
import org.jclouds.abiquo.features.InfrastructureApi;
import org.jclouds.abiquo.features.services.AdministrationService;
import org.jclouds.abiquo.predicates.enterprise.RolePredicates;
import org.jclouds.abiquo.predicates.enterprise.UserPredicates;
import org.jclouds.abiquo.predicates.infrastructure.RemoteServicePredicates;
import org.jclouds.abiquo.predicates.infrastructure.StorageDeviceMetadataPredicates;
import org.jclouds.abiquo.predicates.infrastructure.StoragePoolPredicates;
import org.jclouds.abiquo.predicates.infrastructure.TierPredicates;
import org.jclouds.abiquo.util.Config;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;

/**
 * Test environment for infrastructure live tests.
 * 
 * @author Ignasi Barrera
 */
public class InfrastructureTestEnvironment implements TestEnvironment {
   /** The rest context. */
   public AbiquoContext context;

   // Environment data made public so tests can use them easily

   public AdministrationService administrationService;

   public InfrastructureApi infrastructureApi;

   public EnterpriseApi enterpriseApi;

   public AdminApi adminApi;

   public ConfigApi configApi;

   // Resources

   public License license;

   public Datacenter datacenter;

   public PublicNetwork publicNetwork;

   public ExternalNetwork externalNetwork;

   public UnmanagedNetwork unmanagedNetwork;

   public List<RemoteService> remoteServices;

   public Rack rack;

   public Machine machine;

   public Enterprise enterprise;

   public StorageDevice storageDevice;

   public StoragePool storagePool;

   public Tier tier;

   public User user;

   public User enterpriseAdmin;

   public Role role;

   public Role anotherRole;

   public ManagedRack ucsRack;

   public InfrastructureTestEnvironment(final AbiquoContext context) {
      super();
      this.context = context;
      this.administrationService = context.getAdministrationService();
      this.context = context;
      this.enterpriseApi = context.getApiContext().getApi().getEnterpriseApi();
      this.infrastructureApi = context.getApiContext().getApi().getInfrastructureApi();
      this.adminApi = context.getApiContext().getApi().getAdminApi();
      this.configApi = context.getApiContext().getApi().getConfigApi();
   }

   @Override
   public void setup() throws Exception {
      // Configuration
      createLicense();

      // Intrastructure
      createDatacenter();
      createRack();
      createMachine();
      createStorageDevice();
      createStoragePool();
      createPublicNetwork();

      // Enterprise
      createEnterprise();
      createRoles();
      createUsers();

      // Networking
      createExternalNetwork();
      createUnmanagedNetwork();
   }

   @Override
   public void tearDown() throws Exception {
      deleteUsers();

      deleteRole(role);
      deleteRole(anotherRole);

      deleteUnmanagedNetwork();
      deleteExternalNetwork();
      deletePublicNetwork();
      deleteStoragePool();
      deleteStorageDevice();
      deleteMachine();
      deleteUcsRack();
      deleteRack();
      deleteDatacenter();
      deleteEnterprise();

      deleteLicense();
   }

   // Setup

   protected void createLicense() throws IOException {
      license = License.builder(context.getApiContext(), readLicense()).build();

      license.add();
      assertNotNull(license.getId());
   }

   protected void createDatacenter() {
      // Assume a monolithic install
      URI endpoint = URI.create(context.getApiContext().getProviderMetadata().getEndpoint());
      String remoteServicesAddress = endpoint.getHost();

      datacenter = Datacenter.builder(context.getApiContext()).name(randomName()).location("Honolulu")
            .remoteServices(remoteServicesAddress, AbiquoEdition.ENTERPRISE).build();
      datacenter.save();
      assertNotNull(datacenter.getId());

      remoteServices = datacenter.listRemoteServices();
      assertEquals(remoteServices.size(), 7);
   }

   protected void createMachine() {
      String ip = Config.get("abiquo.hypervisor.address");
      HypervisorType type = HypervisorType.valueOf(Config.get("abiquo.hypervisor.type"));
      String user = Config.get("abiquo.hypervisor.user");
      String pass = Config.get("abiquo.hypervisor.pass");

      machine = datacenter.discoverSingleMachine(ip, type, user, pass);

      String vswitch = machine.findAvailableVirtualSwitch(Config.get("abiquo.hypervisor.vswitch"));
      machine.setVirtualSwitch(vswitch);

      Datastore datastore = machine.findDatastore(Config.get("abiquo.hypervisor.datastore"));
      datastore.setEnabled(true);

      machine.setRack(rack);
      machine.save();
      assertNotNull(machine.getId());
   }

   protected void createRack() {
      rack = Rack.builder(context.getApiContext(), datacenter).name(PREFIX + "Aloha").build();
      rack.save();
      assertNotNull(rack.getId());
   }

   public void createUcsRack() {
      String ip = Config.get("abiquo.ucs.address");
      Integer port = Integer.parseInt(Config.get("abiquo.ucs.port"));
      String user = Config.get("abiquo.ucs.user");
      String pass = Config.get("abiquo.ucs.pass");

      ucsRack = ManagedRack.builder(context.getApiContext(), datacenter).ipAddress(ip).port(port).user(user)
            .name("ucs rack").password(pass).build();

      ucsRack.save();
      assertNotNull(ucsRack.getId());
   }

   protected void createStorageDevice() {
      String ip = Config.get("abiquo.storage.address");
      String type = Config.get("abiquo.storage.type");
      String user = Config.get("abiquo.storage.user");
      String pass = Config.get("abiquo.storage.pass");

      List<StorageDeviceMetadata> devices = datacenter.listSupportedStorageDevices();
      StorageDeviceMetadata metadata = Iterables.find(devices, StorageDeviceMetadataPredicates.type(type));

      storageDevice = StorageDevice.builder(context.getApiContext(), datacenter) //
            .name(PREFIX + "Storage Device")//
            .type(type)//
            .managementIp(ip).managementPort(metadata.getDefaultManagementPort())//
            .iscsiIp(ip).iscsiPort(metadata.getDefaultIscsiPort()) //
            .username(user)//
            .password(pass) //
            .build();

      storageDevice.save();
      assertNotNull(storageDevice.getId());
   }

   protected void createStoragePool() {
      String pool = Config.get("abiquo.storage.pool");

      storagePool = storageDevice.findRemoteStoragePool(StoragePoolPredicates.name(pool));
      tier = datacenter.findTier(TierPredicates.name("Default Tier 1"));

      storagePool.setTier(tier);
      storagePool.save();

      assertNotNull(storagePool.getUUID());
   }

   protected void createUsers() {
      Role userRole = administrationService.findRole(RolePredicates.name("USER"));
      Role enterpriseAdminRole = administrationService.findRole(RolePredicates.name("ENTERPRISE_ADMIN"));

      user = User.builder(context.getApiContext(), enterprise, userRole).name(randomName(), randomName())
            .nick("jclouds").authType("ABIQUO").description(randomName()).email(randomName() + "@abiquo.com")
            .locale("en_US").password("user").build();

      user.save();
      assertNotNull(user.getId());
      assertEquals(userRole.getId(), user.getRole().getId());

      enterpriseAdmin = User.builder(context.getApiContext(), enterprise, enterpriseAdminRole)
            .name(randomName(), randomName()).nick("jclouds-admin").authType("ABIQUO").description(randomName())
            .email(randomName() + "@abiquo.com").locale("en_US").password("admin").build();

      enterpriseAdmin.save();
      assertNotNull(enterpriseAdmin.getId());
      assertEquals(enterpriseAdminRole.getId(), enterpriseAdmin.getRole().getId());
   }

   protected void createRoles() {
      role = Role.builder(context.getApiContext()).name(randomName()).blocked(false).build();
      role.save();

      anotherRole = Role.Builder.fromRole(role).build();
      anotherRole.setName("Another role");
      anotherRole.save();

      assertNotNull(role.getId());
      assertNotNull(anotherRole.getId());
   }

   protected void createEnterprise() {
      enterprise = Enterprise.builder(context.getApiContext()).name(randomName()).build();
      enterprise.save();
      assertNotNull(enterprise.getId());
      Limits limits = enterprise.allowDatacenter(datacenter);
      assertNotNull(limits);
   }

   protected void createPublicNetwork() {
      publicNetwork = PublicNetwork.builder(context.getApiContext(), datacenter).name("PublicNetwork")
            .gateway("80.80.80.1").address("80.80.80.0").mask(24).tag(5).build();
      publicNetwork.save();
      assertNotNull(publicNetwork.getId());
   }

   protected void createExternalNetwork() {
      externalNetwork = ExternalNetwork.builder(context.getApiContext(), datacenter, enterprise)
            .name("ExternalNetwork").gateway("10.0.0.1").address("10.0.0.0").mask(24).tag(7).build();
      externalNetwork.save();
      assertNotNull(externalNetwork.getId());
   }

   protected void createUnmanagedNetwork() {
      unmanagedNetwork = UnmanagedNetwork.builder(context.getApiContext(), datacenter, enterprise)
            .name("UnmanagedNetwork").gateway("10.0.1.1").address("10.0.1.0").mask(24).tag(8).build();
      unmanagedNetwork.save();
      assertNotNull(unmanagedNetwork.getId());
   }

   // Tear down

   protected void deleteUnmanagedNetwork() {
      if (unmanagedNetwork != null) {
         Integer id = unmanagedNetwork.getId();
         unmanagedNetwork.delete();
         assertNull(datacenter.getNetwork(id));
      }
   }

   protected void deleteExternalNetwork() {
      if (externalNetwork != null) {
         Integer id = externalNetwork.getId();
         externalNetwork.delete();
         assertNull(datacenter.getNetwork(id));
      }
   }

   protected void deletePublicNetwork() {
      if (publicNetwork != null) {
         Integer id = publicNetwork.getId();
         publicNetwork.delete();
         assertNull(datacenter.getNetwork(id));
      }
   }

   protected void deleteUsers() {
      if (user != null) {
         String nick = user.getNick();
         user.delete();
         // Nick is unique in an enterprise
         assertNull(enterprise.findUser(UserPredicates.nick(nick)));
      }

      if (enterpriseAdmin != null) {
         String nick = enterpriseAdmin.getNick();
         enterpriseAdmin.delete();
         // Nick is unique in an enterprise
         assertNull(enterprise.findUser(UserPredicates.nick(nick)));
      }
   }

   protected void deleteRole(final Role role) {
      if (role != null) {
         Integer roleId = role.getId();
         role.delete();
         assertNull(adminApi.getRole(roleId));
      }
   }

   protected void deleteStoragePool() {
      if (storagePool != null) {
         String idStoragePool = storagePool.getUUID();
         storagePool.delete();
         assertNull(infrastructureApi.getStoragePool(storageDevice.unwrap(), idStoragePool));
      }

   }

   protected void deleteStorageDevice() {
      if (storageDevice != null) {
         Integer idStorageDevice = storageDevice.getId();
         storageDevice.delete();
         assertNull(infrastructureApi.getStorageDevice(datacenter.unwrap(), idStorageDevice));
      }
   }

   protected void deleteMachine() {
      if (machine != null && rack != null) {
         Integer idMachine = machine.getId();
         machine.delete();
         assertNull(infrastructureApi.getMachine(rack.unwrap(), idMachine));
      }
   }

   protected void deleteRack() {
      if (rack != null && datacenter != null) {
         Integer idRack = rack.getId();
         rack.delete();
         assertNull(infrastructureApi.getRack(datacenter.unwrap(), idRack));
      }
   }

   protected void deleteUcsRack() {
      if (ucsRack != null && datacenter != null) {
         Integer idRack = ucsRack.getId();
         ucsRack.delete();
         assertNull(infrastructureApi.getManagedRack(datacenter.unwrap(), idRack));
      }
   }

   protected void deleteDatacenter() {
      if (datacenter != null) {
         // Remove limits first
         enterprise.prohibitDatacenter(datacenter);

         Integer idDatacenter = datacenter.getId();
         datacenter.delete(); // Abiquo API will delete remote services too
         assertNull(infrastructureApi.getDatacenter(idDatacenter));
      }
   }

   protected void deleteEnterprise() {
      if (enterprise != null) {
         Integer idEnterprise = enterprise.getId();
         enterprise.delete();
         assertNull(enterpriseApi.getEnterprise(idEnterprise));
      }
   }

   protected void deleteLicense() {
      license.remove();
   }

   protected static String randomName() {
      return PREFIX + UUID.randomUUID().toString().substring(0, 12);
   }

   // Utility methods

   public static String readLicense() throws IOException {
      URL url = CloudTestEnvironment.class.getResource("/license/expired");

      return Resources.toString(url, Charset.defaultCharset());
   }

   public RemoteService findRemoteService(final RemoteServiceType type) {
      return find(remoteServices, RemoteServicePredicates.type(type));
   }
}
