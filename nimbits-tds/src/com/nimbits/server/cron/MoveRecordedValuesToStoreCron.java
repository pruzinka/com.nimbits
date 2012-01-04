package com.nimbits.server.cron;

import com.nimbits.client.model.*;
import com.nimbits.client.model.point.*;
import com.nimbits.server.point.*;
import com.nimbits.server.task.*;
import com.nimbits.shared.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/20/11
 * Time: 5:24 PM
 */
public class MoveRecordedValuesToStoreCron extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
   // MemcacheService systemCache;

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        PrintWriter out = resp.getWriter();
        String reloadParam = req.getParameter(Const.PARAM_RELOAD);
        boolean reload = (!Utils.isEmptyString(reloadParam));

       // try {

           // systemCache = MemcacheServiceFactory.getMemcacheService(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_SYSTEM + Const.CONST_SERVER_VERSION);
            List<Long> activityLog;

//            if (!reload && systemCache.contains(Const.PARAM_POINTS)) {
//                activityLog = (List<Long>) systemCache.get(Const.PARAM_POINTS);
//                for (final long l : activityLog) {
//
//                    Point point = PointTransactionsFactory.getDaoInstance(null).getPointByID(l);
//                    TaskFactoryLocator.getInstance().startMoveCachedValuesToStoreTask(point);
//
//
//                }
//                out.print("<h4> Total Points (using activity log): " + activityLog.size() + "</h4>");
//            } else {

                int set = 0;
                int results = -1;

                int count = 0;
                while (results != 0) {
                    final List<Point> points = PointServiceFactory.getInstance().getAllPoints(set, set + Const.CONST_QUERY_CHUNK_SIZE);
                    count++;
                    results = points.size();
                    set += Const.CONST_QUERY_CHUNK_SIZE;
                    for (final Point point : points) {
                        out.println("<p>" + point.getName() + "</p>");
                        TaskFactoryLocator.getInstance().startMoveCachedValuesToStoreTask(point);
                    }
                }
                out.print("<h4> Total Points (using datastore): " + count + "</h4>");
           // }
//        } catch (NimbitsException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

    }
}
