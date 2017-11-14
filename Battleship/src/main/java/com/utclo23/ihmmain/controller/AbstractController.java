/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utclo23.ihmmain.controller;

import com.utclo23.ihmmain.IHMMain;
import com.utclo23.ihmmain.facade.IHMMainFacade;

/**
 * upper class of all ihm-main controller class, contain IHMMain class
 * @author Linxuhao
 */
public class AbstractController {
    /**
     * the reference of ihmmain, to jump between scenes
     */
    public IHMMain ihmmain;
    
    public IHMMainFacade facade;

    public IHMMainFacade getFacade() {
        return facade;
    }

    public void setFacade(IHMMainFacade facade) {
        this.facade = facade;
    }

    public IHMMain getIhmmain() {
        return ihmmain;
    }

    public void setIhmmain(IHMMain ihmmain) {
        this.ihmmain = ihmmain;
    }
     
}
