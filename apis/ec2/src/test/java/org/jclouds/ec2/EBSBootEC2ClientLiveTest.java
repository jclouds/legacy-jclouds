/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.ec2;

import static org.jclouds.ec2.options.CreateSnapshotOptions.Builder.withDescription;
import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.withKernelId;
import static org.jclouds.ec2.options.RunInstancesOptions.Builder.withKeyName;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Image.Architecture;
import org.jclouds.ec2.domain.Image.ImageType;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.domain.Volume.InstanceInitiatedShutdownBehavior;
import org.jclouds.ec2.predicates.InstanceStateRunning;
import org.jclouds.ec2.predicates.InstanceStateStopped;
import org.jclouds.ec2.predicates.InstanceStateTerminated;
import org.jclouds.ec2.predicates.SnapshotCompleted;
import org.jclouds.ec2.predicates.VolumeAttached;
import org.jclouds.ec2.predicates.VolumeAvailable;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payloads;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.inject.Injector;

/**
 * Adapted from the following sources: {@link http://gist.github.com/249915}, {@link http
 * ://www.capsunlock.net/2009/12/create-ebs-boot-ami.html}
 * <p/>
 * 
 * Generally disabled, as it incurs higher fees.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = false, singleThreaded = true, testName = "EBSBootEC2ClientLiveTest")
public class EBSBootEC2ClientLiveTest extends BaseComputeServiceContextLiveTest {
   public EBSBootEC2ClientLiveTest() {
      provider = "ec2";
   }

   // TODO: parameterize
   private static final String IMAGE_ID = "ami-7e28ca17";

   // don't need a lot of space. 2GB should be more than enough for testing
   private static final int VOLUME_SIZE = 2;
   private static final String SCRIPT_END = "----COMPLETE----";
   private static final String INSTANCE_PREFIX = System.getProperty("user.name") + ".ec2ebs";

   private EC2Client client;
   private SshClient.Factory sshFactory;

   private KeyPair keyPair;
   private String securityGroupName;

   private RetryablePredicate<HostAndPort> socketTester;
   private RetryablePredicate<Attachment> attachTester;
   private RetryablePredicate<Volume> volumeTester;
   private RunningInstance instance;
   private RetryablePredicate<RunningInstance> runningTester;
   private RetryablePredicate<RunningInstance> stoppedTester;
   private RetryablePredicate<RunningInstance> terminatedTester;
   private Volume volume;
   private RetryablePredicate<Snapshot> snapshotTester;
   private Snapshot snapshot;
   private Image ebsImage;
   private RunningInstance ebsInstance;
   private Attachment attachment;
   private String mkEbsBoot;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      Injector injector = view.utils().injector();
      client = injector.getInstance(EC2Client.class);
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<HostAndPort>(socketOpen, 120, 1, TimeUnit.SECONDS);

      VolumeAvailable volumeAvailable = injector.getInstance(VolumeAvailable.class);
      volumeTester = new RetryablePredicate<Volume>(volumeAvailable, 60, 1, TimeUnit.SECONDS);

      SnapshotCompleted snapshotCompleted = injector.getInstance(SnapshotCompleted.class);
      snapshotTester = new RetryablePredicate<Snapshot>(snapshotCompleted, 120, 3, TimeUnit.SECONDS);

      VolumeAttached volumeAttached = injector.getInstance(VolumeAttached.class);
      attachTester = new RetryablePredicate<Attachment>(volumeAttached, 60, 1, TimeUnit.SECONDS);

      runningTester = new RetryablePredicate<RunningInstance>(new InstanceStateRunning(client), 180, 5,
            TimeUnit.SECONDS);

      InstanceStateStopped instanceStateStopped = injector.getInstance(InstanceStateStopped.class);
      stoppedTester = new RetryablePredicate<RunningInstance>(instanceStateStopped, 60, 1, TimeUnit.SECONDS);

      InstanceStateTerminated instanceStateTerminated = injector.getInstance(InstanceStateTerminated.class);
      terminatedTester = new RetryablePredicate<RunningInstance>(instanceStateTerminated, 60, 1, TimeUnit.SECONDS);

      injector.injectMembers(socketOpen); // add logger
   }

   @Test(enabled = false)
   void testCreateSecurityGroupIngressCidr() throws InterruptedException, ExecutionException, TimeoutException {
      securityGroupName = INSTANCE_PREFIX + "ingress";

      try {
         client.getSecurityGroupServices().deleteSecurityGroupInRegion(null, securityGroupName);
      } catch (Exception e) {
      }

      client.getSecurityGroupServices().createSecurityGroupInRegion(null, securityGroupName, securityGroupName);
      client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(null, securityGroupName, IpProtocol.TCP,
            80, 80, "0.0.0.0/0");
      client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(null, securityGroupName, IpProtocol.TCP,
            443, 443, "0.0.0.0/0");
      client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(null, securityGroupName, IpProtocol.TCP,
            22, 22, "0.0.0.0/0");
   }

   @Test(enabled = false)
   void testCreateKeyPair() {
      String keyName = INSTANCE_PREFIX + "1";
      try {
         client.getKeyPairServices().deleteKeyPairInRegion(null, keyName);
      } catch (Exception e) {

      }

      keyPair = client.getKeyPairServices().createKeyPairInRegion(null, keyName);
      assertNotNull(keyPair);
      assertNotNull(keyPair.getKeyMaterial());
      assertNotNull(keyPair.getSha1OfPrivateKey());
      assertEquals(keyPair.getKeyName(), keyName);
   }

   @Test(enabled = false, dependsOnMethods = { "testCreateKeyPair", "testCreateSecurityGroupIngressCidr" })
   public void testCreateRunningInstance() throws Exception {
      instance = createInstance(IMAGE_ID);
   }

   private RunningInstance createInstance(String imageId) throws UnknownHostException {
      RunningInstance instance = null;
      while (instance == null) {
         try {
            System.out.printf("%d: running instance%n", System.currentTimeMillis());
            Reservation<? extends RunningInstance> reservation = client.getInstanceServices().runInstancesInRegion(
                  null, null, // allow
                  // ec2
                  // to
                  // chose
                  // an
                  // availability
                  // zone
                  imageId, 1, // minimum instances
                  1, // maximum instances
                  withKeyName(keyPair.getKeyName())// key I created above
                        .asType(InstanceType.M1_SMALL)// smallest instance
                        // size
                        .withSecurityGroup(securityGroupName));// group I
            // created
            // above
            instance = Iterables.getOnlyElement(reservation);
         } catch (HttpResponseException htpe) {
            if (htpe.getResponse().getStatusCode() == 400)
               continue;
            throw htpe;
         }
      }
      assertNotNull(instance.getId());
      assertEquals(instance.getInstanceState(), InstanceState.PENDING);
      instance = blockUntilWeCanSshIntoInstance(instance);
      return instance;
   }

   @Test(enabled = false, dependsOnMethods = "testCreateRunningInstance")
   void testCreateAndAttachVolume() {
      volume = client.getElasticBlockStoreServices().createVolumeInAvailabilityZone(instance.getAvailabilityZone(),
            VOLUME_SIZE);
      System.out.printf("%d: %s awaiting volume to become available%n", System.currentTimeMillis(), volume.getId());

      assert volumeTester.apply(volume);

      Attachment attachment = client.getElasticBlockStoreServices().attachVolumeInRegion(instance.getRegion(),
            volume.getId(), instance.getId(), "/dev/sdh");

      System.out.printf("%d: %s awaiting attachment to complete%n", System.currentTimeMillis(), attachment.getId());

      assert attachTester.apply(attachment);
      System.out.printf("%d: %s attachment complete%n", System.currentTimeMillis(), attachment.getId());
   }

   // TODO use userData to do this, and make initbuilder an example for
   // something else.
   @BeforeTest
   void makeScript() {

      mkEbsBoot = InitScript.builder()
            .name("mkebsboot")
            .home("/tmp")
            .logDir("/tmp/logs")
            .exportVariables(ImmutableMap.of("imageDir", "/mnt/tmp", "ebsDevice", "/dev/sdh", "ebsMountPoint", "/mnt/ebs"))
            .run(Statements
                  .interpret(
                        "echo creating a filesystem and mounting the ebs volume",
                        "{md} {varl}IMAGE_DIR{varr} {varl}EBS_MOUNT_POINT{varr}",
                        "rm -rf {varl}IMAGE_DIR{varr}/*",
                        "yes| mkfs -t ext3 {varl}EBS_DEVICE{varr} 2>&-",
                        "mount {varl}EBS_DEVICE{varr} {varl}EBS_MOUNT_POINT{varr}",
                        "echo making a local working copy of the boot disk",
                        "rsync -ax --exclude /ubuntu/.bash_history --exclude /home/*/.bash_history --exclude /etc/ssh/ssh_host_* --exclude /etc/ssh/moduli --exclude /etc/udev/rules.d/*persistent-net.rules --exclude /var/lib/* --exclude=/mnt/* --exclude=/proc/* --exclude=/tmp/* --exclude=/dev/log / {varl}IMAGE_DIR{varr}",
                        "echo preparing the local working copy",
                        "touch {varl}IMAGE_DIR{varr}/etc/init.d/ec2-init-user-data",
                        "echo copying the local working copy to the ebs mount", "{cd} {varl}IMAGE_DIR{varr}",
                        "tar -cSf - * | tar xf - -C {varl}EBS_MOUNT_POINT{varr}", "echo size of ebs",
                        "du -sk {varl}EBS_MOUNT_POINT{varr}", "echo size of source", "du -sk {varl}IMAGE_DIR{varr}",
                        "rm -rf {varl}IMAGE_DIR{varr}/*", "umount {varl}EBS_MOUNT_POINT{varr}", "echo " + SCRIPT_END)).build()
            .render(OsFamily.UNIX);
   }

   @Test(enabled = false, dependsOnMethods = "testCreateAndAttachVolume")
   void testBundleInstance() {
      SshClient ssh = sshFactory.create(HostAndPort.fromParts(instance.getIpAddress(), 22),
            LoginCredentials.builder().user("ubuntu").privateKey(keyPair.getKeyMaterial()).build());
      try {
         ssh.connect();
      } catch (SshException e) {// try twice in case there is a network timeout
         try {
            Thread.sleep(10 * 1000);
         } catch (InterruptedException e1) {
         }
         ssh.connect();
      }
      try {
         System.out.printf("%d: %s writing ebs script%n", System.currentTimeMillis(), instance.getId());
         String script = "/tmp/mkebsboot-init.sh";
         ssh.put(script, Payloads.newStringPayload(mkEbsBoot));

         System.out.printf("%d: %s launching ebs script%n", System.currentTimeMillis(), instance.getId());
         ssh.exec("chmod 755 " + script);
         ssh.exec(script + " init");
         ExecResponse output = ssh.exec("sudo " + script + " start");
         System.out.println(output);
         output = ssh.exec(script + " status");

         assert !output.getOutput().trim().equals("") : output;

         RetryablePredicate<String> scriptTester = new RetryablePredicate<String>(new ScriptTester(ssh, SCRIPT_END),
               600, 10, TimeUnit.SECONDS);
         scriptTester.apply(script);
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   public static class ScriptTester implements Predicate<String> {
      private final SshClient ssh;
      private final String endMatches;

      public ScriptTester(SshClient ssh, String endMatches) {
         this.ssh = ssh;
         this.endMatches = endMatches;
      }

      @Override
      public boolean apply(String script) {
         System.out.printf("%d: %s testing status%n", System.currentTimeMillis(), script);
         ExecResponse output = ssh.exec(script + " status");
         if (output.getOutput().trim().equals("")) {
            output = ssh.exec(script + " tail");
            String stdout = output.getOutput().trim();
            if (stdout.contains(endMatches)) {
               return true;
            } else {
               output = ssh.exec(script + " tailerr");
               String stderr = output.getOutput().trim();
               throw new RuntimeException(String.format(
                     "script %s ended without token: stdout.log: [%s]; stderr.log: [%s]; ", script, stdout, stderr));
            }
         }
         return false;
      }

   }

   @Test(enabled = false, dependsOnMethods = "testBundleInstance")
   void testAMIFromBundle() {
      volume = Iterables.getOnlyElement(client.getElasticBlockStoreServices().describeVolumesInRegion(
            volume.getRegion(), volume.getId()));
      if (volume.getAttachments().size() > 0) {
         // should be cleanly unmounted, so force is not necessary.
         client.getElasticBlockStoreServices().detachVolumeInRegion(instance.getRegion(), volume.getId(), false);
         System.out.printf("%d: %s awaiting detachment to complete%n", System.currentTimeMillis(), volume.getId());
         assert volumeTester.apply(volume);
      } else {
         attachment = null; // protect test closer so that it doesn't try to
         // detach
      }
      snapshot = client.getElasticBlockStoreServices().createSnapshotInRegion(volume.getRegion(), volume.getId(),
            withDescription("EBS Ubuntu Hardy"));

      System.out.printf("%d: %s awaiting snapshot to complete%n", System.currentTimeMillis(), snapshot.getId());

      assert snapshotTester.apply(snapshot);
      Image image = Iterables.getOnlyElement(client.getAMIServices().describeImagesInRegion(snapshot.getRegion(),
            imageIds(IMAGE_ID)));
      String description = image.getDescription() == null ? "jclouds" : image.getDescription();

      System.out.printf("%d: %s creating ami from snapshot%n", System.currentTimeMillis(), snapshot.getId());

      String amiId = client.getAMIServices().registerUnixImageBackedByEbsInRegion(
            snapshot.getRegion(),
            "ebsboot-" + image.getId(),
            snapshot.getId(),
            withKernelId(image.getKernelId()).withRamdisk(image.getRamdiskId()).withDescription(description)
                  .asArchitecture(Architecture.I386));
      try {
         ebsImage = Iterables.getOnlyElement(client.getAMIServices().describeImagesInRegion(snapshot.getRegion(),
               imageIds(amiId)));
      } catch (AWSResponseException e) {
         // TODO add a retry handler for this HTTP code 400 and the below error
         if (e.getError().getClass().equals("InvalidAMIID.NotFound"))
            ebsImage = Iterables.getOnlyElement(client.getAMIServices().describeImagesInRegion(snapshot.getRegion(),
                  imageIds(amiId)));
         else
            throw e;
      }
      verifyImage();
   }

   @Test(enabled = false, dependsOnMethods = { "testAMIFromBundle" })
   public void testInstanceFromEBS() throws Exception {
      System.out.printf("%d: %s creating instance from ebs-backed ami%n", System.currentTimeMillis(), ebsImage.getId());

      ebsInstance = createInstance(ebsImage.getId());

      client.getInstanceServices().stopInstancesInRegion(ebsInstance.getRegion(), true, ebsInstance.getId());

      System.out.printf("%d: %s awaiting instance to stop %n", System.currentTimeMillis(), ebsInstance.getId());
      stoppedTester.apply(ebsInstance);
      tryToChangeStuff();
      System.out.printf("%d: %s awaiting instance to start %n", System.currentTimeMillis(), ebsInstance.getId());
      client.getInstanceServices().startInstancesInRegion(ebsInstance.getRegion(), ebsInstance.getId());
      ebsInstance = blockUntilWeCanSshIntoInstance(ebsInstance);
   }

   private void verifyImage() {
      assertEquals(ebsImage.getImageType(), ImageType.MACHINE);
      assertEquals(ebsImage.getRootDeviceType(), RootDeviceType.EBS);
      assertEquals(ebsImage.getRootDeviceName(), "/dev/sda1");
      assertEquals(ebsImage.getEbsBlockDevices().entrySet(),
            ImmutableMap.of("/dev/sda1", new Image.EbsBlockDevice(snapshot.getId(), VOLUME_SIZE, true)).entrySet());
   }

   private void tryToChangeStuff() {
      setUserDataForInstanceInRegion();
      setRamdiskForInstanceInRegion();
      setKernelForInstanceInRegion();
      setInstanceTypeForInstanceInRegion();
      setInstanceInitiatedShutdownBehaviorForInstanceInRegion();
      setBlockDeviceMappingForInstanceInRegion();
   }

   private void setUserDataForInstanceInRegion() {
      client.getInstanceServices().setUserDataForInstanceInRegion(null, ebsInstance.getId(), "test".getBytes());
      assertEquals("test", client.getInstanceServices().getUserDataForInstanceInRegion(null, ebsInstance.getId()));
   }

   private void setRamdiskForInstanceInRegion() {
      String ramdisk = client.getInstanceServices().getRamdiskForInstanceInRegion(null, ebsInstance.getId());
      client.getInstanceServices().setRamdiskForInstanceInRegion(null, ebsInstance.getId(), ramdisk);
      assertEquals(ramdisk, client.getInstanceServices().getRamdiskForInstanceInRegion(null, ebsInstance.getId()));
   }

   private void setKernelForInstanceInRegion() {
      String oldKernel = client.getInstanceServices().getKernelForInstanceInRegion(null, ebsInstance.getId());
      client.getInstanceServices().setKernelForInstanceInRegion(null, ebsInstance.getId(), oldKernel);
      assertEquals(oldKernel, client.getInstanceServices().getKernelForInstanceInRegion(null, ebsInstance.getId()));
   }

   private void setInstanceTypeForInstanceInRegion() {
      client.getInstanceServices()
            .setInstanceTypeForInstanceInRegion(null, ebsInstance.getId(), InstanceType.C1_MEDIUM);
      assertEquals(InstanceType.C1_MEDIUM,
            client.getInstanceServices().getInstanceTypeForInstanceInRegion(null, ebsInstance.getId()));
      client.getInstanceServices().setInstanceTypeForInstanceInRegion(null, ebsInstance.getId(), InstanceType.M1_SMALL);
      assertEquals(InstanceType.M1_SMALL,
            client.getInstanceServices().getInstanceTypeForInstanceInRegion(null, ebsInstance.getId()));
   }

   private void setBlockDeviceMappingForInstanceInRegion() {
      String volumeId = ebsInstance.getEbsBlockDevices().get("/dev/sda1").getVolumeId();

      Map<String, BlockDevice> mapping = Maps.newLinkedHashMap();
      mapping.put("/dev/sda1", new BlockDevice(volumeId, false));
      try {
         client.getInstanceServices().setBlockDeviceMappingForInstanceInRegion(null, ebsInstance.getId(), mapping);

         Map<String, BlockDevice> devices = client.getInstanceServices().getBlockDeviceMappingForInstanceInRegion(null,
               ebsInstance.getId());
         assertEquals(devices.size(), 1);
         String deviceName = Iterables.getOnlyElement(devices.keySet());
         BlockDevice device = Iterables.getOnlyElement(devices.values());

         assertEquals(device.getVolumeId(), volumeId);
         assertEquals(deviceName, "/dev/sda1");
         assertEquals(device.isDeleteOnTermination(), false);

         System.out.println("OK: setBlockDeviceMappingForInstanceInRegion");
      } catch (Exception e) {
         System.err.println("setBlockDeviceMappingForInstanceInRegion");

         e.printStackTrace();
      }
   }

   private void setInstanceInitiatedShutdownBehaviorForInstanceInRegion() {
      try {

         client.getInstanceServices().setInstanceInitiatedShutdownBehaviorForInstanceInRegion(null,
               ebsInstance.getId(), InstanceInitiatedShutdownBehavior.STOP);

         assertEquals(InstanceInitiatedShutdownBehavior.STOP, client.getInstanceServices()
               .getInstanceInitiatedShutdownBehaviorForInstanceInRegion(null, ebsInstance.getId()));
         client.getInstanceServices().setInstanceInitiatedShutdownBehaviorForInstanceInRegion(null,
               ebsInstance.getId(), InstanceInitiatedShutdownBehavior.TERMINATE);

         assertEquals(InstanceInitiatedShutdownBehavior.TERMINATE, client.getInstanceServices()
               .getInstanceInitiatedShutdownBehaviorForInstanceInRegion(null, ebsInstance.getId()));
         System.out.println("OK: setInstanceInitiatedShutdownBehaviorForInstanceInRegion");
      } catch (Exception e) {
         System.err.println("setInstanceInitiatedShutdownBehaviorForInstanceInRegion");
         e.printStackTrace();
      }
   }

   /**
    * this tests "personality" as the file looked up was sent during instance creation
    * 
    * @throws UnknownHostException
    */
   private void sshPing(RunningInstance newDetails) throws UnknownHostException {
      try {
         doCheckKey(newDetails);
      } catch (SshException e) {// try twice in case there is a network timeout
         try {
            Thread.sleep(10 * 1000);
         } catch (InterruptedException e1) {
         }
         doCheckKey(newDetails);
      }
   }

   private void doCheckKey(RunningInstance newDetails) throws UnknownHostException {
      doCheckKey(newDetails.getIpAddress());
   }

   private void doCheckKey(String address) {
      SshClient ssh = sshFactory.create(HostAndPort.fromParts(address, 22),
            LoginCredentials.builder().user("ubuntu").privateKey(keyPair.getKeyMaterial()).build());
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   private RunningInstance blockUntilWeCanSshIntoInstance(RunningInstance instance) throws UnknownHostException {
      System.out.printf("%d: %s awaiting instance to run %n", System.currentTimeMillis(), instance.getId());
      assert runningTester.apply(instance);

      // search my identity for the instance I just created
      Set<? extends Reservation<? extends RunningInstance>> reservations = client.getInstanceServices()
            .describeInstancesInRegion(instance.getRegion(), instance.getId()); // last
      // parameter
      // (ids)
      // narrows
      // the
      // search

      instance = Iterables.getOnlyElement(Iterables.getOnlyElement(reservations));

      System.out.printf("%d: %s awaiting ssh service to start%n", System.currentTimeMillis(), instance.getIpAddress());
      assert socketTester.apply(HostAndPort.fromParts(instance.getIpAddress(), 22));
      System.out.printf("%d: %s ssh service started%n", System.currentTimeMillis(), instance.getDnsName());
      sshPing(instance);
      System.out.printf("%d: %s ssh connection made%n", System.currentTimeMillis(), instance.getId());
      return instance;
   }

   @AfterTest
   void cleanup() {
      if (ebsInstance != null) {
         try {
            client.getInstanceServices().terminateInstancesInRegion(ebsInstance.getRegion(), ebsInstance.getId());
            terminatedTester.apply(ebsInstance);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      if (ebsImage != null) {
         try {
            client.getAMIServices().deregisterImageInRegion(ebsImage.getRegion(), ebsImage.getId());
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      if (snapshot != null) {
         try {
            client.getElasticBlockStoreServices().deleteSnapshotInRegion(snapshot.getRegion(), snapshot.getId());
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      if (attachment != null) {
         try {
            client.getElasticBlockStoreServices().detachVolumeInRegion(volume.getRegion(), volume.getId(), true);
            assert volumeTester.apply(volume);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      if (instance != null) {
         try {
            client.getInstanceServices().terminateInstancesInRegion(instance.getRegion(), instance.getId());
            terminatedTester.apply(instance);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      if (volume != null) {
         try {
            client.getElasticBlockStoreServices().deleteVolumeInRegion(volume.getRegion(), volume.getId());
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      if (keyPair != null) {
         try {
            client.getKeyPairServices().deleteKeyPairInRegion(keyPair.getRegion(), keyPair.getKeyName());
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      if (securityGroupName != null) {
         try {
            client.getSecurityGroupServices().deleteSecurityGroupInRegion(null, securityGroupName);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

}
