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

package com.android.askme.models;

import java.util.List;

/**
 * Created by Anand Sharmaon 10/10/15.
 */
public class RouteInfo {


    /**
     * status : OK
     * origin_addresses : ["Vancouver, BC, Canada","Seattle, État de Washington, États-Unis"]
     * destination_addresses : ["San Francisco, Californie, États-Unis","Victoria, BC, Canada"]
     * rows : [{"elements":[{"status":"OK","duration":{"value":340110,"text":"3 jours 22 heures"},"distance":{"value":1734542,"text":"1 735 km"}},{"status":"OK","duration":{"value":24487,"text":"6 heures 48 minutes"},"distance":{"value":129324,"text":"129 km"}}]},{"elements":[{"status":"OK","duration":{"value":288834,"text":"3 jours 8 heures"},"distance":{"value":1489604,"text":"1 490 km"}},{"status":"OK","duration":{"value":14388,"text":"4 heures 0 minutes"},"distance":{"value":135822,"text":"136 km"}}]}]
     */

    private String status;
    private List<String> origin_addresses;
    private List<String> destination_addresses;
    private List<RowsEntity> rows;

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrigin_addresses(List<String> origin_addresses) {
        this.origin_addresses = origin_addresses;
    }

    public void setDestination_addresses(List<String> destination_addresses) {
        this.destination_addresses = destination_addresses;
    }

    public void setRows(List<RowsEntity> rows) {
        this.rows = rows;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getOrigin_addresses() {
        return origin_addresses;
    }

    public List<String> getDestination_addresses() {
        return destination_addresses;
    }

    public List<RowsEntity> getRows() {
        return rows;
    }

    public static class RowsEntity {
        /**
         * elements : [{"status":"OK","duration":{"value":340110,"text":"3 jours 22 heures"},"distance":{"value":1734542,"text":"1 735 km"}},{"status":"OK","duration":{"value":24487,"text":"6 heures 48 minutes"},"distance":{"value":129324,"text":"129 km"}}]
         */

        private List<ElementsEntity> elements;

        public void setElements(List<ElementsEntity> elements) {
            this.elements = elements;
        }

        public List<ElementsEntity> getElements() {
            return elements;
        }

        public static class ElementsEntity {
            /**
             * status : OK
             * duration : {"value":340110,"text":"3 jours 22 heures"}
             * distance : {"value":1734542,"text":"1 735 km"}
             */

            private String status;
            private DurationEntity duration;
            private DistanceEntity distance;

            public void setStatus(String status) {
                this.status = status;
            }

            public void setDuration(DurationEntity duration) {
                this.duration = duration;
            }

            public void setDistance(DistanceEntity distance) {
                this.distance = distance;
            }

            public String getStatus() {
                return status;
            }

            public DurationEntity getDuration() {
                return duration;
            }

            public DistanceEntity getDistance() {
                return distance;
            }

            public static class DurationEntity {
                /**
                 * value : 340110
                 * text : 3 jours 22 heures
                 */

                private int value;
                private String text;

                public void setValue(int value) {
                    this.value = value;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public int getValue() {
                    return value;
                }

                public String getText() {
                    return text;
                }
            }

            public static class DistanceEntity {
                /**
                 * value : 1734542
                 * text : 1 735 km
                 */

                private int value;
                private String text;

                public void setValue(int value) {
                    this.value = value;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public int getValue() {
                    return value;
                }

                public String getText() {
                    return text;
                }
            }
        }
    }
}
