-- =======================================================
-- 1. KHỞI TẠO DATABASE
-- =======================================================
DROP DATABASE IF EXISTS Laundry_DB;
CREATE DATABASE Laundry_DB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE Laundry_DB;

-- =======================================================
-- 2. TẠO CÁC BẢNG
-- =======================================================

-- 1. Users
CREATE TABLE Users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL UNIQUE, 
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    role VARCHAR(50) NOT NULL, 
    avatar VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    email VARCHAR(150) UNIQUE,
    reset_password_token VARCHAR(45)
);

-- 2. Services
CREATE TABLE Services (
    service_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(255) NOT NULL,
    description TEXT,
    image VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE 
);

-- 3. Clothing_Types
CREATE TABLE Clothing_Types (
    type_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(255) NOT NULL,
    category VARCHAR(50), 
    is_active BOOLEAN DEFAULT TRUE,
    image VARCHAR(255)
);

-- 4. Price_List
CREATE TABLE Price_List (
    price_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_id BIGINT NOT NULL,
    type_id BIGINT NOT NULL,
    unit VARCHAR(50) NOT NULL, 
    price DECIMAL(10, 2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    UNIQUE(service_id, type_id),
    FOREIGN KEY (service_id) REFERENCES Services(service_id),
    FOREIGN KEY (type_id) REFERENCES Clothing_Types(type_id)
);

-- 5. Orders
CREATE TABLE Orders (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    staff_id BIGINT,
    pickup_staff_id BIGINT,
    delivery_staff_id BIGINT,
    pickup_address TEXT,
    delivery_address TEXT,
    total_amount DECIMAL(10, 2) DEFAULT 0,
    status VARCHAR(50) DEFAULT 'PENDING', 
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES Users(user_id),
    FOREIGN KEY (staff_id) REFERENCES Users(user_id),
    FOREIGN KEY (pickup_staff_id) REFERENCES Users(user_id),
    FOREIGN KEY (delivery_staff_id) REFERENCES Users(user_id)
);

-- 6. Order_Details
CREATE TABLE Order_Details (
    detail_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    price_id BIGINT NOT NULL,
    quantity FLOAT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    note TEXT,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (price_id) REFERENCES Price_List(price_id)
);

-- =======================================================
-- 3. INSERT DỮ LIỆU MẪU (MẬT KHẨU KHÔNG MÃ HÓA)
-- =======================================================

INSERT INTO Users (full_name, phone, password, address, role, is_active, email) VALUES 
('Quản Trị Viên', '0901', '123456', 'Trụ sở chính', 'ADMIN', TRUE, 'nguyenthetan60484@gmail.com'),
('Nhân Viên Tiếp Nhận', '0902', '123456', 'Kho Hàng Số 1', 'STAFF', TRUE, 'staff1@laundry.com'),
('Shipper Nhanh Nhẹn', '0902000002', '123456', 'Kho Hàng Số 2', 'STAFF', TRUE, 'staff2@laundry.com'),
('Nguyễn Văn Khách A', '0903', '123456', '123 Cầu Giấy, Hà Nội', 'CLIENT', TRUE, 'clientA@gmail.com'),
('Trần Thị Khách B', '0903000002', '123456', '456 Đê La Thành, Hà Nội', 'CLIENT', TRUE, 'clientB@gmail.com'),
('Nhân Viên Đã Nghỉ', '0999999999', '123456', 'Đã nghỉ việc', 'STAFF', FALSE, 'nghiviec@laundry.com');

INSERT INTO Services (service_name, description, is_active) VALUES 
('Giặt sấy (Wash & Fold)', 'Giặt máy, sấy khô, gấp gọn.', TRUE), 
('Giặt khô (Dry Clean)', 'Giặt dung môi cao cấp.', TRUE),
('Vệ sinh giày (Shoes Spa)', 'Làm sạch sâu, khử mùi.', TRUE),
('Giặt chăn ga gối đệm', 'Giặt tẩy trắng, diệt khuẩn.', TRUE),
('Ủi hơi nước (Steam)', 'Chỉ ủi phẳng, không giặt.', TRUE),
('Dịch vụ cũ (Ngừng KD)', 'Dịch vụ này tạm ngưng hoạt động.', FALSE);

INSERT INTO Clothing_Types (type_name, category, is_active, image) VALUES 
('Quần áo hỗn hợp', 'normal', TRUE, NULL),
('Áo Sơ mi', 'normal', TRUE, NULL),
('Quần Tây / Kaki', 'normal', TRUE, NULL),
('Váy Dạ hội / Cưới', 'premium', TRUE, NULL),
('Bộ Vest (Suit)', 'premium', TRUE, NULL),
('Giày Thể thao', 'normal', TRUE, NULL);

INSERT INTO Price_List (service_id, type_id, unit, price, is_active) VALUES 
(1, 1, 'Kg', 15000, TRUE),
(1, 6, 'Kg', 25000, TRUE),
(2, 2, 'Cái', 30000, TRUE),
(2, 4, 'Cái', 150000, TRUE),
(2, 5, 'Bộ', 120000, TRUE),
(3, 6, 'Đôi', 80000, TRUE);

INSERT INTO Orders (customer_id, staff_id, delivery_address, total_amount, status, created_at) VALUES 
(4, NULL, '123 Cầu Giấy', 45000, 'PENDING', NOW()),
(5, 2, '456 Đê La Thành', 120000, 'CONFIRMED', NOW()),
(4, 2, '123 Cầu Giấy', 300000, 'PROCESSING', NOW()),
(5, 3, '456 Đê La Thành', 80000, 'SHIPPING', NOW()),
(4, 3, '123 Cầu Giấy', 150000, 'COMPLETED', '2023-12-20'),
(5, NULL, '789 Xã Đàn', 0, 'CANCELLED', '2023-12-21');

INSERT INTO Order_Details (order_id, price_id, quantity, unit_price, subtotal) VALUES 
(1, 1, 3, 15000, 45000),
(2, 5, 1, 120000, 120000),
(3, 4, 2, 150000, 300000),
(4, 6, 1, 80000, 80000),
(5, 4, 1, 150000, 150000),
(6, 1, 5, 15000, 75000);