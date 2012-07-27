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
package org.jclouds.demo.paas.service.scheduler.quartz.plugins;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.jclouds.logging.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerKey;
import org.quartz.jobs.FileScanJob;
import org.quartz.jobs.FileScanListener;
import org.quartz.plugins.xml.XMLSchedulingDataProcessorPlugin;
import org.quartz.simpl.CascadingClassLoadHelper;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;
import org.quartz.xml.XMLSchedulingDataProcessor;

/**
 * A copy of {@link XMLSchedulingDataProcessorPlugin} that does not reference 
 * {@code javax.transaction.UserTransaction} as so does not require a dependency
 * on JTA.
 * 
 * @author Andrew Phillips
 * @see XMLSchedulingDataProcessorPlugin
 */
public class TransactionlessXmlSchedulingDataProcessorPlugin implements
        FileScanListener, SchedulerPlugin {

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Data members.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private static final int MAX_JOB_TRIGGER_NAME_LEN = 80;
    private static final String JOB_INITIALIZATION_PLUGIN_NAME = "JobSchedulingDataLoaderPlugin";
    private static final String FILE_NAME_DELIMITERS = ",";

    private String name;
    private Scheduler scheduler;
    private final Logger log = Logger.CONSOLE;

    private boolean failOnFileNotFound = true;

    private String fileNames = XMLSchedulingDataProcessor.QUARTZ_XML_DEFAULT_FILE_NAME;

    // Populated by initialization
    private Map<String, JobFile> jobFiles = new LinkedHashMap<String, JobFile>();

    private long scanInterval = 0; 

    boolean started = false;

    protected ClassLoadHelper classLoadHelper = null;

    private Set<String> jobTriggerNameSet = new HashSet<String>();

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Interface.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * Comma separated list of file names (with paths) to the XML files that should be read.
     */
    public String getFileNames() {
        return fileNames;
    }

    /**
     * The file name (and path) to the XML file that should be read.
     */
    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    /**
     * The interval (in seconds) at which to scan for changes to the file.  
     * If the file has been changed, it is re-loaded and parsed.   The default 
     * value for the interval is 0, which disables scanning.
     * 
     * @return Returns the scanInterval.
     */
    public long getScanInterval() {
        return scanInterval / 1000;
    }

    /**
     * The interval (in seconds) at which to scan for changes to the file.  
     * If the file has been changed, it is re-loaded and parsed.   The default 
     * value for the interval is 0, which disables scanning.
     * 
     * @param scanInterval The scanInterval to set.
     */
    public void setScanInterval(long scanInterval) {
        this.scanInterval = scanInterval * 1000;
    }

    /**
     * Whether or not initialization of the plugin should fail (throw an
     * exception) if the file cannot be found. Default is <code>true</code>.
     */
    public boolean isFailOnFileNotFound() {
        return failOnFileNotFound;
    }

    /**
     * Whether or not initialization of the plugin should fail (throw an
     * exception) if the file cannot be found. Default is <code>true</code>.
     */
    public void setFailOnFileNotFound(boolean failOnFileNotFound) {
        this.failOnFileNotFound = failOnFileNotFound;
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * SchedulerPlugin Interface.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * <p>
     * Called during creation of the <code>Scheduler</code> in order to give
     * the <code>SchedulerPlugin</code> a chance to initialize.
     * </p>
     * 
     * @throws org.quartz.SchedulerConfigException
     *           if there is an error initializing.
     */
    @Override
    public void initialize(String name, Scheduler scheduler)
        throws SchedulerException {
        this.name = name;
        this.scheduler = scheduler;

        classLoadHelper = new CascadingClassLoadHelper();
        classLoadHelper.initialize();

        log.info("Registering Quartz Job Initialization Plug-in.");

        // Create JobFile objects
        StringTokenizer stok = new StringTokenizer(fileNames, FILE_NAME_DELIMITERS);
        while (stok.hasMoreTokens()) {
            final String fileName = stok.nextToken();
            final JobFile jobFile = new JobFile(fileName);
            jobFiles.put(fileName, jobFile);
        }
    }

    @Override
    public void start() {
        try {
            if (jobFiles.isEmpty() == false) {

                if (scanInterval > 0) {
                    scheduler.getContext().put(JOB_INITIALIZATION_PLUGIN_NAME + '_' + name, this);
                }

                Iterator<JobFile> iterator = jobFiles.values().iterator();
                while (iterator.hasNext()) {
                    JobFile jobFile = iterator.next();

                    if (scanInterval > 0) {
                        String jobTriggerName = buildJobTriggerName(jobFile.getFileBasename());
                        TriggerKey tKey = new TriggerKey(jobTriggerName, JOB_INITIALIZATION_PLUGIN_NAME);

                        // remove pre-existing job/trigger, if any
                        scheduler.unscheduleJob(tKey);

                        // TODO: convert to use builder
                        SimpleTrigger trig = newTrigger()
                            .withIdentity(jobTriggerName, JOB_INITIALIZATION_PLUGIN_NAME)
                            .startNow()
                            .endAt(null)
                            .withSchedule(simpleSchedule()
                                    .repeatForever()
                                    .withIntervalInMilliseconds(scanInterval))
                            .build();

                        JobDetail job = JobBuilder.newJob(FileScanJob.class)
                                        .withIdentity(jobTriggerName, JOB_INITIALIZATION_PLUGIN_NAME)
                                        .build();
                        job.getJobDataMap().put(FileScanJob.FILE_NAME, jobFile.getFileName());
                        job.getJobDataMap().put(FileScanJob.FILE_SCAN_LISTENER_NAME, JOB_INITIALIZATION_PLUGIN_NAME + '_' + name);

                        scheduler.scheduleJob(job, trig);
                        log.debug("Scheduled file scan job for data file: {}, at interval: {}", jobFile.getFileName(), scanInterval);
                    }

                    processFile(jobFile);
                }
            }
        } catch(SchedulerException se) {
            log.error("Error starting background-task for watching jobs file.", se);
        } finally {
            started = true;
        }
    }

    /**
     * Helper method for generating unique job/trigger name for the  
     * file scanning jobs (one per FileJob).  The unique names are saved
     * in jobTriggerNameSet.
     */
    private String buildJobTriggerName(
            String fileBasename) {
        // Name w/o collisions will be prefix + _ + filename (with '.' of filename replaced with '_')
        // For example: JobInitializationPlugin_jobInitializer_myjobs_xml
        String jobTriggerName = JOB_INITIALIZATION_PLUGIN_NAME + '_' + name + '_' + fileBasename.replace('.', '_');

        // If name is too long (DB column is 80 chars), then truncate to max length
        if (jobTriggerName.length() > MAX_JOB_TRIGGER_NAME_LEN) {
            jobTriggerName = jobTriggerName.substring(0, MAX_JOB_TRIGGER_NAME_LEN);
        }

        // Make sure this name is unique in case the same file name under different
        // directories is being checked, or had a naming collision due to length truncation.
        // If there is a conflict, keep incrementing a _# suffix on the name (being sure
        // not to get too long), until we find a unique name.
        int currentIndex = 1;
        while (jobTriggerNameSet.add(jobTriggerName) == false) {
            // If not our first time through, then strip off old numeric suffix
            if (currentIndex > 1) {
                jobTriggerName = jobTriggerName.substring(0, jobTriggerName.lastIndexOf('_'));
            }

            String numericSuffix = "_" + currentIndex++;

            // If the numeric suffix would make the name too long, then make room for it.
            if (jobTriggerName.length() > (MAX_JOB_TRIGGER_NAME_LEN - numericSuffix.length())) {
                jobTriggerName = jobTriggerName.substring(0, (MAX_JOB_TRIGGER_NAME_LEN - numericSuffix.length()));
            }

            jobTriggerName += numericSuffix;
        }

        return jobTriggerName;
    }

    @Override
    public void shutdown() {
        // nothing to do
    }

    private void processFile(JobFile jobFile) {
        if (jobFile == null || !jobFile.getFileFound()) {
            return;
        }

        try {
            XMLSchedulingDataProcessor processor = 
                new XMLSchedulingDataProcessor(this.classLoadHelper);

            processor.addJobGroupToNeverDelete(JOB_INITIALIZATION_PLUGIN_NAME);
            processor.addTriggerGroupToNeverDelete(JOB_INITIALIZATION_PLUGIN_NAME);

            processor.processFileAndScheduleJobs(
                    jobFile.getFileName(), 
                    jobFile.getFileName(), // systemId 
                    scheduler);
        } catch (Exception e) {
            log.error("Error scheduling jobs: " + e.getMessage(), e);
        }
    }

    public void processFile(String filePath) {
        processFile((JobFile)jobFiles.get(filePath));
    }

    /** 
     * @see org.quartz.jobs.FileScanListener#fileUpdated(java.lang.String)
     */
    public void fileUpdated(String fileName) {
        if (started) {
            processFile(fileName);
        }
    }

    class JobFile {
        private String fileName;

        // These are set by initialize()
        private String filePath;
        private String fileBasename;
        private boolean fileFound;

        protected JobFile(String fileName) throws SchedulerException {
            this.fileName = fileName;
            initialize();
        }

        protected String getFileName() {
            return fileName;
        }

        protected boolean getFileFound() {
            return fileFound;
        }

        protected String getFilePath() {
            return filePath;
        }

        protected String getFileBasename() {
            return fileBasename;
        }

        private void initialize() throws SchedulerException {
            InputStream f = null;
            try {
                String furl = null;

                File file = new File(getFileName()); // files in filesystem
                if (!file.exists()) {
                    URL url = classLoadHelper.getResource(getFileName());
                    if(url != null) {
                        try {
                            furl = URLDecoder.decode(url.getPath(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            furl = url.getPath();
                        }
                        file = new File(furl);
                        try {
                            f = url.openStream();
                        } catch (IOException ignore) {
                            // Swallow the exception
                        }
                    }
                } else {
                    try {
                        f = new java.io.FileInputStream(file);
                    }catch (FileNotFoundException e) {
                        // ignore
                    }
                }

                if (f == null) {
                    if (isFailOnFileNotFound()) {
                        throw new SchedulerException(
                            "File named '" + getFileName() + "' does not exist.");
                    } else {
                        log.warn("File named '" + getFileName() + "' does not exist.");
                    }
                } else {
                    fileFound = true;
                }
                filePath = (furl != null) ? furl : file.getAbsolutePath();
                fileBasename = file.getName();
            } finally {
                try {
                    if (f != null) {
                        f.close();
                    }
                } catch (IOException ioe) {
                    log.warn("Error closing jobs file " + getFileName(), ioe);
                }
            }
        }
    }
}