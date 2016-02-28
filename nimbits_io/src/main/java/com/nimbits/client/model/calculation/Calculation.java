/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.model.calculation;

import com.nimbits.client.model.trigger.Trigger;

import java.io.Serializable;


public interface Calculation extends Trigger, Serializable {

    String getFormula();

    String getX();

    String getY();

    String getZ();

    void setZ(final String z);

    void setY(final String y);

    void setX(final String x);


    void setFormula(final String formula);

}
