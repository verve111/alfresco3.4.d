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

package org.alfresco.module.vti.handler.alfresco.v3;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.CheckOutCheckInServiceHandler;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Alfresco implementation of CheckOutCheckInServiceHandler
 * 
 * @author DmitryVas
 */
public class AlfrescoCheckOutCheckInServiceHandler implements CheckOutCheckInServiceHandler
{
    private static Log logger = LogFactory.getLog(AlfrescoCheckOutCheckInServiceHandler.class);

    private VtiPathHelper pathHelper;
    private CheckOutCheckInService checkOutCheckInService;
    private LockService lockService;
    private TransactionService transactionService;
    private NodeService nodeService;
    private AuthenticationService authenticationService;

    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
    }

    public void setLockService(LockService lockService)
    {
        this.lockService = lockService;
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    /**
     * @see org.alfresco.module.vti.handler.CheckOutCheckInServiceHandler#undoCheckOutDocument(java.lang.String)
     */
    public NodeRef undoCheckOutDocument(final String fileName, final boolean lockAfterSucess)
    {
        NodeRef originalNode = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute()
            {
                try
                {

                    FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(fileName);
                    NodeRef workingCopy = checkOutCheckInService.getWorkingCopy(documentFileInfo.getNodeRef());
                    String workingCopyOwner = nodeService.getProperty(workingCopy, ContentModel.PROP_WORKING_COPY_OWNER).toString();
                    if (!workingCopyOwner.equals(authenticationService.getCurrentUserName()))
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Unable to perform undo check out. Not an owner!!!");
                        }

                        return null;
                    }

                    NodeRef originalNode = checkOutCheckInService.cancelCheckout(workingCopy);
                    if (lockAfterSucess)
                    {
                        lockService.lock(originalNode, LockType.WRITE_LOCK, WebDAV.TIMEOUT_INFINITY);
                    }
                    return originalNode;

                }
                catch (Exception e)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Can't perform 'undo check out' operation for file '" + fileName + "'", e);
                    }
                    return null;
                }

            }
        });

        return originalNode;
    }

    /**
     * @see org.alfresco.module.vti.handler.CheckOutCheckInServiceHandler#checkInDocument(java.lang.String, java.lang.String)
     */
    public NodeRef checkInDocument(final String fileName, final String comment)
    {
        NodeRef originalNode = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute()
            {
                try
                {
                    FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(fileName);
                    NodeRef workingCopy = checkOutCheckInService.getWorkingCopy(documentFileInfo.getNodeRef());
                    String workingCopyOwner = nodeService.getProperty(workingCopy, ContentModel.PROP_WORKING_COPY_OWNER).toString();
                    if (!workingCopyOwner.equals(authenticationService.getCurrentUserName()))
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Unable to perform check in. Not an owner!!!");
                        }

                        return null;
                    }

                    Map<String, Serializable> versionProperties = new HashMap<String, Serializable>(1, 1.0f);
                    versionProperties.put(Version.PROP_DESCRIPTION, comment);
                    versionProperties.put(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR);

                    NodeRef originalNode = checkOutCheckInService.checkin(workingCopy, versionProperties);
                    lockService.lock(originalNode, LockType.WRITE_LOCK, WebDAV.TIMEOUT_INFINITY);

                    return originalNode;

                }
                catch (Exception e)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Can't perform 'check in' operation for file '" + fileName + "'", e);
                    }
                    return null;
                }
            }
        });

        return originalNode;
    }

    /**
     * @see org.alfresco.module.vti.handler.CheckOutCheckInServiceHandler#checkOutDocument(java.lang.String)
     */
    public NodeRef checkOutDocument(final String fileName)
    {
        NodeRef workingCopy = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute()
            {
                try
                {
                    FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(fileName);
                    NodeRef workingCopy = checkOutCheckInService.checkout(documentFileInfo.getNodeRef());
                    lockService.lock(workingCopy, LockType.WRITE_LOCK, WebDAV.TIMEOUT_INFINITY);

                    return workingCopy;

                }
                catch (Exception e)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Can't perform 'check out' operation for file '" + fileName + "'", e);
                    }
                    return null;
                }
            }
        });

        return workingCopy;
    }

}
