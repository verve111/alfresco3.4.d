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

package org.alfresco.service.cmr.avm.deploy;

public class DeploymentReportCallback implements DeploymentCallback 
{
     private DeploymentReport report;
     
     public DeploymentReportCallback(DeploymentReport report )
     {
			   this.report = report;
     }
	       
	 /**
	   * Called each time something happens during deployment.
	   * This is called synchronously by the deployer and should 
	   * therefore be handled rapidly, if possible.
	   * @param event The event that occurred.
	   */
	  public void eventOccurred(DeploymentEvent event){
	      report.add(event);
	  }
}
