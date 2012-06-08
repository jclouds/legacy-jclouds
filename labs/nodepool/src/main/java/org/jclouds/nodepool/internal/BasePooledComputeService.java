package org.jclouds.nodepool.internal;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.nodepool.PooledComputeService;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * A base class for {@link PooledComputeService} for all methods that are simply delegated to the
 * backing {@link ComputeService}
 * 
 * @author David Alves
 * 
 */
public abstract class BasePooledComputeService implements PooledComputeService {

   protected final ComputeService backingComputeService;
   protected final String poolGroupName;

   public BasePooledComputeService(ComputeService backingComputeService, String poolGroupNamePrefix) {
      this.backingComputeService = backingComputeService;
      this.poolGroupName = poolGroupNamePrefix;
   }

   @Override
   public ComputeServiceContext getContext() {
      // not sure this is enough, should we have our own?
      return backingComputeService.getContext();
   }

   // we ignore user provided templates and options
   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count, Template template)
            throws RunNodesException {
      return createNodesInGroup(group, count);
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count, TemplateOptions templateOptions)
            throws RunNodesException {
      return createNodesInGroup(group, count);
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
            String runScript) throws RunScriptOnNodesException {
      return null;
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
            Statement runScript) throws RunScriptOnNodesException {
      return null;
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
            String runScript, RunScriptOptions options) throws RunScriptOnNodesException {
      return null;
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
            Statement runScript, RunScriptOptions options) throws RunScriptOnNodesException {
      return null;
   }

   // set of direct delegation methods

   @Override
   public TemplateBuilder templateBuilder() {
      return backingComputeService.templateBuilder();
   }

   @Override
   public TemplateOptions templateOptions() {
      return backingComputeService.templateOptions();
   }

   @Override
   public Set<? extends Hardware> listHardwareProfiles() {
      return backingComputeService.listHardwareProfiles();
   }

   @Override
   public Set<? extends Image> listImages() {
      return backingComputeService.listImages();

   }

   @Override
   public Image getImage(String id) {
      return backingComputeService.getImage(id);
   }

   @Override
   public Set<? extends Location> listAssignableLocations() {
      return backingComputeService.listAssignableLocations();
   }

   @Override
   public void suspendNode(String id) {
      backingComputeService.suspendNode(id);
   }

   @Override
   public void resumeNode(String id) {
      backingComputeService.resumeNode(id);
   }

   @Override
   public void rebootNode(String id) {
      backingComputeService.rebootNode(id);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, Statement runScript, RunScriptOptions options) {
      return backingComputeService.runScriptOnNode(id, runScript, options);
   }

   @Override
   public ListenableFuture<ExecResponse> submitScriptOnNode(String id, Statement runScript, RunScriptOptions options) {
      return backingComputeService.submitScriptOnNode(id, runScript, options);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, Statement runScript) {
      return backingComputeService.runScriptOnNode(id, runScript);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, String runScript, RunScriptOptions options) {
      return backingComputeService.runScriptOnNode(id, runScript, options);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, String runScript) {
      return backingComputeService.runScriptOnNode(id, runScript);
   }

   @Override
   public Optional<ImageExtension> getImageExtension() {
      return backingComputeService.getImageExtension();
   }

}
