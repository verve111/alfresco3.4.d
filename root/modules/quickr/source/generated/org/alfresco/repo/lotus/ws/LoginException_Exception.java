
package org.alfresco.repo.lotus.ws;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.2.2
 * Tue Feb 16 19:13:38 EET 2010
 * Generated source version: 2.2.2
 * 
 */

@WebFault(name = "LoginException", targetNamespace = "http://exception.webservices.clb.content.ibm.com")
public class LoginException_Exception extends Exception {
    public static final long serialVersionUID = 20100216191338L;
    
    private org.alfresco.repo.lotus.ws.LoginException loginException;

    public LoginException_Exception() {
        super();
    }
    
    public LoginException_Exception(String message) {
        super(message);
    }
    
    public LoginException_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginException_Exception(String message, org.alfresco.repo.lotus.ws.LoginException loginException) {
        super(message);
        this.loginException = loginException;
    }

    public LoginException_Exception(String message, org.alfresco.repo.lotus.ws.LoginException loginException, Throwable cause) {
        super(message, cause);
        this.loginException = loginException;
    }

    public org.alfresco.repo.lotus.ws.LoginException getFaultInfo() {
        return this.loginException;
    }
}
