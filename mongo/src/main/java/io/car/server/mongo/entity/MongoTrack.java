/**
 * Copyright (C) 2013  Christian Autermann, Jan Alexander Wirwahn,
 *                     Arne De Wall, Dustin Demuth, Saqib Rasheed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.car.server.mongo.entity;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Indexed;
import com.github.jmkgreen.morphia.annotations.Reference;
import com.github.jmkgreen.morphia.annotations.Transient;
import com.github.jmkgreen.morphia.utils.IndexDirection;
import com.google.inject.Inject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import io.car.server.core.entities.Measurement;
import io.car.server.core.entities.Measurements;
import io.car.server.core.entities.Sensor;
import io.car.server.core.entities.Track;
import io.car.server.core.entities.User;

@Entity("tracks")
public class MongoTrack extends MongoBaseEntity<MongoTrack> implements Track {
    public static final String BBOX = "bbox";
    public static final String MEASUREMENTS = "measurements";
    public static final String SENSOR = "sensor";
    public static final String USER = "user";
    @Indexed(IndexDirection.GEO2D)
    @Embedded(BBOX)
    private Geometry bbox;
    @Reference(USER)
    private MongoUser user;
    @Reference(SENSOR)
    private MongoSensor sensor;
    @Reference(value = MEASUREMENTS, lazy = true)
    private List<MongoMeasurement> measurements = new ArrayList<MongoMeasurement>();
    @Inject
    @Transient
    private GeometryFactory factory;

    @Override
    public MongoTrack addMeasurement(Measurement measurement) {
        this.measurements.add((MongoMeasurement) measurement);
        return this;
    }

    @Override
    public MongoTrack removeMeasurement(Measurement measurement) {
        this.measurements.remove((MongoMeasurement) measurement);
        return this;
    }

    @Override
    public Measurements getMeasurements() {
        return new Measurements(this.measurements);
    }

    public MongoTrack setMeasurements(Measurements measurements) {
        this.measurements.clear();
        for (Measurement m : measurements) {
            this.measurements.add((MongoMeasurement) m);
        }
        return this;
    }

    @Override
    public MongoTrack addMeasurements(Measurements measurements) {
        for (Measurement m : measurements) {
            this.measurements.add((MongoMeasurement) m);
        }
        return this;
    }

    @Override
    public MongoTrack setBbox(Geometry bbox) {
        this.bbox = bbox;
        return this;
    }

    @Override
    public Geometry getBbox() {
        return this.bbox;
    }

    @Override
    public MongoTrack setBbox(double minx, double miny, double maxx, double maxy) {
        Coordinate[] coords = new Coordinate[] { new Coordinate(minx, miny),
                                                 new Coordinate(maxx, maxy) };
        this.bbox = factory.createPolygon(coords);
        return this;
    }

    @Override
    public String getIdentifier() {
        return (getId() == null) ? null : getId().toString();
    }

    @Override
    public MongoTrack setIdentifier(String id) {
        return setId(new ObjectId(id));
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public Track setUser(User user) {
        this.user = (MongoUser) user;
        return this;
    }

    @Override
    public MongoSensor getSensor() {
        return sensor;
    }

    @Override
    public MongoTrack setSensor(Sensor sensor) {
        this.sensor = (MongoSensor) sensor;
        return this;
    }
}