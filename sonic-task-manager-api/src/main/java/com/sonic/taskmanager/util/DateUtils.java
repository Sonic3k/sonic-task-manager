package com.sonic.taskmanager.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Get current date
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Get current datetime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Calculate days between two dates
     */
    public static long daysBetween(LocalDate from, LocalDate to) {
        return ChronoUnit.DAYS.between(from, to);
    }

    /**
     * Check if date is today
     */
    public static boolean isToday(LocalDate date) {
        return date != null && date.equals(LocalDate.now());
    }

    /**
     * Check if date is in the past
     */
    public static boolean isPast(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

    /**
     * Check if date is in the future
     */
    public static boolean isFuture(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    /**
     * Check if datetime is in the past
     */
    public static boolean isPast(LocalDateTime datetime) {
        return datetime != null && datetime.isBefore(LocalDateTime.now());
    }

    /**
     * Add days to a date
     */
    public static LocalDate addDays(LocalDate date, long days) {
        return date != null ? date.plusDays(days) : null;
    }

    /**
     * Subtract days from a date
     */
    public static LocalDate subtractDays(LocalDate date, long days) {
        return date != null ? date.minusDays(days) : null;
    }

    /**
     * Get start of day for a date
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    /**
     * Get end of day for a date
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date != null ? date.atTime(23, 59, 59) : null;
    }

    /**
     * Format date as string
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    /**
     * Format datetime as string
     */
    public static String formatDateTime(LocalDateTime datetime) {
        return datetime != null ? datetime.format(DATETIME_FORMATTER) : null;
    }

    /**
     * Parse date from string
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get days until deadline with friendly description
     */
    public static String getDeadlineDescription(LocalDate deadline) {
        if (deadline == null) {
            return "No deadline";
        }

        long days = daysBetween(LocalDate.now(), deadline);

        if (days < 0) {
            return "Overdue " + Math.abs(days) + " day" + (Math.abs(days) == 1 ? "" : "s");
        } else if (days == 0) {
            return "Due today";
        } else if (days == 1) {
            return "Due tomorrow";
        } else if (days <= 7) {
            return "Due in " + days + " days";
        } else if (days <= 30) {
            return "Due in " + days + " days";
        } else {
            return "Due in " + (days / 7) + " week" + ((days / 7) == 1 ? "" : "s");
        }
    }

    /**
     * Check if deadline is urgent (within 3 days)
     */
    public static boolean isUrgentDeadline(LocalDate deadline) {
        if (deadline == null) return false;
        long days = daysBetween(LocalDate.now(), deadline);
        return days >= 0 && days <= 3;
    }

    /**
     * Check if deadline is overdue
     */
    public static boolean isOverdueDeadline(LocalDate deadline) {
        if (deadline == null) return false;
        return deadline.isBefore(LocalDate.now());
    }

    /**
     * Get N days ago
     */
    public static LocalDate daysAgo(int days) {
        return LocalDate.now().minusDays(days);
    }

    /**
     * Get N days from now
     */
    public static LocalDate daysFromNow(int days) {
        return LocalDate.now().plusDays(days);
    }

    /**
     * Get N hours ago
     */
    public static LocalDateTime hoursAgo(int hours) {
        return LocalDateTime.now().minusHours(hours);
    }

    /**
     * Check if two dates are the same day
     */
    public static boolean isSameDay(LocalDate date1, LocalDate date2) {
        return date1 != null && date2 != null && date1.equals(date2);
    }
}