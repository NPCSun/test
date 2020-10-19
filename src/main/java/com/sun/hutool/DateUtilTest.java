package com.sun.hutool;

import cn.hutool.core.date.DateUtil;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtilTest {
    public static void main(String[] args) {
        long now = System.currentTimeMillis();
        System.out.println(now);
        Instant instant = Instant.ofEpochMilli(now);
        OffsetDateTime date3 = instant.atOffset(ZoneOffset.ofHours(7));
        System.out.println(date3.toLocalDateTime().toString());
    }
}
