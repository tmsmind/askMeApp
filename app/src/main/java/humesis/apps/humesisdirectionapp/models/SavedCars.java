package humesis.apps.humesisdirectionapp.models;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import humesis.apps.humesisdirectionapp.preferences.SettingsUtil;

/**
 * Created by dhanraj on 17/10/15.
 */
public class SavedCars {

    String carName;
    String carMake;
    String carModel;
    int fuelStatus;
    int fuelEfficiency;


    public SavedCars() {
    }

    public SavedCars(String carName, String carMake, String carModel, int fuelStatus, int fuelEfficiency) {
        this.carName = carName;
        this.carMake = carMake;
        this.carModel = carModel;
        this.fuelStatus = fuelStatus;
        this.fuelEfficiency = fuelEfficiency;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarMake() {
        return carMake;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public int getFuelStatus() {
        return fuelStatus;
    }

    public void setFuelStatus(int fuelStatus) {
        this.fuelStatus = fuelStatus;
    }

    public int getFuelEfficiency() {
        return fuelEfficiency;
    }

    public void setFuelEfficiency(int fuelEfficiency) {
        this.fuelEfficiency = fuelEfficiency;
    }

    /**
     * Save cars to preferences
     * @param cars the car to save
     * @param context context
     */
    public static void addCarToList(SavedCars cars,Context context){
        if(cars.carMake!=null) {
            ArrayList<SavedCars> savedCars = getCarsList(context);
            savedCars.add(cars);
            SettingsUtil.set(context, "SavedCars", new Gson().toJson(savedCars));
        }
    }

    /**
     * Get saved cars from preference
     * @param context context
     * @return the list of cars
     */
    public static ArrayList<SavedCars> getCarsList(Context context){
        Type listType = new TypeToken<ArrayList<SavedCars>>() {
        }.getType();
        String json = SettingsUtil.get(context,"SavedCars","");
        return json.length()>0? new Gson().<ArrayList<SavedCars>>fromJson(json, listType) :new ArrayList<SavedCars>();
    }
}
