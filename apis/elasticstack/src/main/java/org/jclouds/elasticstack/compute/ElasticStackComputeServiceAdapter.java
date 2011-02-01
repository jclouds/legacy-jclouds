package org.jclouds.elasticstack.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.concurrent.FutureIterables.transformParallel;
import static org.jclouds.elasticstack.util.Servers.small;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.elasticstack.ElasticStackAsyncClient;
import org.jclouds.elasticstack.ElasticStackClient;
import org.jclouds.elasticstack.domain.Device;
import org.jclouds.elasticstack.domain.Drive;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.elasticstack.domain.ImageConversionType;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.ServerInfo;
import org.jclouds.elasticstack.domain.ServerStatus;
import org.jclouds.elasticstack.domain.WellKnownImage;
import org.jclouds.elasticstack.reference.ElasticStackConstants;
import org.jclouds.location.suppliers.JustProvider;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * defines the connection between the {@link ElasticStackClient} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class ElasticStackComputeServiceAdapter implements
         ComputeServiceAdapter<ServerInfo, Hardware, DriveInfo, Location> {
   private final ElasticStackClient client;
   private final ElasticStackAsyncClient aclient;
   private final Predicate<DriveInfo> driveNotClaimed;
   private final Map<String, WellKnownImage> preinstalledImages;
   private final Map<String, DriveInfo> cache;
   private final JustProvider locationSupplier;
   private final String defaultVncPassword;
   private final ExecutorService executor;

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   public ElasticStackComputeServiceAdapter(ElasticStackClient client, ElasticStackAsyncClient aclient,
            Predicate<DriveInfo> driveNotClaimed,  JustProvider locationSupplier, 
            Map<String, WellKnownImage> preinstalledImages, Map<String, DriveInfo> cache,
            @Named(ElasticStackConstants.PROPERTY_VNC_PASSWORD) String defaultVncPassword,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.client = checkNotNull(client, "client");
      this.aclient = checkNotNull(aclient, "aclient");
      this.driveNotClaimed = checkNotNull(driveNotClaimed, "driveNotClaimed");
      this.locationSupplier = checkNotNull(locationSupplier, "locationSupplier");
      this.preinstalledImages = checkNotNull(preinstalledImages, "preinstalledImages");
      this.cache = checkNotNull(cache, "cache");
      this.defaultVncPassword = checkNotNull(defaultVncPassword, "defaultVncPassword");
      this.executor = checkNotNull(executor, "executor");
   }

   @Override
   public ServerInfo createNodeWithGroupEncodedIntoNameThenStoreCredentials(String tag, String name, Template template,
            Map<String, Credentials> credentialStore) {
      long bootSize = (long) (template.getHardware().getVolumes().get(0).getSize() * 1024 * 1024 * 1024l);
      logger.debug(">> creating boot drive bytes(%d)", bootSize);
      DriveInfo drive = client.createDrive(new Drive.Builder().name(template.getImage().getName()).size(bootSize)
               .build());
      logger.debug("<< drive(%s)", drive.getUuid());

      logger.debug(">> imaging boot drive source(%s)", template.getImage().getId());
      client.imageDrive(template.getImage().getId(), drive.getUuid(), ImageConversionType.GUNZIP);
      boolean success = driveNotClaimed.apply(drive);
      logger.debug("<< imaged (%s)", success);
      if (!success) {
         client.destroyDrive(drive.getUuid());
         throw new IllegalStateException("could not image drive in time!");
      }
      Server toCreate = small(name, drive.getUuid(), defaultVncPassword).mem(template.getHardware().getRam()).cpu(
               (int) (template.getHardware().getProcessors().get(0).getSpeed())).build();

      ServerInfo from = client.createAndStartServer(toCreate);
      // store the credentials so that later functions can use them
      credentialStore.put(from.getUuid() + "", new Credentials(template.getImage().getDefaultCredentials().identity,
               from.getVnc().getPassword()));
      return from;
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      Builder<Hardware> hardware = ImmutableSet.<Hardware> builder();
      for (double cpu : new double[] { 1000, 5000, 10000, 20000 })
         for (int ram : new int[] { 512, 1024, 2048, 4096, 8192 }) {
            final float size = (float) cpu / 1000;
            String id = String.format("cpu=%f,ram=%s,disk=%f", cpu, ram, size);
            hardware.add(new HardwareBuilder().supportsImage(new Predicate<Image>() {

               @Override
               public boolean apply(Image input) {
                  String toParse = input.getUserMetadata().get("size");
                  return (toParse != null && new Float(toParse) <= size);
               }

               @Override
               public String toString() {
                  return "sizeLessThanOrEqual(" + size + ")";
               }

            }).ids(id).ram(ram).processors(ImmutableList.of(new Processor(1, cpu))).volumes(
                     ImmutableList.<Volume> of(new VolumeImpl(size, true, true))).build());
         }
      return hardware.build();
   }

   /**
    * look up the current standard images and do not error out, if they are not found.
    */
   @Override
   public Iterable<DriveInfo> listImages() {
      Iterable<DriveInfo> drives = transformParallel(preinstalledImages.keySet(),
               new Function<String, Future<DriveInfo>>() {

                  @Override
                  public Future<DriveInfo> apply(String input) {
                     return aclient.getDriveInfo(input);
                  }

               }, executor, null, logger, "drives");
      Iterable<DriveInfo> returnVal = filter(drives, notNull());
      for (DriveInfo drive : returnVal)
         cache.put(drive.getUuid(), drive);
      return returnVal;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Iterable<ServerInfo> listNodes() {
      return (Iterable<ServerInfo>) client.listServerInfo();
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public Iterable<Location> listLocations() {
      return (Iterable<Location>) locationSupplier.get();
   }

   @Override
   public ServerInfo getNode(String id) {
      return client.getServerInfo(id);
   }

   @Override
   public void destroyNode(String id) {
      ServerInfo server = getNode(id);
      if (server != null) {
         if (server.getStatus() != ServerStatus.STOPPED)
            client.stopServer(id);
         client.destroyServer(id);
         for (Device dev : server.getDevices().values())
            client.destroyDrive(dev.getDriveUuid());
      }
   }

   @Override
   public void rebootNode(String id) {
      client.resetServer(id);
   }

   @Override
   public void resumeNode(String id) {
      client.startServer(id);

   }

   @Override
   public void suspendNode(String id) {
      client.stopServer(id);
   }
}