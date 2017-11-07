package com.utclo23.data.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.utclo23.com.ComFacade;
import com.utclo23.data.configuration.Configuration;
import com.utclo23.data.facade.DataFacade;
import com.utclo23.data.structure.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * User mediator related to user features
 *
 * @author wuxiaoda
 */
public class UserMediator {

    /**
     * map that store users
     */
    private Map<String, LightPublicUser> mapConnectedUser;
    /**
     * owner who is the current user
     */
    private Owner owner;
    /**
     * reference to the data facade
     */
    private DataFacade dataFacade;

    /**
     * constructor
     *
     * @param dataFacade reference to the facade
     */
    public UserMediator(DataFacade dataFacade) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Création du mediator");

        this.dataFacade = dataFacade;
        this.mapConnectedUser = new HashMap<>();
    }

    /**
     * get connected user
     *
     * @return
     */
    public List<LightPublicUser> getConnectedUsers() {
        List<LightPublicUser> listConnectedUser = new ArrayList<>(this.mapConnectedUser.values());
        return listConnectedUser;
    }

    /**
     * get the local connected user
     *
     * @return
     */
    public Owner getMyOwnerProfile() {
        return owner;
    }

    /**
     * set the owner
     *
     * @param owner
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * get facade
     *
     * @return
     */
    public DataFacade getDataFacade() {
        return dataFacade;
    }

    /**
     * set facade
     */
    public void setDataFacade(DataFacade dataFacade) {
        this.dataFacade = dataFacade;
    }

    /**
     * extract bytes from a file
     *
     * @param imageName
     * @return
     * @throws DataException
     */
    private byte[] extractBytes(String imageName) throws DataException {
        try { // open image
            File imgPath = new File(imageName);
            BufferedImage bufferedImage = ImageIO.read(imgPath);

            // get DataBufferBytes from Raster
            WritableRaster raster = bufferedImage.getRaster();
            DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
            return (data.getData());
        } catch (Exception e) {
            throw new DataException("Data : error in image process");
        }
    }

    /**
     * return owner public profile
     *
     * @return public user
     */
    public PublicUser getMyPublicUserProfile() {
        PublicUser publicUser = null;
        if (this.owner != null) {
            publicUser = this.owner.getUserIdentity();
        }

        return publicUser;
    }

    /**
     * create a user
     *
     * @param playerName
     * @param password
     * @param firstName
     * @param lastName
     * @param birthDate
     * @param fileImage
     * @throws DataException
     */
    public void createUser(String playerName, String password, String firstName, String lastName, Date birthDate, String fileImage) throws DataException {

        //blank playername or password
        if (playerName.isEmpty() || password.isEmpty()) {
            throw new DataException("Data : error due to empty playername or password");
        }

        //determine the parth
        String path = Configuration.SAVE_DIR + File.separator + playerName + ".json";

        //all uppercase
        playerName = playerName.toUpperCase();
        password = password.toUpperCase();
        firstName = firstName.toUpperCase();
        lastName = lastName.toUpperCase();

        File userFile = new File(path);

        //check the existence of the file
        if (userFile.exists()) {
            throw new DataException("account already exists"); // throw related error
        } else {

            //create user 
            String id = new UID().toString();
            LightPublicUser lightPublicUser = new LightPublicUser(id, playerName);
            //TODO thumbnail
            PublicUser publicUser = new PublicUser(lightPublicUser, lastName, firstName, birthDate);

            //for unit test
            if (!this.dataFacade.isTestMode()) {
                publicUser.setAvatar(this.extractBytes(fileImage));
            }

            this.owner = new Owner();
            owner.setUserIdentity(publicUser);
            owner.setPassword(password);

            //save user in json file
            save();

        }
    }

    /**
     * Update user
     *
     * @param password
     * @param firstName
     * @param lastName
     * @param birthDate
     * @param fileImage
     * @throws DataException
     */
    public void updateUser(String password, String firstName, String lastName, Date birthDate, String fileImage) throws DataException {

        if (this.owner != null) {

            password = password.toUpperCase();
            firstName = firstName.toUpperCase();
            lastName = lastName.toUpperCase();

            this.owner.setPassword(password);
            this.owner.getUserIdentity().setAvatar(this.extractBytes(fileImage));
            //TODO update thumbnail
            this.owner.getUserIdentity().setBirthDate(birthDate);
            this.owner.getUserIdentity().setFirstName(firstName);
            this.owner.getUserIdentity().setLastName(lastName);

            save();

            //remove old profile and add new one
            //to check by data module
            ComFacade comFacade = this.dataFacade.getComfacade();
            if (comFacade != null) {
                if (this.owner != null) {
                    comFacade.notifyUserSignedOut(this.owner.getUserIdentity());
                    comFacade.notifyUserSignedIn(this.owner.getUserIdentity());
                }
            }

        } else {
            throw new DataException("Data : error in updating");
        }

    }

    /**
     * get user profile
     *
     * @param id
     * @return
     */
    public LightPublicUser getLightPublicUser(String id) {
        LightPublicUser lightPublicUser = null;

        if (this.mapConnectedUser.containsKey(id)) {
            lightPublicUser = this.mapConnectedUser.get(id);
        }

        return lightPublicUser;
    }

    /**
     * connection
     *
     * @param username
     * @param password
     * @throws Exception
     */
    public void signIn(String username, String password) throws DataException {

        //uppercase
        username = username.toUpperCase();
        password = password.toUpperCase();

        //already connected
        if (this.owner != null) {
            throw new DataException("Data : already connected"); //throw related error
        }

        //determine path
        String path = Configuration.SAVE_DIR + File.separator + username + ".json";
        File userFile = new File(path);
        //check the existence
        if (!userFile.exists()) {
            throw new DataException("Data : error in save user file"); //throw related error
        }

        ObjectMapper mapper = new ObjectMapper();

        //retrieval
        Owner user = null;
        try {
            user = mapper.readValue(userFile, Owner.class);
        } catch (Exception e) {
            throw new DataException("Data : error in reading file");
        }

        //password matching 
        if (user != null && !user.getPassword().equals(password)) {
            throw new DataException("Data : error in password");
        } else {

            this.owner = user;

            //notification
            ComFacade comFacade = this.dataFacade.getComfacade();
            if (comFacade != null) {
                if (this.owner != null) {
                    comFacade.notifyUserSignedIn(this.owner.getUserIdentity());
                }
            }

        }
    }

    /**
     * disconnection
     */
    public void singOut() throws DataException {
        if (this.owner != null) {

            this.save(); //Save the file

            //notification
            ComFacade comFacade = this.dataFacade.getComfacade();
            if (comFacade != null) {
                if (this.owner != null) {
                    comFacade.notifyUserSignedOut(this.owner.getUserIdentity());
                }
            }

            //owner destroyed
            this.owner = null;

        } else {
            throw new DataException("Data : no connected user");
        }

    }

    /**
     * save
     */
    private void save() throws DataException {

        try {
            //determine the path
            String path = Configuration.SAVE_DIR + File.separator + owner.getUserIdentity().getPlayerName() + ".json";

            //save into a json file
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File(path), owner);
        } catch (Exception e) {
            throw new DataException("Data : error in save process"); //throw related error by using exception of DataModule
        }

    }

    /**
     * add user
     *
     * @param usr
     * @return
     */
    public boolean addConnectedUser(LightPublicUser usr) {

        boolean add = false;
        if (!this.mapConnectedUser.containsKey(usr.getId())) {
            add = true;
            this.mapConnectedUser.put(usr.getId(), usr);
        }

        return add;
    }

    /**
     * remove connected user
     *
     * @param usr
     * @return
     */
    public boolean removeConnectedUser(LightPublicUser usr) {
        boolean remove = false;
        if (this.mapConnectedUser.containsKey(usr.getId())) {
            remove = true;
            this.mapConnectedUser.remove(usr.getId());
        }
        return remove;
    }
}
