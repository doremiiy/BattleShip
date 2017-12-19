/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utclo23.ihmmain.controller;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
/**
 * Controller of the network interface selection.
 * @author Louis
 */
public class NetworkInterfaceChoiceController extends AbstractController {
    
    @FXML
    ListView listNetworks;
    
    @FXML
    private void exit(ActionEvent event){
        System.exit(0);
    }
        
    @Override
    public void start(){
        lookForInterface();
        listNetworks.getSelectionModel().select(0);
    }
    
    private void lookForInterface(){
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (isValid(iface)){
                    listNetworks.getItems().add(iface.getName());
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
    
    @FXML 
    private void handleButtonValidate(ActionEvent event) throws Exception{
        Inet4Address usedIf;
        String networkName = (String)listNetworks.getSelectionModel().getSelectedItem();
        NetworkInterface chosenIf = NetworkInterface.getNetworkInterfaces().nextElement();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.getName().equals(networkName)){
                    chosenIf = iface;
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        
        
        for(InterfaceAddress ifAddr : chosenIf.getInterfaceAddresses()){
            try{
                usedIf = (Inet4Address) ifAddr.getAddress();
                getFacade().iDataIHMMain.setNetworkInterface(ifAddr);
                Logger.getLogger(NetworkInterfaceChoiceController.class.getName()).log(Level.INFO, usedIf.getHostAddress());
                getIhmmain().toLogin();
            } catch (Exception e){}
        }   
    }
    
    private boolean isValid(NetworkInterface networkInt){
        Inet4Address usedIf;
        Boolean isValid = false;
        for(InterfaceAddress ifAddr : networkInt.getInterfaceAddresses()){
            try{
                usedIf = (Inet4Address) ifAddr.getAddress();
                getFacade().iDataIHMMain.setNetworkInterface(ifAddr);
                Logger.getLogger(NetworkInterfaceChoiceController.class.getName()).log(Level.INFO, usedIf.getHostAddress());
                isValid = true;
            } catch (Exception e){}
        }
        return isValid;
    } 
}
