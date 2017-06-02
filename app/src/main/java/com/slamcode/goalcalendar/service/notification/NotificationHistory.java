package com.slamcode.goalcalendar.service.notification;

import java.util.Date;

/**
 * Interface for classes checking saving and retrieving data about specific notifications
 * occurrences
 */

public interface NotificationHistory {

    Date getLastTimeNotificationWasPublished(String notificationId);

    void markNotificationWasPublished(String notificationId);
}
