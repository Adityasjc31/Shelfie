
package com.db.ms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the lifecycle status of an Order in the Digital Book Store Management system.
 * Each status includes:
 * - A numeric status code for internal representation or integration.
 * - A descriptive status detail for display and logging purposes.

 * Typical transitions:
 * PENDING -> SHIPPED -> DELIVERED

 * Usage:
 * Use statusCode for persistence or external systems.
 * Use statusDetail for user-facing messages and logs.
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2024-12-07
 */
@AllArgsConstructor
@Getter
public enum OrderEnum {

    /** The order has been received and is being processed. */
    PENDING(1, "Order is being processed"),

    /** The order has been shipped and is on its way to the customer. */
    SHIPPED(2, "Order has been shipped"),

    /** The order has been delivered successfully to the customer. */
    DELIVERED(3, "Order delivered successfully");

    /** Numeric code representing the status. Useful for persistence and integrations. */
    private final int statusCode;

    /** Human-readable description of the current status. */
    private final String statusDetail;
}
