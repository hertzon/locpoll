package com.commonsware.cwac.locpoll;

/**
 * Created by Nelson Rodriguez on 01/12/2016.
 */

import android.location.Location;
import android.os.Bundle;

public class LocationPollerResult {

    private static final String KEY = "com.commonsware.cwac.locpoll.";
    static final String LOCATION_KEY = KEY + "EXTRA_LOCATION";
    static final String LASTKNOWN_LOCATION_KEY = KEY + "EXTRA_LASTKNOWN";
    static final String ERROR_KEY = KEY + "EXTRA_ERROR";

    private Bundle bundle;

    public LocationPollerResult(Bundle bundle) {
        this.bundle = bundle;
    }

    public Location getLocation() {
        return (Location)bundle.get(LOCATION_KEY);
    }

    public void setLocation(Location location) {
        bundle.putParcelable(LOCATION_KEY, location);
    }

    public Location getLastKnownLocation() {
        return (Location)bundle.get(LASTKNOWN_LOCATION_KEY);
    }

    public void setLastKnownLocation(Location location) {
        bundle.putParcelable(LASTKNOWN_LOCATION_KEY, location);
    }

    public String getError() {
        return bundle.getString(ERROR_KEY);
    }

    public Location getBestAvailableLocation() {
        Location location = getLocation();
        if (location == null) {
            location = getLastKnownLocation();
        }
        return location;
    }

}