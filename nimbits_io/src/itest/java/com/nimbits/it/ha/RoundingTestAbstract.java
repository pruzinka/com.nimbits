package com.nimbits.it.ha;

import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 *fix: doubleValue is rounded to integer.
 Short description: database stores double values without signs after dot.

 Scenario:
 Create datapoint on one of the instances.
 Record some daouble values into the datapoint: 0.1; 0.2; 0.3; 0.4 and so on.
 Read all values (for example Rest '/series' or from database: nimbits.valuestore).
 [Expected]: Values should be stored as double.
 [Actual]: Values are rounded to integer.
 */
public class RoundingTestAbstract extends AbstractNimbitsTest {

    @Test
    public void testScenario() {

        User me = nimbits.getMe();
        Point p = nimbits.addPoint(me, new PointModel.Builder().create());
        log(p);

        String pname = p.getName().getValue();
        double[] sample = {1.1, 1.2, 1.3, 1.4, 1.5, 0.0001, 1122.0, -123.2345, 222.001};

        for (int i = 0; i < sample.length; i++) {
             Value vx =  nimbits.recordValueSync(pname, new Value.Builder()
                    .doubleValue(sample[i])
                    .meta(String.valueOf(i))
                    .create());
             log(vx);


        }

sleep(3);


        List<Value> valueList = nimbits.getValues(p, 10);
        Collections.reverse(valueList);

        assertFalse(valueList.isEmpty());
        for (int i = 0; i < sample.length; i++) {
             assertEquals(sample[i], valueList.get(i).getDoubleValue(), 0.00001);

        }

    }
}
