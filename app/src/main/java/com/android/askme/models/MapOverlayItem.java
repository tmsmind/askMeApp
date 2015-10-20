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
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;

import com.google.android.maps.OverlayItem;

import java.util.ArrayList;

/**
 * Created by dhanraj on 10/10/15.
 */
public class MapOverlayItem extends ItemizedOverlay<OverlayItem> {

    private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();

    private Context context;

    public MapOverlayItem(Drawable drawable) {
        super(drawable);
    }

    public MapOverlayItem(Drawable drawable, ArrayList<OverlayItem> mapOverlays, Context context) {
        super(drawable);
        this.mapOverlays = mapOverlays;
        this.context = context;
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mapOverlays.get(i);
    }

    @Override
    public int size() {
        return mapOverlays.size();
    }

    public void addOverlay(OverlayItem overlay) {
        mapOverlays.add(overlay);
    }

    public void populateNow() {
        this.populate();
    }
}
