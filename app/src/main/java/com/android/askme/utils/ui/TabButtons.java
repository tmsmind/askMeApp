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

package com.android.askme.utils.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.android.askme.R;


/**
 * Created by dhanraj on 09/10/15.
 */
public class TabButtons extends LinearLayout {

    @DrawableRes int buttonIcon;
    boolean indicatorEnabled;

    public TabButtons(Context context) {
        super(context);
    }

    public TabButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TabButton, 0, 0);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tab_button, this, true);

        buttonIcon = a.getResourceId(R.styleable.TabButton_tab_button_drawable, R.drawable.ic_person);
        indicatorEnabled = a.getBoolean(R.styleable.TabButton_tab_indicator_enabled,false);

        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ImageButton button = (ImageButton) this.findViewById(R.id.tab_button);

        button.setBackgroundResource(buttonIcon);
        setEnabled(indicatorEnabled);
    }

    public void setEnabled(boolean enabled){
        View indicator = this.findViewById(R.id.tab_button_indicator);
        if(enabled){
            indicator.setBackground(getResources().getDrawable(R.color.white));
        }else {
            indicator.setBackground(getResources().getDrawable(android.R.color.transparent));
        }
        invalidate();
    }

}
