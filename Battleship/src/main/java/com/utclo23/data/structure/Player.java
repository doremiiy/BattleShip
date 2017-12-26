/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utclo23.data.structure;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Davy
 */
public class Player extends SerializableEntity{
    private List<Ship> ships;
    private List<Mine> mines;
    private LightPublicUser lightPublicUser;
    private boolean computer;

    /**
     *
     * @param lightPublicUser
     */
    public Player(LightPublicUser lightPublicUser) {
        super();
        this.lightPublicUser = lightPublicUser;

        this.computer = false;

        this.mines = new ArrayList<>();
        this.ships = new ArrayList<>();

    }

    /**
     *
     */
    public Player() {
    }
    
    /**
     *
     * @param lightPublicUser
     */
    public void setLightPublicUser(LightPublicUser lightPublicUser) {
        this.lightPublicUser = lightPublicUser;
    }

    /**
     *
     * @return
     */
    public List<Ship> getShips() {
        return ships;
    }

    /**
     *
     * @param ships
     */
    public void setShips(List<Ship> ships) {
  
        this.ships = ships;
    }

    /**
     *
     * @return
     */
    public List<Mine> getMines() {
        return mines;
    }

    /**
     *
     * @param mines
     */
    public void setMines(List<Mine> mines) {
        this.mines = mines;
    }

    /**
     *
     * @return
     */
    public LightPublicUser getLightPublicUser() {
        return lightPublicUser;
    }

    /**
     *
     * @return
     */
    public boolean isComputer() {
        return computer;
    }

    /**
     *
     * @param computer
     */
    public void setComputer(boolean computer) {
        this.computer = computer;
    }
    
    
}
