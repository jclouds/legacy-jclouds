package org.jclouds.aws.ec2.compute.internal;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.TemplateBuilderImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;

import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 */
public class EC2TemplateBuilderImpl extends TemplateBuilderImpl {

   private final ConcurrentMap<RegionAndName, Image> imageMap;

   @Inject
   protected EC2TemplateBuilderImpl(Provider<Set<? extends Location>> locations,
            Provider<Set<? extends Image>> images, Provider<Set<? extends Size>> sizes,
            Location defaultLocation, Provider<TemplateOptions> optionsProvider,
            @Named("DEFAULT") Provider<TemplateBuilder> defaultTemplateProvider,
            ConcurrentMap<RegionAndName, Image> imageMap) {
      super(locations, images, sizes, defaultLocation, optionsProvider, defaultTemplateProvider);
      this.imageMap = imageMap;
   }

   /**
    * @throws NoSuchElementException
    *            if the image is not found
    */
   @Override
   protected List<? extends Image> resolveImages() {
      try {
         return super.resolveImages();
      } catch (NoSuchElementException e) {
         if (locationId != null && imageId != null) {
            RegionAndName key = new RegionAndName(this.locationId, this.imageId);
            try {
               return ImmutableList.of(imageMap.get(key));
            } catch (NullPointerException nex) {
               throw new NoSuchElementException(String.format("image %s/%s not found", key
                        .getRegion(), key.getName()));
            }
         }
         throw e;
      }
   }

}
