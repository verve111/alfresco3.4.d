/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.util.exec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;
import org.alfresco.util.exec.RuntimeExec.ExecutionResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;

/**
 * Application bootstrap bean that is able to execute one or more
 * native executable statements upon startup and shutdown.
 * 
 * @author Derek Hulley
 */
public class RuntimeExecBootstrapBean extends AbstractLifecycleBean
{
    private static Log logger = LogFactory.getLog(RuntimeExecBootstrapBean.class);
    
    private List<RuntimeExec> startupCommands;
    private boolean failOnError;
    private boolean killProcessesOnShutdown;
    private boolean enabled;
    
    /** Keep track of the processes so that we can kill them on shutdown */
    private List<ExecutionResult> executionResults;

    private Thread shutdownThread;

    /**
     * Initializes the bean
     * <ul>
     *   <li>failOnError = true</li>
     *   <li>killProcessesOnShutdown = true</li>
     *   <li>enabled = true</li>
     * </ul>
     */
    public RuntimeExecBootstrapBean()
    {
        this.startupCommands = Collections.emptyList();
        this.executionResults = new ArrayList<ExecutionResult>(1);
        failOnError = true;
        killProcessesOnShutdown = true;
        enabled = true;
    }

    /**
     * Set the commands to execute, in sequence, when the application context
     * is initialized.
     * 
     * @param startupCommands list of commands
     */
    public void setStartupCommands(List<RuntimeExec> startupCommands)
    {
        this.startupCommands = startupCommands;
    }

    /**
     * Set whether a process failure generates an error or not.  Deviation from the default is
     * useful if use as part of a process where the command or the codes generated by the
     * execution may be ignored or avoided by the system.
     * 
     * @param failOnError           <tt>true<tt> (default) to issue an error message and throw an
     *                              exception if the process fails to execute or generates an error
     *                              return value.
     * 
     * @since 2.1
     */
    public void setFailOnError(boolean failOnError)
    {
        this.failOnError = failOnError;
    }

    /**
     * Set whether or not to force a shutdown of successfully started processes.  As most
     * bootstrap processes are kicked off in order to provide the server with some or other
     * service, this is <tt>true</tt> by default.
     * 
     * @param killProcessesOnShutdown
     *          <tt>true</tt> to force any successfully executed commands' processes to
     *          be forcibly killed when the server shuts down.
     * 
     * @since 2.1.0
     */
    public void setKillProcessesOnShutdown(boolean killProcessesOnShutdown)
    {
        this.killProcessesOnShutdown = killProcessesOnShutdown;
    }

    /**
     * Set whether or not the process should be disabled at ApplicationContext bootstrap.
     * If a RuntimeExecBootstrapBean is disabled, then the command will not be executed.
     * This property is not required and is <code>false</code> by default.
     * <P/>
     * This method has been deprecated in favour of a clearer name introduced in 3.3.
     * See {@link #setEnabled}.
     * 
     * @param disabledAtStartUp any String which <code>equalsIgnoreCase("true")</code>
     *        to prevent the command from being executed.
     * @since 3.2.1
     * @deprecated Use {@link #setEnabled} instead, remembering that the boolean property should be inverted.
     */
    public void setDisabledAtStartUp(String disabledAtStartUp)
    {
    	boolean disabled = Boolean.parseBoolean(disabledAtStartUp);
    	this.setEnabled(Boolean.toString(!disabled));
    }

    /**
     * Set whether or not the process should be enabled at ApplicationContext bootstrap.
     * If a RuntimeExecBootstrapBean is not enabled, then the command will not be executed.
     * This property is not required and is <code>true</code> by default.
     * 
     * @param enabled any String which does not <code>equalsIgnoreCase("true")</code>
     *        will prevent the command from being executed.
     * 
     * @since 3.3
     */
    public void setEnabled(String enabled)
    {
        // A String parameter rather than a boolean parameter is used here in order to allow
        // the injection of properties ${foo.bar}. In this way undefined properties (which will
        // be injected as "${foo.bar}") will mean the parameter is equivalent to false.
        this.enabled = Boolean.parseBoolean(enabled);
    }
    
    @Override
    protected synchronized void onBootstrap(ApplicationEvent event)
    {
        // If the command is disabled then do nothing.
        if (this.enabled == false)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Bootstrap execution of " + startupCommands.size() + " was not enabled");
            }
            return;
        }
        // execute
        for (RuntimeExec command : startupCommands)
        {
            ExecutionResult result = command.execute();
            // check for failure
            if (!result.getSuccess())
            {
                String msg = "Bootstrap command failed: \n" + result;
                if (failOnError)
                {
                    throw new AlfrescoRuntimeException(msg);
                }
                else
                {
                    logger.error(msg);
                }
            }
            else
            {
                // It executed, so keep track of it
                executionResults.add(result);
            }
        }
        if (killProcessesOnShutdown)
        {
            // Force a shutdown on VM termination as we can't rely on the Spring context termination
            this.shutdownThread = new KillProcessShutdownThread();
            Runtime.getRuntime().addShutdownHook(this.shutdownThread);
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Bootstrap execution of " + startupCommands.size() + " commands was successful");
        }
    }

    /**
     * A thread that serves to kill the successfully created process, if required
     * 
     * @since 2.1
     * @author Derek Hulley
     */
    private class KillProcessShutdownThread extends Thread
    {
        @Override
        public void run()
        {
            doShutdown();
        }
    }

    /**
     * Handle the shutdown of a subsystem but not the entire VM
     */
    @Override
    protected synchronized void onShutdown(ApplicationEvent event)
    {
        if (this.enabled == false)
        {
            return;
        }
        
        try
        {
            // We managed to stop the process ourselves (e.g. on subsystem shutdown). Remove the shutdown hook
            Runtime.getRuntime().removeShutdownHook(this.shutdownThread);
            doShutdown();
        }
        catch (IllegalStateException e)
        {
            // The system is shutting down - we'll have to let the shutdown hook run
        }
    }
    
    private void doShutdown()
    {
        if (!killProcessesOnShutdown)
        {
            // Do not force a kill
            return;
        }
        for (ExecutionResult executionResult : executionResults)
        {
            executionResult.killProcess();
        }        
    }
}
