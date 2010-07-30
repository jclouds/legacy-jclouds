package org.jclouds.vcloud.bluelock.compute.config.providers;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.compute.functions.FindLocationForResource;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.VDC;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class ParseVAppTemplatesInVDCToSizeProvider implements Provider<Set<? extends Size>> {
   // ex Ubuntu904Serverx64 1CPUx16GBx20GB
   public static final Pattern GBRAM_PATTERN = Pattern.compile("[^ ] ([0-9]+)CPUx([0-9]+)GBx([0-9]+)GB");

   // ex Windows2008stdx64 1CPUx512MBx30GB
   public static final Pattern MBRAM_PATTERN = Pattern.compile("[^ ] ([0-9]+)CPUx([0-9]+)MBx([0-9]+)GB");

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;

   private final VCloudClient client;

   private final FindLocationForResource findLocationForResourceInVDC;

   // TODO fix to work with multiple orgs. this currently assumes only one per user which is ok for
   // now
   @Inject
   ParseVAppTemplatesInVDCToSizeProvider(VCloudClient client, FindLocationForResource findLocationForResourceInVDC) {
      this.client = client;
      this.findLocationForResourceInVDC = findLocationForResourceInVDC;
   }

   @Override
   public Set<? extends Size> get() {
      final Set<Size> sizes = Sets.newHashSet();
      logger.debug(">> providing vAppTemplates");
      for (final NamedResource vDC : client.getDefaultOrganization().getVDCs().values()) {
         VDC vdc = client.getVDC(vDC.getId());
         addSizesFromVAppTemplatesInVDC(vdc, sizes);
      }
      return sizes;
   }

   @VisibleForTesting
   void addSizesFromVAppTemplatesInVDC(VDC vdc, Set<Size> sizes) {
      for (NamedResource resource : vdc.getResourceEntities().values()) {
         if (resource.getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
            Location location = findLocationForResourceInVDC.apply(vdc);
            try {
               Matcher matcher = getMatcherAndFind(resource.getName());
               double cores = Double.parseDouble(matcher.group(1));
               int ram = Integer.parseInt(matcher.group(2));
               if (matcher.pattern().equals(GBRAM_PATTERN))
                  ram *= 1024;
               int disk = Integer.parseInt(matcher.group(3));
               String name = resource.getName().split(" ")[1];
               String id = vdc.getId() + "/" + resource.getId();
               sizes.add(new SizeImpl(resource.getId(), name, id, location, null, ImmutableMap.<String, String> of(),
                        cores, ram, disk, ImagePredicates.idEquals(id)));
            } catch (NoSuchElementException e) {
               logger.debug("<< didn't match at all(%s)", resource);
            }
         }
      }
   }

   /**
    * 
    * @throws NoSuchElementException
    *            if no configured matcher matches the name.
    */
   private Matcher getMatcherAndFind(String name) {
      for (Pattern pattern : new Pattern[] { GBRAM_PATTERN, MBRAM_PATTERN }) {
         Matcher matcher = pattern.matcher(name);
         if (matcher.find())
            return matcher;
      }
      throw new NoSuchElementException(name);
   }
}