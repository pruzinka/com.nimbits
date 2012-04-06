package com.nimbits.client.model.entity;

import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.point.*;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 11:06 AM
 */
public class EntityModel  implements Serializable, Entity {


    private String name;
    private String description;
    private int entityType;
    private int protectionLevel;
    private int alertType;
    private String entity;
    private String parent;
    private String owner;
    private boolean readOnly = false;
    private String blobKey;
    private List<Entity> points;

    public EntityModel(final CommonIdentifier name,
                       final String description,
                       final EntityType entityType,
                       final ProtectionLevel protectionLevel,
                       final String parent,
                       final String owner,
                       final String blobKey) {
        this.name = name.getValue();
        this.description = description;
        this.entityType = entityType.getCode();
        this.parent = parent;
        this.owner = owner;
        this.protectionLevel = protectionLevel.getCode();
        this.alertType = AlertType.OK.getCode();
        this.blobKey = blobKey;

    }
    public EntityModel() {
    }
    public EntityModel(final Entity entity) throws NimbitsException {
        this.name = entity.getName().getValue();
        this.description = entity.getDescription();
        this.entityType = entity.getEntityType().getCode();
        this.entity =entity.getKey();
        this.parent = entity.getParent();
        this.owner = entity.getOwner();
        this.protectionLevel = entity.getProtectionLevel().getCode();
        this.alertType = entity.getAlertType().getCode();
        this.blobKey = entity.getBlobKey();

    }

    public List<Entity> getPoints() {
        return points;
    }

    @Override
    public void setPoints(List<Entity> points) {
        this.points = points;
    }



    @Override
    public EntityName getName() throws NimbitsException {
        return CommonFactoryLocator.getInstance().createName(name, EntityType.get(entityType));
    }

    @Override
    public void setName(EntityName name) {
        this.name = name.getValue();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.get(entityType);
    }

    @Override
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType.getCode();
    }

    @Override
    public String getKey() {
        return (this.entity);
    }

    @Override
    public String getParent() {
        return (parent);
    }

    @Override
    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public ProtectionLevel getProtectionLevel() {
        return ProtectionLevel.get(protectionLevel);
    }

    @Override
    public void setProtectionLevel(ProtectionLevel protectionLevel) {
        this.protectionLevel = protectionLevel.getCode();
    }

    @Override
    public String getOwner() {
        return (owner);
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public AlertType getAlertType() {
        return AlertType.get(this.alertType);
    }

    @Override
    public void setAlertType(AlertType alertType) {
       this.alertType = alertType.getCode();
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }


    @Override
    public String getBlobKey() {
        return blobKey;
    }
    @Override
    public void setBlobKey(String blobKey) {
        this.blobKey = blobKey;
    }




}
