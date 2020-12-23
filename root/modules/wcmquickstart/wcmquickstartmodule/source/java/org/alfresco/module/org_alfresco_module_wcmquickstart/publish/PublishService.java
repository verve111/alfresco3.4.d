/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import java.util.Collection;

import org.alfresco.service.cmr.repository.NodeRef;

public interface PublishService
{

    public void enqueuePublishedNodes(NodeRef... nodes);

    public void enqueueRemovedNodes(NodeRef... nodes);

    public void enqueuePublishedNodes(Collection<NodeRef> nodes);

    public void enqueueRemovedNodes(Collection<NodeRef> nodes);

    public void publishQueue(NodeRef websiteId);

    public String getTransferTargetName();

}
