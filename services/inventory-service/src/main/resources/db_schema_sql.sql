-- ==========================================
-- Database Schema for Inventory Service
-- Digital Bookstore Management System
-- ==========================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS bookstore_inventory
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bookstore_inventory;

-- ==========================================
-- Inventory Table
-- ==========================================
CREATE TABLE IF NOT EXISTS inventory (
    inventory_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL UNIQUE,
    quantity INT NOT NULL DEFAULT 0,
    low_stock_threshold INT NOT NULL DEFAULT 10,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_threshold CHECK (low_stock_threshold >= 1),
    
    -- Indexes for performance
    INDEX idx_book_id (book_id),
    INDEX idx_quantity (quantity),
    INDEX idx_low_stock (quantity, low_stock_threshold),
    INDEX idx_created_at (created_at),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- Insert Sample Data (for testing)
-- ==========================================
INSERT INTO inventory (book_id, quantity, low_stock_threshold) VALUES
    (101, 50, 10),
    (102, 75, 15),
    (103, 5, 10),   -- Low stock
    (104, 0, 10),   -- Out of stock
    (105, 100, 20),
    (106, 8, 10),   -- Low stock
    (107, 150, 25),
    (108, 30, 10),
    (109, 0, 15),   -- Out of stock
    (110, 200, 30)
ON DUPLICATE KEY UPDATE 
    quantity = VALUES(quantity),
    low_stock_threshold = VALUES(low_stock_threshold);

-- ==========================================
-- Stored Procedures
-- ==========================================

-- Procedure to check stock availability
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS CheckStockAvailability(
    IN p_book_id BIGINT,
    IN p_required_quantity INT,
    OUT p_available BOOLEAN
)
BEGIN
    DECLARE v_current_quantity INT;
    
    SELECT quantity INTO v_current_quantity
    FROM inventory
    WHERE book_id = p_book_id;
    
    IF v_current_quantity >= p_required_quantity THEN
        SET p_available = TRUE;
    ELSE
        SET p_available = FALSE;
    END IF;
END //
DELIMITER ;

-- Procedure to reduce inventory (with transaction)
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS ReduceInventory(
    IN p_book_id BIGINT,
    IN p_quantity INT
)
BEGIN
    DECLARE v_current_quantity INT;
    
    START TRANSACTION;
    
    -- Get current quantity with lock
    SELECT quantity INTO v_current_quantity
    FROM inventory
    WHERE book_id = p_book_id
    FOR UPDATE;
    
    -- Check if sufficient stock
    IF v_current_quantity >= p_quantity THEN
        UPDATE inventory
        SET quantity = quantity - p_quantity,
            updated_at = CURRENT_TIMESTAMP
        WHERE book_id = p_book_id;
        
        COMMIT;
    ELSE
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient stock available';
    END IF;
END //
DELIMITER ;

-- ==========================================
-- Views for Quick Access
-- ==========================================

-- View for low stock items
CREATE OR REPLACE VIEW v_low_stock_items AS
SELECT 
    i.inventory_id,
    i.book_id,
    i.quantity AS current_quantity,
    i.low_stock_threshold,
    (i.low_stock_threshold - i.quantity) AS quantity_needed,
    CASE 
        WHEN i.quantity = 0 THEN 'CRITICAL'
        ELSE 'WARNING'
    END AS alert_level,
    i.updated_at
FROM inventory i
WHERE i.quantity <= i.low_stock_threshold
ORDER BY i.quantity ASC;

-- View for out of stock items
CREATE OR REPLACE VIEW v_out_of_stock_items AS
SELECT 
    i.inventory_id,
    i.book_id,
    i.quantity,
    i.low_stock_threshold,
    i.created_at,
    i.updated_at
FROM inventory i
WHERE i.quantity = 0;

-- View for in-stock items
CREATE OR REPLACE VIEW v_in_stock_items AS
SELECT 
    i.inventory_id,
    i.book_id,
    i.quantity,
    i.low_stock_threshold,
    i.created_at,
    i.updated_at
FROM inventory i
WHERE i.quantity > 0
ORDER BY i.quantity DESC;

-- ==========================================
-- Triggers for Audit/History (Optional)
-- ==========================================

-- Create inventory history table for audit trail
CREATE TABLE IF NOT EXISTS inventory_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    old_quantity INT,
    new_quantity INT,
    quantity_change INT,
    operation_type VARCHAR(20),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(100),
    
    INDEX idx_inventory_id (inventory_id),
    INDEX idx_book_id (book_id),
    INDEX idx_changed_at (changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Trigger to log inventory changes
DELIMITER //
CREATE TRIGGER IF NOT EXISTS trg_inventory_update_history
AFTER UPDATE ON inventory
FOR EACH ROW
BEGIN
    IF OLD.quantity != NEW.quantity THEN
        INSERT INTO inventory_history (
            inventory_id,
            book_id,
            old_quantity,
            new_quantity,
            quantity_change,
            operation_type
        ) VALUES (
            NEW.inventory_id,
            NEW.book_id,
            OLD.quantity,
            NEW.quantity,
            NEW.quantity - OLD.quantity,
            CASE 
                WHEN NEW.quantity > OLD.quantity THEN 'RESTOCK'
                WHEN NEW.quantity < OLD.quantity THEN 'REDUCTION'
                ELSE 'UPDATE'
            END
        );
    END IF;
END //
DELIMITER ;

-- ==========================================
-- Indexes for Performance Optimization
-- ==========================================

-- Composite index for low stock queries
CREATE INDEX IF NOT EXISTS idx_quantity_threshold 
ON inventory(quantity, low_stock_threshold);

-- Index for history queries
CREATE INDEX IF NOT EXISTS idx_history_date 
ON inventory_history(changed_at DESC);

-- ==========================================
-- Comments for Documentation
-- ==========================================

ALTER TABLE inventory 
    COMMENT = 'Stores inventory information for books in the digital bookstore';

ALTER TABLE inventory_history 
    COMMENT = 'Audit trail for inventory quantity changes';

-- ==========================================
-- Database User and Permissions (Optional)
-- ==========================================

-- Create dedicated user for inventory service
-- CREATE USER IF NOT EXISTS 'inventory_service'@'%' IDENTIFIED BY 'secure_password_here';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON bookstore_inventory.* TO 'inventory_service'@'%';
-- FLUSH PRIVILEGES;

-- ==========================================
-- Analytics Queries (for reference)
-- ==========================================

-- Total inventory value (requires book price from book service)
-- SELECT SUM(quantity) AS total_books FROM inventory;

-- Low stock count
-- SELECT COUNT(*) AS low_stock_count FROM v_low_stock_items;

-- Out of stock count
-- SELECT COUNT(*) AS out_of_stock_count FROM v_out_of_stock_items;

-- Average stock level
-- SELECT AVG(quantity) AS avg_stock_level FROM inventory;
