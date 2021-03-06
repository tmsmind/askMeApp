/*
 * ==================================================================
 *   Copyright (c) 2015 Anand Sharma (Humesis, Inc.)
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

package com.android.askme.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.askme.R;
import com.android.askme.activity.NewCarActivity;
import com.android.askme.adapters.CarsAdapter;
import com.android.askme.models.Event;
import com.android.askme.models.SavedCars;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Anand Sharmaon 09/10/15.
 */
public class CarsFragment extends Fragment {

    ListView carsListView;
    FloatingActionButton newCarButton;
    TextView emptyView;

    ArrayList<SavedCars> carsList = new ArrayList<>();
    CarsAdapter carsAdapter;

    public CarsFragment() {
    }

    public static CarsFragment newInstance(){
        return new CarsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car,
                container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        carsListView = (ListView) view.findViewById(R.id.cars_list);
        newCarButton = (FloatingActionButton) view.findViewById(R.id.new_car_button);
        emptyView = (TextView) view.findViewById(R.id.empty_view);

        carsAdapter = new CarsAdapter(getContext());
        carsListView.setAdapter(carsAdapter);
        carsListView.setEmptyView(emptyView);

        carsList = SavedCars.getCarsList(getContext());
        carsAdapter.updateList(carsList);

        newCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewCarActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(Event.NewCarEvent carEvent){
        switch (carEvent.getEventType()){
            case NEW_CAR:
                carsList = SavedCars.getCarsList(getContext());
                carsAdapter.updateList(carsList);
                break;
        }
    }
}
