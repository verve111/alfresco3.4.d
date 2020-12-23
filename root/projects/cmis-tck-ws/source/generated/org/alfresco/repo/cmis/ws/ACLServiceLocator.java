/**
 * ACLServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class ACLServiceLocator extends org.apache.axis.client.Service implements org.alfresco.repo.cmis.ws.ACLService {

    public ACLServiceLocator() {
    }


    public ACLServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ACLServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ACLServicePort
    private java.lang.String ACLServicePort_address = "http://cmis/services/ACLService.ACLServicePort";

    public java.lang.String getACLServicePortAddress() {
        return ACLServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ACLServicePortWSDDServiceName = "ACLServicePort";

    public java.lang.String getACLServicePortWSDDServiceName() {
        return ACLServicePortWSDDServiceName;
    }

    public void setACLServicePortWSDDServiceName(java.lang.String name) {
        ACLServicePortWSDDServiceName = name;
    }

    public org.alfresco.repo.cmis.ws.ACLServicePort getACLServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ACLServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getACLServicePort(endpoint);
    }

    public org.alfresco.repo.cmis.ws.ACLServicePort getACLServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.alfresco.repo.cmis.ws.ACLServicePortBindingStub _stub = new org.alfresco.repo.cmis.ws.ACLServicePortBindingStub(portAddress, this);
            _stub.setPortName(getACLServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setACLServicePortEndpointAddress(java.lang.String address) {
        ACLServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.alfresco.repo.cmis.ws.ACLServicePort.class.isAssignableFrom(serviceEndpointInterface)) {
                org.alfresco.repo.cmis.ws.ACLServicePortBindingStub _stub = new org.alfresco.repo.cmis.ws.ACLServicePortBindingStub(new java.net.URL(ACLServicePort_address), this);
                _stub.setPortName(getACLServicePortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ACLServicePort".equals(inputPortName)) {
            return getACLServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/ws/200908/", "ACLService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/ws/200908/", "ACLServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ACLServicePort".equals(portName)) {
            setACLServicePortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
