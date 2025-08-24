CREATE TABLE transactions (
    transaction_id VARCHAR(255) PRIMARY KEY,
    transaction_type VARCHAR(50) NOT NULL,
    sender_user_id VARCHAR(255) NOT NULL,
    receiver_user_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    description VARCHAR(255),
    reference_id VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
