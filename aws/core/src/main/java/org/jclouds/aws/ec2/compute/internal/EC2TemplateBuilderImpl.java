package org.jclouds.aws.ec2.compute.internal;

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

/**
 * 
 * @author Adrian Cole
 */
public class EC2TemplateBuilderImpl extends TemplateBuilderImpl {

   private final ConcurrentMap<RegionAndName, Image> imageMap;

   @Inject
   protected EC2TemplateBuilderImpl(Set<? extends Location> locations, Set<? extends Image> images,
            Set<? extends Size> sizes, Location defaultLocation,
            Provider<TemplateOptions> optionsProvider,
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
   protected Image resolveImage() {
      try {
         return super.resolveImage();
      } catch (NoSuchElementException e) {
         RegionAndName key = new RegionAndName(this.locationId, this.imageId);
         try {
            return imageMap.get(key);
         } catch (NullPointerException nex) {
            throw new NoSuchElementException(String.format("image %s/%s not found",
                     key.getRegion(), key.getName()));
         }
      }
   }

}
