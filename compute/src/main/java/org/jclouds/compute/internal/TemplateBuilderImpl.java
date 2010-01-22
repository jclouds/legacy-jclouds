package org.jclouds.compute.internal;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.TemplateImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.ResourceLocation;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

/**
 * 
 * @author Adrian Cole
 */
@SuppressWarnings("unchecked")
public class TemplateBuilderImpl implements TemplateBuilder {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Set<? extends Image> images;
   private final Set<? extends Size> sizes;
   private String location;
   private OsFamily os;
   private Architecture arch;
   private String imageId;
   private String sizeId;

   private String osDescription;
   private String imageVersion;
   private String imageDescription;

   private int minCores;
   private int minRam;

   private boolean biggest;
   private boolean fastest;

   @Inject
   public TemplateBuilderImpl(@ResourceLocation String location, Set<? extends Image> images,
            Set<? extends Size> sizes) {
      this.location = location;
      this.images = images;
      this.sizes = sizes;
   }

   private final Predicate<Image> imageIdPredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (imageId != null) {
            returnVal = imageId.equals(input.getId());
            // match our input params so that the later predicates pass.
            if (returnVal) {
               fromImage(input);
            }
         }
         return returnVal;
      }
   };

   private final Predicate<Image> locationPredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (location != null)
            returnVal = location.equals(input.getLocation());
         return returnVal;
      }
   };

   private final Predicate<Image> osPredicate = new Predicate<Image>() {

      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (os != null)
            returnVal = os.equals(input.getOsFamily());
         return returnVal;
      }

   };
   private final Predicate<Image> imageArchPredicate = new Predicate<Image>() {

      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (arch != null)
            returnVal = arch.equals(input.getArchitecture());
         return returnVal;
      }

   };
   private final Predicate<Image> osDescriptionPredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (osDescription != null) {
            if (input.getOsDescription() == null)
               returnVal = false;
            else
               returnVal = input.getOsDescription().matches(osDescription);
         }
         return returnVal;
      }
   };
   private final Predicate<Image> imageVersionPredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (imageVersion != null) {
            if (input.getVersion() == null)
               returnVal = false;
            else
               returnVal = input.getVersion().matches(imageVersion);
         }
         return returnVal;
      }
   };
   private final Predicate<Image> imageDescriptionPredicate = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         boolean returnVal = true;
         if (imageDescription != null) {
            if (input.getDescription() == null)
               returnVal = false;
            else
               returnVal = input.getDescription().matches(imageDescription);
         }
         return returnVal;
      }
   };

   private final Predicate<Size> sizeIdPredicate = new Predicate<Size>() {
      @Override
      public boolean apply(Size input) {
         boolean returnVal = true;
         if (sizeId != null) {
            returnVal = sizeId.equals(input.getId());
            // match our input params so that the later predicates pass.
            if (returnVal) {
               fromSize(input);
            }
         }
         return returnVal;
      }
   };

   private final Predicate<Image> imagePredicate = Predicates.and(imageIdPredicate,
            locationPredicate, osPredicate, imageArchPredicate, osDescriptionPredicate,
            imageVersionPredicate, imageDescriptionPredicate);

   private final Predicate<Size> sizeArchPredicate = new Predicate<Size>() {
      @Override
      public boolean apply(Size input) {
         boolean returnVal = false;
         if (arch != null)
            returnVal = input.supportsArchitecture(arch);
         return returnVal;
      }
   };

   private final Predicate<Size> sizeCoresPredicate = new Predicate<Size>() {
      @Override
      public boolean apply(Size input) {
         return input.getCores() >= TemplateBuilderImpl.this.minCores;
      }
   };

   private final Predicate<Size> sizeRamPredicate = new Predicate<Size>() {
      @Override
      public boolean apply(Size input) {
         return input.getRam() >= TemplateBuilderImpl.this.minRam;
      }
   };
   private final Predicate<Size> sizePredicate = Predicates.and(sizeIdPredicate, sizeArchPredicate,
            sizeCoresPredicate, sizeRamPredicate);

   static final Ordering<Size> DEFAULT_SIZE_ORDERING = new Ordering<Size>() {
      public int compare(Size left, Size right) {
         return ComparisonChain.start().compare(left.getCores(), right.getCores()).compare(
                  left.getRam(), right.getRam()).compare(left.getDisk(), right.getDisk()).result();
      }
   };
   static final Ordering<Size> BY_CORES_ORDERING = new Ordering<Size>() {
      public int compare(Size left, Size right) {
         return Ints.compare(left.getCores(), right.getCores());
      }
   };
   static final Ordering<Image> DEFAULT_IMAGE_ORDERING = new Ordering<Image>() {
      public int compare(Image left, Image right) {
         return ComparisonChain.start().compare(left.getOsDescription(),
                  right.getOsDescription()).compare(left.getVersion(), right.getVersion())
                  .result();
      }
   };

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fromTemplate(Template template) {
      fromSize(template.getSize());
      fromImage(template.getImage());
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fromSize(Size size) {
      this.minCores = size.getCores();
      this.minRam = size.getRam();
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fromImage(Image image) {
      if (image.getLocation() != null)
         this.location = image.getLocation();
      if (image.getOsFamily() != null)
         this.os = image.getOsFamily();
      if (image.getOsDescription() != null)
         this.osDescription = image.getOsDescription();
      if (image.getVersion() != null)
         this.imageVersion = image.getVersion();
      if (image.getArchitecture() != null)
         this.arch = image.getArchitecture();
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder smallest() {
      this.biggest = false;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder biggest() {
      this.biggest = true;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder fastest() {
      this.fastest = true;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder location(String location) {
      this.location = location;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder osFamily(OsFamily os) {
      this.os = os;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder architecture(Architecture architecture) {
      this.arch = architecture;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template build() {
      logger.debug(">> searching params(%s)", this);
      Image image;
      try {
         image = DEFAULT_IMAGE_ORDERING.max(Iterables.filter(images, imagePredicate));
      } catch (NoSuchElementException exception) {
         throw new NoSuchElementException("image didn't match: " + toString() + "\n" + images);
      }
      logger.debug("<<   matched image(%s)", image);
      // ensure we have an architecture matching
      this.arch = image.getArchitecture();

      Ordering<Size> sizeOrdering = DEFAULT_SIZE_ORDERING;
      if (!biggest)
         sizeOrdering = sizeOrdering.reverse();
      if (fastest)
         sizeOrdering = Ordering.compound(ImmutableList.of(BY_CORES_ORDERING, sizeOrdering));
      Size size;
      try {
         size = sizeOrdering.max(Iterables.filter(sizes, sizePredicate));
      } catch (NoSuchElementException exception) {
         throw new NoSuchElementException("size didn't match: " + toString() + "\n" + sizes);
      }
      logger.debug("<<   matched size(%s)", size);
      return new TemplateImpl(image, size) {
      };
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageId(String imageId) {
      this.imageId = imageId;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageDescriptionMatches(String descriptionRegex) {
      this.imageDescription = descriptionRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder imageVersionMatches(String imageVersionRegex) {
      this.imageVersion = imageVersionRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder minCores(int minCores) {
      this.minCores = minCores;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder minRam(int megabytes) {
      this.minRam = megabytes;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder osDescriptionMatches(String osDescriptionRegex) {
      this.osDescription = osDescriptionRegex;
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TemplateBuilder sizeId(String sizeId) {
      this.sizeId = sizeId;
      return this;
   }

   @Override
   public String toString() {
      return "[arch=" + arch + ", biggest=" + biggest + ", fastest=" + fastest
               + ", imageDescription=" + imageDescription + ", imageId=" + imageId
               + ", imageVersion=" + imageVersion + ", location=" + location + ", minCores="
               + minCores + ", minRam=" + minRam + ", os=" + os + ", osDescription=" + osDescription
               + ", sizeId=" + sizeId + "]";
   }

}
