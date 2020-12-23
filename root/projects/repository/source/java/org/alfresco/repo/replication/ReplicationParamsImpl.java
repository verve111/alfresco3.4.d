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
package org.alfresco.repo.replication;


/**
 * Configurable system parameters.
 */
public class ReplicationParamsImpl implements ReplicationParams
{
    /** Lock replication items? */
    private boolean readOnly = true;
    
    public ReplicationParamsImpl()
    {
    }

    /**
     * Sets whether to lock replicated items
     * 
     * @param readOnly <code>true</code> lock replicated items in target repository
     */
    public void setTransferReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.replication.ReplicationParams#getTransferReadOnly()
     */
    public boolean getTransferReadOnly()
    {
        return this.readOnly;
    }
}
