package com.barbershop_appointment_api.config;

import java.time.LocalTime;

public class BarbershopRules {

    public static final LocalTime OPENING = LocalTime.of(9, 0);
    public static final LocalTime CLOSING = LocalTime.of(18, 0);
    public static final LocalTime START_LUNCH = LocalTime.of(12, 0);
    public static final LocalTime END_LUNCH = LocalTime.of(13, 30);

}
