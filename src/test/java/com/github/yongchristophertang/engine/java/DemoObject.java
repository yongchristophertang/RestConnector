/*
 *  Copyright 2014-2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.yongchristophertang.engine.java;

/**
 * Created by YongTang on 2016/4/12.
 *
 * @author Yong Tang
 * @since 0.5
 */
public class DemoObject {
    private int integer;
    private String str;
    private double dbl;

    public DemoObject(){}

    public DemoObject(int integer, String str, double dbl) {
        this.integer = integer;
        this.str = str;
        this.dbl = dbl;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public double getDbl() {
        return dbl;
    }

    public void setDbl(double dbl) {
        this.dbl = dbl;
    }

//    @Override
//    public String toString() {
//        return "DemoObject{" +
//            "integer=" + integer +
//            ", str='" + str + '\'' +
//            ", dbl=" + dbl +
//            '}';
//    }
}
