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
package org.alfresco.repo.domain.node.ibatis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.repo.domain.node.AbstractNodeDAOImpl;
import org.alfresco.repo.domain.node.ChildAssocEntity;
import org.alfresco.repo.domain.node.ChildPropertyEntity;
import org.alfresco.repo.domain.node.NodeAspectsEntity;
import org.alfresco.repo.domain.node.NodeAssocEntity;
import org.alfresco.repo.domain.node.NodeEntity;
import org.alfresco.repo.domain.node.NodeIdAndAclId;
import org.alfresco.repo.domain.node.NodePropertyEntity;
import org.alfresco.repo.domain.node.NodePropertyKey;
import org.alfresco.repo.domain.node.NodePropertyValue;
import org.alfresco.repo.domain.node.NodeUpdateEntity;
import org.alfresco.repo.domain.node.PrimaryChildrenAclUpdateEntity;
import org.alfresco.repo.domain.node.ServerEntity;
import org.alfresco.repo.domain.node.StoreEntity;
import org.alfresco.repo.domain.node.Transaction;
import org.alfresco.repo.domain.node.TransactionEntity;
import org.alfresco.repo.domain.node.TransactionQueryEntity;
import org.alfresco.repo.domain.qname.QNameDAO;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.util.Assert;

import com.ibatis.sqlmap.client.event.RowHandler;
import com.sun.star.uno.RuntimeException;

/**
 * iBatis-specific extension of the Node abstract DAO 
 * 
 * @author Derek Hulley
 * @since 3.4
 */
public class NodeDAOImpl extends AbstractNodeDAOImpl
{
    private static final String SELECT_SERVER_BY_IPADDRESS = "alfresco.node.select_ServerByIpAddress";
    private static final String INSERT_SERVER = "alfresco.node.insert_Server";
    private static final String INSERT_TRANSACTION = "alfresco.node.insert_Transaction";
    private static final String UPDATE_TRANSACTION_COMMIT_TIME = "alfresco.node.update_TransactionCommitTime";
    private static final String DELETE_TRANSACTION_BY_ID = "alfresco.node.delete_TransactionById";
    private static final String INSERT_STORE = "alfresco.node.insert_Store";
    private static final String UPDATE_STORE_ROOT = "alfresco.node.update_StoreRoot";
    private static final String UPDATE_STORE = "alfresco.node.update_Store";
    private static final String SELECT_STORE_ALL = "alfresco.node.select_StoreAll";
    private static final String SELECT_STORE_ROOT_NODE_BY_ID = "alfresco.node.select_StoreRootNodeById";
    private static final String SELECT_STORE_ROOT_NODE_BY_REF = "alfresco.node.select_StoreRootNodeByRef";
    private static final String INSERT_NODE = "alfresco.node.insert_Node";
    private static final String UPDATE_NODE = "alfresco.node.update_Node";
    private static final String UPDATE_NODE_PATCH_ACL = "alfresco.node.update_NodePatchAcl";
    private static final String DELETE_NODE_BY_ID = "alfresco.node.delete_NodeById";
    private static final String DELETE_NODES_BY_TXN_COMMIT_TIME = "alfresco.node.delete_NodesByTxnCommitTime";
    private static final String SELECT_NODE_BY_ID = "alfresco.node.select_NodeById";
    private static final String SELECT_NODE_BY_NODEREF = "alfresco.node.select_NodeByNodeRef";
    private static final String SELECT_NODES_BY_UUIDS = "alfresco.node.select_NodesByUuids";
    private static final String SELECT_NODE_PROPERTIES = "alfresco.node.select_NodeProperties";
    private static final String SELECT_NODE_ASPECTS = "alfresco.node.select_NodeAspects";
    private static final String INSERT_NODE_PROPERTY = "alfresco.node.insert_NodeProperty";
    private static final String UPDATE_PRIMARY_CHILDREN_SHARED_ACL = "alfresco.node.update_PrimaryChildrenSharedAcl";
    private static final String INSERT_NODE_ASPECT = "alfresco.node.insert_NodeAspect";
    private static final String DELETE_NODE_ASPECTS = "alfresco.node.delete_NodeAspects";
    private static final String DELETE_NODE_PROPERTIES = "alfresco.node.delete_NodeProperties";
    private static final String SELECT_NODES_WITH_ASPECT_ID = "alfresco.node.select_NodesWithAspectId";
    private static final String INSERT_NODE_ASSOC = "alfresco.node.insert_NodeAssoc";
    private static final String DELETE_NODE_ASSOC = "alfresco.node.delete_NodeAssoc";
    private static final String DELETE_NODE_ASSOCS_TO_AND_FROM = "alfresco.node.delete_NodeAssocsToAndFrom";
    private static final String SELECT_NODE_ASSOCS_BY_SOURCE = "alfresco.node.select_NodeAssocsBySource";
    private static final String SELECT_NODE_ASSOCS_BY_TARGET = "alfresco.node.select_NodeAssocsByTarget";
    private static final String SELECT_NODE_ASSOC_BY_ID = "alfresco.node.select_NodeAssocById";
    private static final String SELECT_NODE_PRIMARY_CHILD_ACLS = "alfresco.node.select_NodePrimaryChildAcls";
    private static final String INSERT_CHILD_ASSOC = "alfresco.node.insert_ChildAssoc";
    private static final String DELETE_CHILD_ASSOC_BY_ID = "alfresco.node.delete_ChildAssocById";
    private static final String UPDATE_CHILD_ASSOCS_INDEX = "alfresco.node.update_ChildAssocsIndex";
    private static final String UPDATE_CHILD_ASSOCS_UNIQUE_NAME = "alfresco.node.update_ChildAssocsUniqueName";
    private static final String DELETE_CHILD_ASSOCS_TO_AND_FROM = "alfresco.node.delete_ChildAssocsToAndFrom";
    private static final String SELECT_CHILD_ASSOC_BY_ID = "alfresco.node.select_ChildAssocById";
    private static final String SELECT_CHILD_ASSOCS_BY_PROPERTY_VALUE = "alfresco.node.select_ChildAssocsByPropertyValue";
    private static final String SELECT_CHILD_ASSOCS_OF_PARENT = "alfresco.node.select_ChildAssocsOfParent";
    private static final String SELECT_CHILD_ASSOCS_OF_PARENT_WITHOUT_PARENT_ASSOCS_OF_TYPE =
            "alfresco.node.select_ChildAssocsOfParentWithoutParentAssocsOfType";
    private static final String SELECT_PARENT_ASSOCS_OF_CHILD = "alfresco.node.select_ParentAssocsOfChild";
    private static final String UPDATE_PARENT_ASSOCS_OF_CHILD = "alfresco.node.update_ParentAssocsOfChild";
    private static final String SELECT_TXN_LAST = "alfresco.node.select_TxnLast";
    private static final String SELECT_TXN_NODES = "alfresco.node.select_TxnNodes";
    private static final String SELECT_TXNS = "alfresco.node.select_Txns";
    private static final String SELECT_TXN_COUNT = "alfresco.node.select_TxnCount";
    private static final String SELECT_TXN_NODE_COUNT = "alfresco.node.select_TxnNodeCount";
    private static final String SELECT_TXNS_UNUSED = "alfresco.node.select_TxnsUnused";
    private static final String SELECT_TXN_MIN_COMMIT_TIME = "alfresco.node.select_TxnMinCommitTime";
    private static final String SELECT_TXN_MAX_COMMIT_TIME = "alfresco.node.select_TxnMaxCommitTime";
    
    private SqlMapClientTemplate template;
    private QNameDAO qnameDAO;
    private DictionaryService dictionaryService;

    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate)
    {
        this.template = sqlMapClientTemplate;
    }

    @Override
    public void setQnameDAO(QNameDAO qnameDAO)
    {
        this.qnameDAO = qnameDAO;
        super.setQnameDAO(qnameDAO);
    }

    @Override
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
        super.setDictionaryService(dictionaryService);
    }

    public void startBatch()
    {
        try
        {
            template.getSqlMapClient().startBatch();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Failed to start DAO batch.", e);
        }
    }

    public void executeBatch()
    {
        try
        {
            template.getSqlMapClient().executeBatch();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Failed to start DAO batch.", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ServerEntity selectServer(String ipAddress)
    {
        ServerEntity entity = new ServerEntity();
        entity.setIpAddress(ipAddress);
        // Potentially more results if there is a case issue (unlikely)
        List<ServerEntity> results = template.queryForList(SELECT_SERVER_BY_IPADDRESS, entity);
        for (ServerEntity serverEntity : results)
        {
            // Take the first one that matches regardless of case
            if (serverEntity.getIpAddress().equalsIgnoreCase(ipAddress))
            {
                return serverEntity;
            }
        }
        // There was no match
        return null;
    }

    @Override
    protected Long insertServer(String ipAddress)
    {
        ServerEntity entity = new ServerEntity();
        entity.setVersion(1L);
        entity.setIpAddress(ipAddress);
        return (Long) template.insert(INSERT_SERVER, entity);
    }

    @Override
    protected Long insertTransaction(Long serverId, String changeTxnId, Long commitTimeMs)
    {
        ServerEntity server = new ServerEntity();
        server.setId(serverId);
        TransactionEntity transaction = new TransactionEntity();
        transaction.setServer(server);
        transaction.setVersion(1L);
        transaction.setChangeTxnId(changeTxnId);
        transaction.setCommitTimeMs(commitTimeMs);
        return (Long) template.insert(INSERT_TRANSACTION, transaction);
    }

    @Override
    protected int updateTransaction(Long txnId, Long commitTimeMs)
    {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setId(txnId);
        transaction.setCommitTimeMs(commitTimeMs);
        return template.update(UPDATE_TRANSACTION_COMMIT_TIME, transaction);
    }

    @Override
    protected int deleteTransaction(Long txnId)
    {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setId(txnId);
        return template.delete(DELETE_TRANSACTION_BY_ID, transaction);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<StoreEntity> selectAllStores()
    {
        return template.queryForList(SELECT_STORE_ALL);
    }

    @Override
    protected NodeEntity selectStoreRootNode(Long storeId)
    {
        StoreEntity store = new StoreEntity();
        store.setId(storeId);
        return (NodeEntity) template.queryForObject(SELECT_STORE_ROOT_NODE_BY_ID, store);
    }

    @Override
    protected NodeEntity selectStoreRootNode(StoreRef storeRef)
    {
        StoreEntity store = new StoreEntity();
        store.setProtocol(storeRef.getProtocol());
        store.setIdentifier(storeRef.getIdentifier());
        return (NodeEntity) template.queryForObject(SELECT_STORE_ROOT_NODE_BY_REF, store);
    }

    @Override
    protected Long insertStore(StoreEntity store)
    {
        store.setVersion(1L);
        return (Long) template.insert(INSERT_STORE, store);
    }

    @Override
    protected int updateStoreRoot(StoreEntity store)
    {
        return template.update(UPDATE_STORE_ROOT, store);
    }

    @Override
    protected int updateStore(StoreEntity store)
    {
        return template.update(UPDATE_STORE, store);
    }

    @Override
    protected Long insertNode(NodeEntity node)
    {
        node.setVersion(1L);
        return (Long) template.insert(INSERT_NODE, node);
    }

    @Override
    protected int updateNode(NodeUpdateEntity nodeUpdate)
    {
        // Increment the version
        nodeUpdate.incrementVersion();
        return template.update(UPDATE_NODE, nodeUpdate);
    }
    
    @Override
    protected int updateNodePatchAcl(NodeUpdateEntity nodeUpdate)
    {
        return template.update(UPDATE_NODE_PATCH_ACL, nodeUpdate);
    }

    @Override
    protected void updatePrimaryChildrenSharedAclId(
            Long primaryParentNodeId,
            Long optionalOldSharedAlcIdInAdditionToNull,
            Long newSharedAlcId)
    {
        PrimaryChildrenAclUpdateEntity primaryChildrenAclUpdateEntity = new PrimaryChildrenAclUpdateEntity();
        primaryChildrenAclUpdateEntity.setPrimaryParentNodeId(primaryParentNodeId);
        primaryChildrenAclUpdateEntity.setOptionalOldSharedAclIdInAdditionToNull(optionalOldSharedAlcIdInAdditionToNull);
        primaryChildrenAclUpdateEntity.setNewSharedAclId(newSharedAlcId);
        
        template.update(UPDATE_PRIMARY_CHILDREN_SHARED_ACL, primaryChildrenAclUpdateEntity);
    }

    @Override
    protected int deleteNodeById(Long nodeId, boolean deletedOnly)
    {
        NodeEntity node = new NodeEntity();
        node.setId(nodeId);
        // Do we delete everything (false) or just nodes already marked as deleted (true)
        node.setDeleted(deletedOnly);
        return template.delete(DELETE_NODE_BY_ID, node);
    }

    @Override
    protected int deleteNodesByCommitTime(boolean deletedOnly, long maxTxnCommitTimeMs)
    {
        TransactionQueryEntity query = new TransactionQueryEntity();
        query.setDeletedNodes(Boolean.TRUE);
        query.setMaxCommitTime(maxTxnCommitTimeMs);
        return template.delete(DELETE_NODES_BY_TXN_COMMIT_TIME, query);
    }

    @Override
    protected NodeEntity selectNodeById(Long id, Boolean deleted)
    {
        NodeEntity node = new NodeEntity();
        node.setId(id);
        // Deleted
        if (deleted != null)
        {
            node.setDeleted(deleted);
        }
        
        return (NodeEntity) template.queryForObject(SELECT_NODE_BY_ID, node);
    }

    @Override
    protected NodeEntity selectNodeByNodeRef(NodeRef nodeRef, Boolean deleted)
    {
        StoreEntity store = new StoreEntity();
        StoreRef storeRef = nodeRef.getStoreRef();
        store.setProtocol(storeRef.getProtocol());
        store.setIdentifier(storeRef.getIdentifier());
        
        NodeEntity node = new NodeEntity();
        // Store
        node.setStore(store);
        // UUID
        node.setUuid(nodeRef.getId());
        // Deleted
        if (deleted != null)
        {
            node.setDeleted(deleted);
        }
        
        return (NodeEntity) template.queryForObject(SELECT_NODE_BY_NODEREF, node);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<NodeEntity> selectNodesByUuids(Long storeId, SortedSet<String> uuids)
    {
        NodeBatchLoadEntity nodeBatchLoadEntity = new NodeBatchLoadEntity();
        nodeBatchLoadEntity.setStoreId(storeId);
        nodeBatchLoadEntity.setUuids(new ArrayList<String>(uuids));
        
        return (List<NodeEntity>) template.queryForList(SELECT_NODES_BY_UUIDS, nodeBatchLoadEntity);
    }

    /**
     * Pull out the key-value pairs from the rows
     */
    private Map<Long, Map<NodePropertyKey, NodePropertyValue>> makePersistentPropertiesMap(List<NodePropertyEntity> rows)
    {
        Map<Long, Map<NodePropertyKey, NodePropertyValue>> results = new HashMap<Long, Map<NodePropertyKey, NodePropertyValue>>(3);
        for (NodePropertyEntity row : rows)
        {
            Long nodeId = row.getNodeId();
            if (nodeId == null)
            {
                throw new RuntimeException("Expect results with a Node ID: " + row);
            }
            Map<NodePropertyKey, NodePropertyValue> props = results.get(nodeId);
            if (props == null)
            {
                props = new HashMap<NodePropertyKey, NodePropertyValue>(17);
                results.put(nodeId, props);
            }
            props.put(row.getKey(), row.getValue());
        }
        // Done
        return results;
    }
    
    /**
     * Convert key-value pairs into rows
     */
    private List<NodePropertyEntity> makePersistentRows(Long nodeId, Map<NodePropertyKey, NodePropertyValue> map)
    {
        List<NodePropertyEntity> rows = new ArrayList<NodePropertyEntity>(map.size());
        for (Map.Entry<NodePropertyKey, NodePropertyValue> entry : map.entrySet())
        {
            NodePropertyEntity row = new NodePropertyEntity();
            row.setNodeId(nodeId);
            row.setKey(entry.getKey());
            row.setValue(entry.getValue());
            rows.add(row);
        }
        // Done
        return rows;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected List<NodeAspectsEntity> selectNodeAspects(Set<Long> nodeIds)
    {
        if (nodeIds.size() == 0)
        {
            return Collections.emptyList();
        }
        NodeAspectsEntity aspects = new NodeAspectsEntity();
        aspects.setNodeIds(new ArrayList<Long>(nodeIds));

        List<NodeAspectsEntity> rows = template.queryForList(SELECT_NODE_ASPECTS, aspects);
        return rows;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected Map<Long, Map<NodePropertyKey, NodePropertyValue>> selectNodeProperties(Set<Long> nodeIds)
    {
        if (nodeIds.size() == 0)
        {
            return Collections.emptyMap();
        }
        NodePropertyEntity prop = new NodePropertyEntity();
        prop.setNodeIds(new ArrayList<Long>(nodeIds));

        List<NodePropertyEntity> rows = template.queryForList(SELECT_NODE_PROPERTIES, prop);
        return makePersistentPropertiesMap(rows);
    }
    @Override
    protected Map<NodePropertyKey, NodePropertyValue> selectNodeProperties(Long nodeId)
    {
        return selectNodeProperties(nodeId, Collections.<Long>emptySet());
    }
    @Override
    @SuppressWarnings("unchecked")
    protected Map<NodePropertyKey, NodePropertyValue> selectNodeProperties(Long nodeId, Set<Long> qnameIds)
    {
        NodePropertyEntity prop = new NodePropertyEntity();
        // Node
        prop.setNodeId(nodeId);
        // QName(s)
        switch(qnameIds.size())
        {
        case 0:
            // Ignore
            break;
        case 1:
            prop.setKey(new NodePropertyKey());
            prop.getKey().setQnameId(qnameIds.iterator().next());
            break;
        default:
            prop.setQnameIds(new ArrayList<Long>(qnameIds));
        }
        
        List<NodePropertyEntity> rows = template.queryForList(SELECT_NODE_PROPERTIES, prop);
        Map<Long, Map<NodePropertyKey, NodePropertyValue>> results = makePersistentPropertiesMap(rows);
        Map<NodePropertyKey, NodePropertyValue> props = results.get(nodeId);
        if (props == null)
        {
            return Collections.emptyMap();
        }
        else
        {
            return props;
        }
    }

    @Override
    protected int deleteNodeProperties(Long nodeId, Set<Long> qnameIds)
    {
        NodePropertyEntity prop = new NodePropertyEntity();
        // Node
        prop.setNodeId(nodeId);
        // QNames
        if (qnameIds != null)
        {
            if (qnameIds.isEmpty())
            {
                return 0;         // Nothing to do
            }
            prop.setQnameIds(new ArrayList<Long>(qnameIds));
        }
        
        return template.delete(DELETE_NODE_PROPERTIES, prop);
    }

    @Override
    protected int deleteNodeProperties(Long nodeId, List<NodePropertyKey> propKeys)
    {
        Assert.notNull(nodeId, "Must have 'nodeId'");
        Assert.notNull(nodeId, "Must have 'propKeys'");
        
        if (propKeys.size() == 0)
        {
            return 0;
        }
        
        NodePropertyEntity prop = new NodePropertyEntity();
        // Node
        prop.setNodeId(nodeId);
        
        startBatch();
        int count = 0;
        try
        {
            for (NodePropertyKey propKey : propKeys)
            {
                prop.setKey(propKey);
                count += template.delete(DELETE_NODE_PROPERTIES, prop);
            }
        }
        finally
        {
            executeBatch();
        }
        return count;
    }

    @Override
    protected void insertNodeProperties(Long nodeId, Map<NodePropertyKey, NodePropertyValue> persistableProps)
    {
        if (persistableProps.isEmpty())
        {
            return;
        }
        
        List<NodePropertyEntity> rows = makePersistentRows(nodeId, persistableProps);
        
        startBatch();
        try
        {
            for (NodePropertyEntity row : rows)
            {
                template.insert(INSERT_NODE_PROPERTY, row);
            }
        }
        finally
        {
            executeBatch();
        }
    }

    @Override
    protected Set<Long> selectNodeAspectIds(Long nodeId)
    {
    	Set<Long> aspectIds = new HashSet<Long>();
    	Set<Long> nodeIds = new HashSet<Long>();
    	nodeIds.add(nodeId);
    	List<NodeAspectsEntity> nodeAspectEntities = selectNodeAspects(nodeIds);
    	if(nodeAspectEntities.size() > 0)
    	{
    		NodeAspectsEntity nodeAspects = nodeAspectEntities.get(0);
    		aspectIds.addAll(nodeAspects.getAspectQNameIds());
    	}
		return aspectIds;
    }

    @Override
    protected void insertNodeAspect(Long nodeId, Long qnameId)
    {
        Map<String, Long> aspectParameters = new HashMap<String, Long>(5);
        aspectParameters.put("nodeId", nodeId);
        aspectParameters.put("qnameId", qnameId);
        template.insert(INSERT_NODE_ASPECT, aspectParameters);
    }

    @Override
    protected int deleteNodeAspects(Long nodeId, Set<Long> qnameIds)
    {
        NodeAspectsEntity nodeAspects = new NodeAspectsEntity();
        nodeAspects.setNodeId(nodeId);
        if (qnameIds != null && !qnameIds.isEmpty())
        {
            nodeAspects.setAspectQNameIds(new ArrayList<Long>(qnameIds));                // Null means all
        }
        return template.delete(DELETE_NODE_ASPECTS, nodeAspects);
    }

    @Override
    protected void selectNodesWithAspect(Long qnameId, Long minNodeId, final NodeRefQueryCallback resultsCallback)
    {
        RowHandler rowHandler = new RowHandler()
        {
            public void handleRow(Object valueObject)
            {
                NodeEntity entity = (NodeEntity) valueObject;
                Pair<Long, NodeRef> nodePair = new Pair<Long, NodeRef>(entity.getId(), entity.getNodeRef());
                resultsCallback.handle(nodePair);
            }
        };
        
        Map<String, Long> parameters = new HashMap<String, Long>(5);
        parameters.put("minNodeId", minNodeId);
        parameters.put("qnameId", qnameId);
        template.queryWithRowHandler(SELECT_NODES_WITH_ASPECT_ID, parameters,rowHandler);
    }

    @Override
    protected Long insertNodeAssoc(Long sourceNodeId, Long targetNodeId, Long assocTypeQNameId)
    {
        NodeAssocEntity assoc = new NodeAssocEntity();
        assoc.setVersion(1L);
        assoc.setTypeQNameId(assocTypeQNameId);
        // Source
        NodeEntity sourceNode = new NodeEntity();
        sourceNode.setId(sourceNodeId);
        assoc.setSourceNode(sourceNode);
        // Target
        NodeEntity targetNode = new NodeEntity();
        targetNode.setId(targetNodeId);
        assoc.setTargetNode(targetNode);
        
        return (Long) template.insert(INSERT_NODE_ASSOC, assoc);
    }

    @Override
    protected int deleteNodeAssoc(Long sourceNodeId, Long targetNodeId, Long assocTypeQNameId)
    {
        NodeAssocEntity assoc = new NodeAssocEntity();
        assoc.setTypeQNameId(assocTypeQNameId);
        // Source
        NodeEntity sourceNode = new NodeEntity();
        sourceNode.setId(sourceNodeId);
        assoc.setSourceNode(sourceNode);
        // Target
        NodeEntity targetNode = new NodeEntity();
        targetNode.setId(targetNodeId);
        assoc.setTargetNode(targetNode);
        
        return template.delete(DELETE_NODE_ASSOC, assoc);
    }

    @Override
    protected int deleteNodeAssocsToAndFrom(Long nodeId)
    {
        NodeAssocEntity assoc = new NodeAssocEntity();
        // Source
        NodeEntity sourceNode = new NodeEntity();
        sourceNode.setId(nodeId);
        assoc.setSourceNode(sourceNode);
        // Target
        NodeEntity targetNode = new NodeEntity();
        targetNode.setId(nodeId);
        assoc.setTargetNode(targetNode);
        
        return template.delete(DELETE_NODE_ASSOCS_TO_AND_FROM, assoc);
    }

    @Override
    protected int deleteNodeAssocsToAndFrom(Long nodeId, Set<Long> assocTypeQNameIds)
    {
        NodeAssocEntity assoc = new NodeAssocEntity();
        assoc.setTypeQNameIds(new ArrayList<Long>(assocTypeQNameIds));
        // Source
        NodeEntity sourceNode = new NodeEntity();
        sourceNode.setId(nodeId);
        assoc.setSourceNode(sourceNode);
        // Target
        NodeEntity targetNode = new NodeEntity();
        targetNode.setId(nodeId);
        assoc.setTargetNode(targetNode);
        
        return template.delete(DELETE_NODE_ASSOCS_TO_AND_FROM, assoc);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<NodeAssocEntity> selectNodeAssocsBySource(Long sourceNodeId)
    {
        NodeAssocEntity assoc = new NodeAssocEntity();
        // Source
        NodeEntity sourceNode = new NodeEntity();
        sourceNode.setId(sourceNodeId);
        assoc.setSourceNode(sourceNode);
        
        return (List<NodeAssocEntity>) template.queryForList(SELECT_NODE_ASSOCS_BY_SOURCE, assoc);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<NodeAssocEntity> selectNodeAssocsByTarget(Long targetNodeId)
    {
        NodeAssocEntity assoc = new NodeAssocEntity();
        // Target
        NodeEntity targetNode = new NodeEntity();
        targetNode.setId(targetNodeId);
        assoc.setTargetNode(targetNode);
        
        return (List<NodeAssocEntity>) template.queryForList(SELECT_NODE_ASSOCS_BY_TARGET, assoc);
    }

    @Override
    protected NodeAssocEntity selectNodeAssocById(Long assocId)
    {
        NodeAssocEntity assoc = new NodeAssocEntity();
        assoc.setId(assocId);
        
        return (NodeAssocEntity) template.queryForObject(SELECT_NODE_ASSOC_BY_ID, assoc);
    }

    @Override
    protected Long insertChildAssoc(ChildAssocEntity assoc)
    {
        assoc.setVersion(1L);
        return (Long) template.insert(INSERT_CHILD_ASSOC, assoc);
    }

    @Override
    protected int deleteChildAssocById(Long assocId)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // ID
        assoc.setId(assocId);
        
        return template.delete(DELETE_CHILD_ASSOC_BY_ID, assoc);
    }

    @Override
    protected int updateChildAssocIndex(
            Long parentNodeId,
            Long childNodeId,
            QName assocTypeQName,
            QName assocQName,
            int index)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Parent
        NodeEntity parentNode = new NodeEntity();
        parentNode.setId(parentNodeId);
        assoc.setParentNode(parentNode);
        // Child
        NodeEntity childNode = new NodeEntity();
        childNode.setId(childNodeId);
        assoc.setChildNode(childNode);
        // Type QName
        assoc.setTypeQNameAll(qnameDAO, assocTypeQName, true);
        // QName
        assoc.setQNameAll(qnameDAO, assocQName, true);
        // Index
        assoc.setAssocIndex(index);
        
        return template.update(UPDATE_CHILD_ASSOCS_INDEX, assoc);
    }

    @Override
    protected int updateChildAssocsUniqueName(Long childNodeId, String name)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Child
        NodeEntity childNode = new NodeEntity();
        childNode.setId(childNodeId);
        assoc.setChildNode(childNode);
        // Name
        assoc.setChildNodeNameAll(null, null, name);
        
        return template.update(UPDATE_CHILD_ASSOCS_UNIQUE_NAME, assoc);
    }

    @Override
    protected int deleteChildAssocsToAndFrom(Long nodeId)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Parent
        NodeEntity parentNode = new NodeEntity();
        parentNode.setId(nodeId);
        assoc.setParentNode(parentNode);
        // Child
        NodeEntity childNode = new NodeEntity();
        childNode.setId(nodeId);
        assoc.setChildNode(childNode);
        
        return template.delete(DELETE_CHILD_ASSOCS_TO_AND_FROM, assoc);
    }

    @Override
    protected ChildAssocEntity selectChildAssoc(Long assocId)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        assoc.setId(assocId);
        
        return (ChildAssocEntity) template.queryForObject(SELECT_CHILD_ASSOC_BY_ID, assoc);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<NodeIdAndAclId> selectPrimaryChildAcls(Long nodeId)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Parent
        NodeEntity parentNode = new NodeEntity();
        parentNode.setId(nodeId);
        assoc.setParentNode(parentNode);
        // Primary
        assoc.setPrimary(true);

        return template.queryForList(SELECT_NODE_PRIMARY_CHILD_ACLS, assoc);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<ChildAssocEntity> selectChildAssoc(
            Long parentNodeId,
            Long childNodeId,
            QName assocTypeQName,
            QName assocQName)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Parent
        NodeEntity parentNode = new NodeEntity();
        parentNode.setId(parentNodeId);
        assoc.setParentNode(parentNode);
        // Child
        NodeEntity childNode = new NodeEntity();
        childNode.setId(childNodeId);
        assoc.setChildNode(childNode);
        // Type QName
        if (!assoc.setTypeQNameAll(qnameDAO, assocTypeQName, false))
        {
            return Collections.emptyList();     // Shortcut
        }
        // QName
        if (!assoc.setQNameAll(qnameDAO, assocQName, false))
        {
            return Collections.emptyList();     // Shortcut
        }
        
        return template.queryForList(SELECT_CHILD_ASSOCS_OF_PARENT, assoc);
    }

    /**
     * Filter to allow the {@link ChildAssocRowHandler} to filter results.
     * 
     * @author Derek Hulley
     * @since 3.4
     */
    private interface ChildAssocRowHandlerFilter
    {
        boolean isResult(ChildAssocEntity assoc);
    }
    
    /**
     * Class that pushes results to a {@link ChildAssocRefQueryCallback}.
     * 
     * @author Derek Hulley
     * @since 3.4
     */
    private class ChildAssocRowHandler implements RowHandler
    {
        private final ChildAssocRowHandlerFilter filter;
        private final ChildAssocRefQueryCallback resultsCallback;
        private boolean more = true;
        
        private ChildAssocRowHandler(ChildAssocRefQueryCallback resultsCallback)
        {
            this(null, resultsCallback);
        }
        
        private ChildAssocRowHandler(ChildAssocRowHandlerFilter filter, ChildAssocRefQueryCallback resultsCallback)
        {
            this.filter = filter;
            this.resultsCallback = resultsCallback;
        }
        
        public void handleRow(Object valueObject)
        {
            // Do nothing if no further results are required
            // TODO: Use iBatis' new feature (when we upgrade) to kill the resultset walking
            if (!more)
            {
                return;
            }
            ChildAssocEntity assoc = (ChildAssocEntity) valueObject;
            if (filter != null && !filter.isResult(assoc))
            {
                // Filtered out
                return;
            }
            Pair<Long, ChildAssociationRef> childAssocPair = assoc.getPair(qnameDAO);
            Pair<Long, NodeRef> parentNodePair = assoc.getParentNode().getNodePair();
            Pair<Long, NodeRef> childNodePair = assoc.getChildNode().getNodePair();
            // Call back
            boolean more = resultsCallback.handle(childAssocPair, parentNodePair, childNodePair);
            if (!more)
            {
                this.more = false;
            }
        }
    }

    @Override
    protected void selectChildAssocs(
            Long parentNodeId,
            Long childNodeId,
            QName assocTypeQName,
            QName assocQName,
            Boolean isPrimary,
            Boolean sameStore,
            ChildAssocRefQueryCallback resultsCallback)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Parent
        NodeEntity parentNode = new NodeEntity();
        parentNode.setId(parentNodeId);
        assoc.setParentNode(parentNode);
        // Child
        if (childNodeId != null)
        {
            NodeEntity childNode = new NodeEntity();
            childNode.setId(childNodeId);
            assoc.setChildNode(childNode);
        }
        // Type QName
        if (assocTypeQName != null)
        {
            if (!assoc.setTypeQNameAll(qnameDAO, assocTypeQName, false))
            {
                return;                     // Shortcut
            }
        }
        // QName
        if (assocQName != null)
        {
            if (!assoc.setQNameAll(qnameDAO, assocQName, false))
            {
                return;                     // Shortcut
            }
        }
        // Primary
        if (isPrimary != null)
        {
            assoc.setPrimary(isPrimary);
        }
        // Same store
        if (sameStore != null)
        {
            assoc.setSameStore(sameStore);
        }
        
        ChildAssocRowHandler rowHandler = new ChildAssocRowHandler(resultsCallback);
        template.queryWithRowHandler(SELECT_CHILD_ASSOCS_OF_PARENT, assoc, rowHandler);
        resultsCallback.done();
    }

    @Override
    protected void selectChildAssocs(
            Long parentNodeId,
            Set<QName> assocTypeQNames,
            ChildAssocRefQueryCallback resultsCallback)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Parent
        NodeEntity parentNode = new NodeEntity();
        parentNode.setId(parentNodeId);
        assoc.setParentNode(parentNode);
        // Type QNames
        Set<Long> assocTypeQNameIds = qnameDAO.convertQNamesToIds(assocTypeQNames, false);
        if (assocTypeQNameIds.size() == 0)
        {
            return;                         // Shortcut as they don't exist
        }
        assoc.setTypeQNameIds(new ArrayList<Long>(assocTypeQNameIds));
        
        ChildAssocRowHandler rowHandler = new ChildAssocRowHandler(resultsCallback);
        template.queryWithRowHandler(SELECT_CHILD_ASSOCS_OF_PARENT, assoc, rowHandler);
        resultsCallback.done();
    }

    @Override
    protected ChildAssocEntity selectChildAssoc(Long parentNodeId, QName assocTypeQName, String childName)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Parent
        NodeEntity parentNode = new NodeEntity();
        parentNode.setId(parentNodeId);
        assoc.setParentNode(parentNode);
        // Type QName
        if (!assoc.setTypeQNameAll(qnameDAO, assocTypeQName, false))
        {
            return null;                    // Shortcut
        }
        // Child name
        assoc.setChildNodeNameAll(null, assocTypeQName, childName);

        // Note: This single results was assumed from inception of the original method.  It's correct.
        return (ChildAssocEntity) template.queryForObject(SELECT_CHILD_ASSOCS_OF_PARENT, assoc);
    }

    @Override
    protected void selectChildAssocs(
            Long parentNodeId,
            QName assocTypeQName,
            Collection<String> childNames,
            ChildAssocRefQueryCallback resultsCallback)
    {
        if (childNames.size() == 0)
        {
            return;
        }
        else if (childNames.size() > 1000)
        {
            throw new IllegalArgumentException("Unable to process more than 1000 child names in getChildAssocs");
        }
        // Work out the child names to query on
        final Set<String> childNamesShort = new HashSet<String>(childNames.size());
        final List<Long> childNamesCrc = new ArrayList<Long>(childNames.size());
        for (String childName : childNames)
        {
            String childNameLower = childName.toLowerCase();
            String childNameShort = ChildAssocEntity.getChildNodeNameShort(childNameLower);
            Long childNameCrc = ChildAssocEntity.getChildNodeNameCrc(childNameLower);
            childNamesShort.add(childNameShort);
            childNamesCrc.add(childNameCrc);
        }
        // Create a filter that checks that the name CRC is present
        ChildAssocRowHandlerFilter filter = new ChildAssocRowHandlerFilter()
        {
            public boolean isResult(ChildAssocEntity assoc)
            {
                return childNamesShort.contains(assoc.getChildNodeName());
            }
        };
        
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Parent
        NodeEntity parentNode = new NodeEntity();
        parentNode.setId(parentNodeId);
        assoc.setParentNode(parentNode);
        // Type QName
        if (assocTypeQName != null)
        {
            if (!assoc.setTypeQNameAll(qnameDAO, assocTypeQName, false))
            {
                return;                         // Shortcut
            }
        }
        // Child names
        assoc.setChildNodeNameCrcs(childNamesCrc);
        
        ChildAssocRowHandler rowHandler = new ChildAssocRowHandler(filter, resultsCallback);
        template.queryWithRowHandler(SELECT_CHILD_ASSOCS_OF_PARENT, assoc, rowHandler);
        resultsCallback.done();
    }

    @Override
    protected void selectChildAssocsByChildTypes(
            Long parentNodeId,
            Set<QName> childNodeTypeQNames,
            ChildAssocRefQueryCallback resultsCallback)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Parent
        NodeEntity parentNode = new NodeEntity();
        parentNode.setId(parentNodeId);
        assoc.setParentNode(parentNode);
        // Child Node Type QNames
        Set<Long> childNodeTypeQNameIds = qnameDAO.convertQNamesToIds(childNodeTypeQNames, false);
        if (childNodeTypeQNameIds.size() == 0)
        {
            return;                         // Shortcut as they don't exist
        }
        assoc.setChildNodeTypeQNameIds(new ArrayList<Long>(childNodeTypeQNameIds));
        
        ChildAssocRowHandler rowHandler = new ChildAssocRowHandler(resultsCallback);
        template.queryWithRowHandler(SELECT_CHILD_ASSOCS_OF_PARENT, assoc, rowHandler);
        resultsCallback.done();
    }
    
    @Override
    protected void selectChildAssocsWithoutParentAssocsOfType(
            Long parentNodeId,
            QName assocTypeQName,
            ChildAssocRefQueryCallback resultsCallback)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Parent
        NodeEntity parentNode = new NodeEntity();
        parentNode.setId(parentNodeId);
        assoc.setParentNode(parentNode);
        // Type QName
        if (!assoc.setTypeQNameAll(qnameDAO, assocTypeQName, false))
        {
            return;                         // Shortcut
        }

        ChildAssocRowHandler rowHandler = new ChildAssocRowHandler(resultsCallback);
        template.queryWithRowHandler(SELECT_CHILD_ASSOCS_OF_PARENT_WITHOUT_PARENT_ASSOCS_OF_TYPE, assoc, rowHandler);
        resultsCallback.done();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<ChildAssocEntity> selectPrimaryParentAssocs(Long childNodeId)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Child
        NodeEntity childNode = new NodeEntity();
        childNode.setId(childNodeId);
        assoc.setChildNode(childNode);
        // Primary
        assoc.setPrimary(Boolean.TRUE);
        
        return template.queryForList(SELECT_PARENT_ASSOCS_OF_CHILD, assoc);
    }

    @Override
    protected void selectParentAssocs(
            Long childNodeId,
            QName assocTypeQName,
            QName assocQName,
            Boolean isPrimary,
            ChildAssocRefQueryCallback resultsCallback)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Child
        NodeEntity childNode = new NodeEntity();
        childNode.setId(childNodeId);
        assoc.setChildNode(childNode);
        // Type QName
        if (assocTypeQName != null)
        {
            if (!assoc.setTypeQNameAll(qnameDAO, assocTypeQName, false))
            {
                return;                         // Shortcut
            }
        }
        // QName
        if (assocQName != null)
        {
            if (!assoc.setQNameAll(qnameDAO, assocQName, false))
            {
                return;                         // Shortcut
            }
        }
        // Primary
        if (isPrimary != null)
        {
            assoc.setPrimary(isPrimary);
        }
        
        ChildAssocRowHandler rowHandler = new ChildAssocRowHandler(resultsCallback);
        template.queryWithRowHandler(SELECT_PARENT_ASSOCS_OF_CHILD, assoc, rowHandler);
        resultsCallback.done();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<ChildAssocEntity> selectParentAssocs(Long childNodeId)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Child
        NodeEntity childNode = new NodeEntity();
        childNode.setId(childNodeId);
        assoc.setChildNode(childNode);
        
        return template.queryForList(SELECT_PARENT_ASSOCS_OF_CHILD, assoc);
    }

    @Override
    protected int updatePrimaryParentAssocs(
            Long childNodeId,
            Long parentNodeId,
            QName assocTypeQName,
            QName assocQName,
            String childNodeName)
    {
        ChildAssocEntity assoc = new ChildAssocEntity();
        // Parent
        NodeEntity parentNode = new NodeEntity();
        parentNode.setId(parentNodeId);
        assoc.setParentNode(parentNode);
        // Child
        NodeEntity childNode = new NodeEntity();
        childNode.setId(childNodeId);
        assoc.setChildNode(childNode);
        // Type QName
        if (assocTypeQName != null)
        {
            assoc.setTypeQNameAll(qnameDAO, assocTypeQName, true);
            // Have to recalculate the crc values for the association
            assoc.setChildNodeNameAll(dictionaryService, assocTypeQName, childNodeName);
        }
        // QName
        if (assocQName != null)
        {
            assoc.setQNameAll(qnameDAO, assocQName, true);
        }
        // Primary
        assoc.setPrimary(Boolean.TRUE);
        
        return template.update(UPDATE_PARENT_ASSOCS_OF_CHILD, assoc);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Transaction selectLastTxnBeforeCommitTime(Long maxCommitTime)
    {
        Assert.notNull(maxCommitTime, "maxCommitTime");
        
        TransactionQueryEntity query = new TransactionQueryEntity();
        query.setMaxCommitTime(maxCommitTime);
        
        List<Transaction> txns = template.queryForList(SELECT_TXN_LAST, query, 0, 1);
        if (txns.size() > 0)
        {
            return txns.get(0);
        }
        else
        {
            return null;
        }
    }

    @Override
    protected int selectTransactionCount()
    {
        return (Integer) template.queryForObject(SELECT_TXN_COUNT);
    }

    @Override
    protected Transaction selectTxnById(Long txnId)
    {
        TransactionQueryEntity query = new TransactionQueryEntity();
        query.setId(txnId);
        
        return (Transaction) template.queryForObject(SELECT_TXNS, query);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<NodeEntity> selectTxnChanges(Long txnId, Long storeId)
    {
        TransactionQueryEntity query = new TransactionQueryEntity();
        query.setId(txnId);
        if (storeId != null)
        {
            query.setStoreId(storeId);
        }
        
        // TODO: Return List<Node> for quicker node_deleted access
        return (List<NodeEntity>) template.queryForList(SELECT_TXN_NODES, query);
    }

    @Override
    protected int selectTxnNodeChangeCount(Long txnId, Boolean updates)
    {
        NodeEntity node = new NodeEntity();
        // Updates or deletes
        if (updates != null)
        {
            node.setDeleted(Boolean.valueOf(!updates));
        }
        // Transaction
        TransactionEntity transaction = new TransactionEntity();
        transaction.setId(txnId);
        node.setTransaction(transaction);

        return (Integer) template.queryForObject(SELECT_TXN_NODE_COUNT, node);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<Transaction> selectTxns(
            Long fromTimeInclusive,
            Long toTimeExclusive,
            Integer count,
            List<Long> includeTxnIds,
            List<Long> excludeTxnIds,
            Long excludeServerId,
            Boolean ascending)
    {
        TransactionQueryEntity query = new TransactionQueryEntity();
        query.setMinCommitTime(fromTimeInclusive);
        query.setMaxCommitTime(toTimeExclusive);
        query.setIncludeTxnIds(includeTxnIds);
        query.setExcludeTxnIds(excludeTxnIds);
        query.setExcludeServerId(excludeServerId);
        query.setAscending(ascending);
        
        if (count == null)
        {
            return template.queryForList(SELECT_TXNS, query);
        }
        else
        {
            return template.queryForList(SELECT_TXNS, query, 0, count);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<Long> selectTxnsUnused(Long minTxnId, Long maxCommitTime, Integer count)
    {
        TransactionQueryEntity query = new TransactionQueryEntity();
        query.setMinId(minTxnId);
        query.setMaxCommitTime(maxCommitTime);
        if (count == null)
        {
            return template.queryForList(SELECT_TXNS_UNUSED, query);
        }
        else
        {
            return template.queryForList(SELECT_TXNS_UNUSED, query, 0, count);
        }
    }

    @Override
    protected Long selectMinTxnCommitTime()
    {
        return (Long) template.queryForObject(SELECT_TXN_MIN_COMMIT_TIME);
    }

    @Override
    protected Long selectMaxTxnCommitTime()
    {
        return (Long) template.queryForObject(SELECT_TXN_MAX_COMMIT_TIME);
    }

    @Override
    protected void selectChildAssocsByPropertyValue(Long parentNodeId,
            QName propertyQName, 
            NodePropertyValue nodeValue,
            ChildAssocRefQueryCallback resultsCallback)
    {
        ChildPropertyEntity assocProp = new ChildPropertyEntity();
     
        // Parent
        assocProp.setParentNodeId(parentNodeId);
        
        // Property name
        Pair<Long,QName> propName = qnameDAO.getQName(propertyQName);
        
        if(propName != null)
        {
            // Property
            assocProp.setValue(nodeValue);
            assocProp.setPropertyQNameId(propName.getFirst());
        
            ChildAssocRowHandler rowHandler = new ChildAssocRowHandler(resultsCallback);
            template.queryWithRowHandler(SELECT_CHILD_ASSOCS_BY_PROPERTY_VALUE, assocProp, rowHandler);
            resultsCallback.done();
        }
    }
}