package com.example.d308vacationproject;

import com.example.d308vacationproject.entities.Trip;

import org.junit.Test;

import static org.junit.Assert.*;


public class TripNameTest {

    // Tests that trip name is valid with normal trip name
    @Test
    public void testValidTripName() {
        Trip trip = new Trip(0, "Hawaii Vacation", "Hilton", "01/01/2026", "01/05/2026", false, false, 1);
        assertTrue(trip.isValid());
    }

    // Tests that trip name is valid with non-alphanumeric characters
    @Test
    public void testSpecialCharactersTripName() {
        Trip trip = new Trip(0, "!@#$%^&*()1234567890,./<>?`~-_=+[]{}/?", "Regular Hotel", "01/01/2001", "01/02/2001", false, false, 1);
        assertTrue(trip.isValid());
    }

    // Tests that trip name is invalid with empty string
    @Test
    public void testEmptyTripName() {
        Trip trip = new Trip(0, "", "Hilton", "01/01/2026", "01/05/2026", false, false, 1);
        assertFalse(trip.isValid());
    }

    // Tests that trip name is invalid as null
    @Test
    public void testNullTripName() {
        Trip trip = new Trip(0, null, "Hilton", "01/01/2026", "01/05/2026", false, false, 1);
        assertFalse(trip.isValid());
    }

    // Tests that trip name is invalid as whitespace
    @Test
    public void testWhitespaceTripName() {
        Trip trip = new Trip(0, "   ", "Hilton", "01/01/2026", "01/05/2026", false, false, 1);
        assertFalse(trip.isValid());
    }

}
