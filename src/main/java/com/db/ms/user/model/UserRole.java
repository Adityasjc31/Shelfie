package com.db.ms.user.model;

/**
 * Enumeration representing different user roles in the system.
 * Defines access levels and permissions for users.
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 */
public enum UserRole {
    /**
     * Customer role - can browse, purchase books, and manage their orders
     */
    CUSTOMER,
    
    /**
     * Admin role - has full access to manage books, users, and orders
     */
    ADMIN
}

