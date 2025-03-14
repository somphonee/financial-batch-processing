-- Transaction table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_date TIMESTAMP NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    account_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    processed BOOLEAN DEFAULT FALSE,
    processed_date TIMESTAMP
);

-- Monthly summary table
CREATE TABLE IF NOT EXISTS monthly_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    transaction_month VARCHAR(7) NOT NULL,
    total_amount DECIMAL(19,4) NOT NULL,
    entry_count INT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Yearly summary table
CREATE TABLE IF NOT EXISTS yearly_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    transaction_year INT NOT NULL,
    total_amount DECIMAL(19,4) NOT NULL,
    total_fees DECIMAL(19,4) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Job execution history
CREATE TABLE IF NOT EXISTS batch_job_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    read_count INT DEFAULT 0,
    processed_count INT DEFAULT 0,
    error_count INT DEFAULT 0,
    error_message VARCHAR(500)
);