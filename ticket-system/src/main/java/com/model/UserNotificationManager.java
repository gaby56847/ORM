package com.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

public class UserNotificationManager {
    // In-memory storage for user notifications
    private static final Map<Long, List<String>> userNotifications = new HashMap<>();

    // Add a notification for a specific user
    public static void addNotification(Long userId, String notification) {
        userNotifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(notification);
    }

    // Get notifications for a specific user
    public static List<String> getNotifications(Long userId) {
        return userNotifications.getOrDefault(userId, Collections.emptyList());
    }

    // Clear notifications for a specific user
    public static void clearNotifications(Long userId) {
        userNotifications.remove(userId);
    }

    // Clear all notifications (e.g., when a user logs out)
    public static void clearAllNotifications() {
        userNotifications.clear();
    }
}

