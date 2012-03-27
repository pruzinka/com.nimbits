/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.dao.datapoint;

import com.nimbits.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.datapoints.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.task.*;

import javax.jdo.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.logging.*;

public class DataPointDAOImpl implements PointTransactions {
    private final Logger log = Logger.getLogger(DataPointDAOImpl.class.getName());
    private final User u;

    public DataPointDAOImpl(final User u) {
        this.u = u;
    }

    /* (non-Javadoc)
    * @see com.nimbits.client.service.datapoints.PointTransactions#getPoints(com.nimbits.client.model.user.NimbitsUser)
    */
    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public List<Point> getPoints() {

        List<Point> retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(DataPoint.class, "userFK == k");
            q.declareParameters("Long k");
            final long userFK = u.getId();
            final List<Point> points = (List<Point>) q.execute(userFK);
            retObj = PointModelFactory.createPointModels(points);
        } finally {
            pm.close();
        }

        return retObj;
    }


    @Override
    public Point getPointByID(final long id) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        Point retObj = null;
        try {
            final DataPoint p = pm.getObjectById(DataPoint.class, id);
            if (p != null) {
                retObj = PointModelFactory.createPointModel(p);
            }

        } catch (JDOObjectNotFoundException ex) {
            log.info("Point not found");

        } finally {
            pm.close();
        }

        return retObj;
    }


    @Override
    public Point updatePoint(final Point update) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        Point retObj = null;
        try {
            DataPoint original = pm.getObjectById(DataPoint.class, update.getId());

            if (original != null) {
                Transaction tx = pm.currentTransaction();
                tx.begin();
                original.setHighAlarm(update.getHighAlarm());
                original.setLowAlarm(update.getLowAlarm());
                original.setLowAlarmOn(update.isLowAlarmOn());
                original.setHighAlarmOn(update.isHighAlarmOn());
                original.setCompression(update.getCompression());
                original.setUnit(update.getUnit());
                original.setExpire(update.getExpire());
                original.setTag(update.getTag());
                original.setUserFK(update.getUserFK());
                original.setLastChecked(update.getLastChecked());
                original.setTargetValue(update.getTargetValue());
                original.setIdleAlarmOn(update.isIdleAlarmOn());
                original.setIdleAlarmSent(update.getIdleAlarmSent());
                original.setIdleSeconds(update.getIdleSeconds());

                tx.commit();
                retObj = PointModelFactory.createPointModel(original);

            }
            return retObj;
        }

        finally {
            pm.close();

        }



    }

    /* (non-Javadoc)
      * @see com.nimbits.client.service.datapoints.PointTransactions#getPointByUUID(java.lang.String)
      */
    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public Point getPointByUUID(final String uuid) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        Point retObj;

        List<DataPoint> points;
        try {
            Query q = pm.newQuery(DataPoint.class, "uuid == k");
            q.declareParameters("String k");
            q.setRange(0, 1);
            points = (List<DataPoint>) q.execute(uuid);

            if (points.size() > 0) {
                DataPoint result = points.get(0);
                retObj = PointModelFactory.createPointModel(result);
            } else {
                retObj = null;
            }
        } finally {
            pm.close();
        }

        return retObj;
    }

    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public List<Point> getAllPoints() {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(DataPoint.class);
            List<Point> result = (List<Point>) q.execute();
            return PointModelFactory.createPointModels(result);

        } finally {
            pm.close();
        }
    }

    @Override
    public Point addPoint(Entity entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {

            final DataPoint jdoPoint = new DataPoint(u, entity);
            jdoPoint.setCompression(Const.DEFAULT_POINT_COMPRESSION);
            jdoPoint.setExpire(Const.DEFAULT_DATA_EXPIRE_DAYS);
            jdoPoint.setLastChecked(new Date());
            jdoPoint.setCreateDate(new Date());
            pm.makePersistent(jdoPoint);

            return PointModelFactory.createPointModel(jdoPoint);
        } finally {
            pm.close();
        }
    }

    @Override
    public Point addPoint(Entity entity, Point point) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final DataPoint jdoPoint = new DataPoint(u, entity);

            pm.makePersistent(jdoPoint);

            return PointModelFactory.createPointModel(jdoPoint);
        } finally {
            pm.close();
        }
    }

    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public List<Point> getPoints(List<Entity> entities) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<String> ids = new ArrayList<String>();

        for (Entity e : entities) {
            if (e.getEntityType().equals(EntityType.point)) {
                ids.add(e.getEntity());
            }
        }


        final Query q1 = pm.newQuery(DataPoint.class, ":p.contains(uuid)");

        try {
            if (ids.size() > 0) {
                final List<Point> result = (List<Point>) q1.execute(ids);
                return PointModelFactory.createPointModels(result);
            }
            else {
                return new ArrayList<Point>();
            }
        } finally {
            pm.close();
        }
    }


    /* (non-Javadoc)
    * @see com.nimbits.client.service.datapoints.PointTransactions#deletePoint(com.nimbits.client.model.DataPoint)
    */
    @Override
    public void deletePoint(final Point p) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q = pm.newQuery(DataPoint.class);
            q.setFilter("id==k");
            q.declareParameters("long k");
            q.deletePersistentAll(p.getId());
            TaskFactoryLocator.getInstance().startDeleteDataTask(p, false, 0);
        } finally {
            pm.close();
        }
    }




    @Override
    public Point checkPoint(final HttpServletRequest req, final EmailAddress email, final Point point) throws NimbitsException {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Transaction tx = pm.currentTransaction();
        Point retObj;

        try {
            tx.begin();

            final DataPoint p = pm.getObjectById(DataPoint.class, point.getId());
            p.setLastChecked(new Date());
            if (p.getUUID() == null) {
                p.setUuid(UUID.randomUUID().toString());
            }
            if (p.getCreateDate() == null) {
                p.setCreateDate(new Date());

            }

            if (p.getExpire() > 0) {
                TaskFactoryLocator.getInstance().startDeleteDataTask(
                        p,
                        true, p.getExpire());
            }
            retObj = PointModelFactory.createPointModel(p);

            tx.commit();
            return retObj;
        }catch (Exception ex) {
            log.severe(ex.getMessage());
            throw new NimbitsException(ex.getMessage());
        } finally {
            pm.close();
        }

    }


    /* (non-Javadoc)
    * @see com.nimbits.client.service.datapoints.PointTransactions#getAllPoints()
    */
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public List<Point> getAllPoints(int start, int end) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<Point> retObj;
        try {

            final Query q = pm.newQuery(DataPoint.class);
            q.declareImports("import java.util.Date");
            q.setOrdering("LastChecked ascending");
            q.setRange(start, end);
            final List<Point> points = (List<Point>) q.execute();

            retObj = PointModelFactory.createPointModels(points);
            return retObj;
        } finally {
            pm.close();
        }


    }


    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public List<Point> getIdlePoints() {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<Point> retObj;
        try {
            List<Point> points;

            Query q = pm
                    .newQuery(DataPoint.class, "idleAlarmOn == k && idleAlarmSent  == c");
            q.declareParameters("Long k, Long c");

            points = (List<Point>) q.execute(true, false);
            retObj = PointModelFactory.createPointModels(points);
        } finally {
            pm.close();
        }


        return retObj;

    }




}


