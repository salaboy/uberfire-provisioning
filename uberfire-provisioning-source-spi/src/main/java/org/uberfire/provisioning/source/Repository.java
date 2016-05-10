/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uberfire.provisioning.source;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author salaboy
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_OBJECT)
public interface Repository {

    public String getId();
    
    public String getURI();

    public String getName();

    public String getType();

    public boolean isBare();    

    public void setURI(String URI);

    public void setName(String name);

    public void setType(String type);

    public void setBare(boolean bare);

}