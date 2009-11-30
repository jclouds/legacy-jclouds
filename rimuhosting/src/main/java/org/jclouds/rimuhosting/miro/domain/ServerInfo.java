/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingTimestamp;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;

/**
 * @author Ivan Meredith
 */
public class ServerInfo {
   /**
    * Whether the server pinged from the host server.
    */
   @SerializedName("pings_ok")
   private boolean instancePings;
   /**
    * The current kernel label.&nbsp; e.g. default is the recommended
    * one.
    */
   @SerializedName("current_kernel")
   private String kernel;
   /**
    * Some current_kernel labels are symlinks to different
    * kernel.&nbsp; e.g. 'default' is a symlink to kernel that we think
    * will work best for the host server and its VPSs.&nbsp; Over time
    * we may change it (e.g. so it points to a newer server).&nbsp; In
    * which case your server would get that new kernel the next time it
    * is restarted.&nbsp; Note that to get the new kernel you need to
    * restart the server from _outside_ the VPS (i.e. not with a reboot
    * command).
    */
   @SerializedName("current_kernel_canonical")
   private String kernelCanonical;
   /**
    * The last backup message stored for the VPS.
    */
   @SerializedName("last_backup_message")
   private String backupMessage;
   /**
    * Whether the console-over-ssh login feature is enabled.
    */
   @SerializedName("is_console_login_enabled")
   private boolean isConsoleLoginEnabled;
   /**
    * The console-over-sshes authorized keys (if they are set).&nbsp;
    * Else the console access would be controlled by a password.
    */
   @SerializedName("console_public_authorized_keys")
   private String consolePublicKeys;
   /**
    * true when the host server is running a backup of the VPS.&nbsp;
    * On new hosts we use LVM file systems so backups can be performed
    * by only pausing VPSs for a fraction of a second.
    */
   @SerializedName("is_backup_running")
   private boolean isBackupRunning;
   /**
    * true on almost all servers.&nbsp; false when the backups are
    * enabled.&nbsp; e.g. if a customer had requested we disable them.
    */
   @SerializedName("is_backups_enabled")
   private boolean backupsEnabled;
   //public boolean is_one_week_backup;
   /**
    * The time we next expect a backup to run.&nbsp; The actual time
    * may be a bit later (e.g. if other VPSs are due to run at this
    * time slot).&nbsp; We run VPS backups sequentially (one after the
    * other).&nbsp; So if two VPSs were scheduled for a backup at the
    * same time one may run a few minutes after the other.
    */
   @SerializedName("next_backup_time")
   private RimuHostingTimestamp nextBackup;
   /**
    * How long Xen reports the VPS has been up and running for.&nbsp;
    * In seconds.&nbsp; Divide by your time unit of choice :)
    */
   @SerializedName("vps_uptime_s")
   private long instanceUptime;
   /**
    * The number of CPU seconds consumed by a VPS.&nbsp; Note that if a
    * VPS is using more than one core or CPU then this number can be
    * higher than the uptime number.
    */
   @SerializedName("vps_cpu_time_s")
   private long instanceCpuTime;
   /**
    * Whether the VPS is, for example, running or not.&nbsp; Typically
    * VPSs will all be running.&nbsp; A VPS may be in a non-running
    * state, for example, for a short time after its host server is
    * restarted.&nbsp; Since we start VPSs sequentially (one after the
    * other) to keep server load to a manageable level.&nbsp;
    */
   @SerializedName("running_state")
   private RunningState state;
   /**
    * Whether the VPS is marked to not run.&nbsp; e.g. some host server
    * operations (like a disk resize) set the suspended state to
    * prevent the VPS running or other actions being performed while
    * that operation is being performed.&nbsp; Sometimes we may set
    * this if the server is 'administratively down', e.g. as a
    * consequence of the server breaching our terms of service.
    */
   @SerializedName("is_suspended")
   private boolean isSuspended;

   public boolean isInstancePings() {
      return instancePings;
   }

   public void setInstancePings(boolean instancePings) {
      this.instancePings = instancePings;
   }

   public String getKernel() {
      return kernel;
   }

   public void setKernel(String kernel) {
      this.kernel = kernel;
   }

   public String getKernelCanonical() {
      return kernelCanonical;
   }

   public void setKernelCanonical(String kernelCanonical) {
      this.kernelCanonical = kernelCanonical;
   }

   public String getBackupMessage() {
      return backupMessage;
   }

   public void setBackupMessage(String backupMessage) {
      this.backupMessage = backupMessage;
   }

   public boolean isConsoleLoginEnabled() {
      return isConsoleLoginEnabled;
   }

   public void setConsoleLoginEnabled(boolean consoleLoginEnabled) {
      isConsoleLoginEnabled = consoleLoginEnabled;
   }

   public String getConsolePublicKeys() {
      return consolePublicKeys;
   }

   public void setConsolePublicKeys(String consolePublicKeys) {
      this.consolePublicKeys = consolePublicKeys;
   }

   public boolean isBackupRunning() {
      return isBackupRunning;
   }

   public void setBackupRunning(boolean backupRunning) {
      isBackupRunning = backupRunning;
   }

   public boolean isBackupsEnabled() {
      return backupsEnabled;
   }

   public void setBackupsEnabled(boolean backupsEnabled) {
      this.backupsEnabled = backupsEnabled;
   }

   public RimuHostingTimestamp getNextBackup() {
      return nextBackup;
   }

   public void setNextBackup(RimuHostingTimestamp nextBackup) {
      this.nextBackup = nextBackup;
   }

   public long getInstanceUptime() {
      return instanceUptime;
   }

   public void setInstanceUptime(long instanceUptime) {
      this.instanceUptime = instanceUptime;
   }

   public long getInstanceCpuTime() {
      return instanceCpuTime;
   }

   public void setInstanceCpuTime(long instanceCpuTime) {
      this.instanceCpuTime = instanceCpuTime;
   }

   public RunningState getState() {
      return state;
   }

   public void setState(RunningState state) {
      this.state = state;
   }

   public boolean isSuspended() {
      return isSuspended;
   }

   public void setSuspended(boolean suspended) {
      isSuspended = suspended;
   }
}
