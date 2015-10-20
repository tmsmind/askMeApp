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

package com.android.askme.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.askme.R;
import com.android.askme.models.Cars;
import com.android.askme.models.Event;
import com.android.askme.models.SavedCars;
import com.android.askme.utils.ui.ListItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by dhanraj on 17/10/15.
 */
public class NewCarActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    ListItem carMake,carModel;
    EditText carName;
    SeekBar carFuel;
    FloatingActionButton saveCar;

    Cars selectedCar;
    SavedCars newCar = new SavedCars();
    private ArrayList<Cars> carsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        carMake = (ListItem) findViewById(R.id.car_make);
        carModel = (ListItem) findViewById(R.id.car_model);
        carName = (EditText) findViewById(R.id.car_name);
        carFuel = (SeekBar) findViewById(R.id.fuel_bar);
        saveCar = (FloatingActionButton) findViewById(R.id.new_car_button);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("New Car");
        }
        readCarsData();

        carMake.setOnClickListener(this);
        carModel.setOnClickListener(this);
        saveCar.setOnClickListener(this);

        carFuel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e("Seekbar Value",""+i);
                newCar.setFuelStatus(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void readCarsData() {
        InputStream is = null;
        try {
            is = getAssets().open("cars.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String bufferString = new String(buffer);
            Type listType = new TypeToken<ArrayList<Cars>>() {
            }.getType();
            carsList = new Gson().fromJson(bufferString, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void prepareCarMake() {
        final List<CharSequence> carMakes = new ArrayList<>();
        for (Cars cars : carsList) {
            carMakes.add(cars.getTitle());
        }
        new MaterialDialog.Builder(this)
                .title("Manufacturer")
                .items(carMakes.toArray(new CharSequence[carMakes.size()]))
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        selectedCar = carsList.get(which);
                        carMake.setSubTitle(selectedCar.getTitle());
                        newCar.setCarMake(selectedCar.getTitle());
                        return true;
                    }
                })
                .show();

    }

    void prepareCarModel() {
        final List<CharSequence> carModels = new ArrayList<>();
        for (Cars.ModelsEntity modelsEntity : selectedCar.getModels()) {
            carModels.add(modelsEntity.getTitle());
        }
        new MaterialDialog.Builder(this)
                .title("Model")
                .items(carModels.toArray(new CharSequence[carModels.size()]))
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        carModel.setSubTitle(String.valueOf(carModels.get(which)));
                        newCar.setCarModel(String.valueOf(carModels.get(which)));

                        if(carName.getText().length()<1) carName.setText(newCar.getCarMake()+" "+newCar.getCarModel());

                        return true;
                    }
                })
                .show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.car_make:
                prepareCarMake();
                break;

            case R.id.car_model:
                prepareCarModel();
                break;

            case R.id.new_car_button:
                if(newCar.getCarMake()!=null) {
                    SavedCars.addCarToList(newCar, getApplicationContext());
                    EventBus.getDefault().postSticky(new Event.NewCarEvent(Event.EventType.NEW_CAR));
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Error! Car info cannot be empty",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
