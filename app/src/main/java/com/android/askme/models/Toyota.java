/*
 * ==================================================================
 *   Copyright (c) 2015 Dhanraj Padmashali (Humesis, Inc.)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 *  ==================================================================
 */

package com.android.askme.models;

import android.content.Context;

import com.android.askme.preferences.SettingsUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by dhanraj on 19/10/15.
 */
public class Toyota {


    /**
     * Model : Scion
     * Year : 2010
     * VIN : 4T1BF1FK0EU391443
     * Engine : 2.5
     * Last Service : 16/10/14
     * Next Service Due : 16/10/15
     * Last Tire Change : 20/05/10
     * Tire Life(Miles) : 50
     * Last Oil Change : 16/10/14
     */

    public String Model;
    public int Year;
    public String VIN;
    public double Engine;
    @SerializedName("Last Service")
    public String LastService;
    @SerializedName("Next Service Due")
    public String NextServiceDue;
    @SerializedName("Last Tire Change")
    public String LastTireChange;
    @SerializedName("Tire Life(Miles)")
    public int TireLife;
    @SerializedName("Last Oil Change")
    public String LastOilChange;

    public Toyota() {
    }



    /**
     * Save cars to preferences
     *
     * @param cars    the car to save
     * @param context context
     */
    public static void addCarToList(Toyota cars, Context context) {
        if (cars != null) {
            ArrayList<Toyota> savedCars = getCarsList(context);
            savedCars.add(cars);
            SettingsUtil.set(context, "SavedCars", new Gson().toJson(savedCars));
        }
    }

    /**
     * Get saved cars from preference
     *
     * @param context context
     * @return the list of cars
     */
    public static ArrayList<Toyota> getCarsList(Context context) {
        Type listType = new TypeToken<ArrayList<Toyota>>() {
        }.getType();
        String json = SettingsUtil.get(context, "SavedCars", "");
        return json.length() > 0 ? new Gson().<ArrayList<Toyota>>fromJson(json, listType) : new ArrayList<Toyota>();
    }
}
