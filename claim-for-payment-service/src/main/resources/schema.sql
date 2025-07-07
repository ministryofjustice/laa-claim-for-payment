CREATE TABLE CLAIMS
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    ufn        VARCHAR(20)   NOT NULL,
    client     VARCHAR(50)   NOT NULL,
    category   VARCHAR(50)   NOT NULL,
    concluded  DATE          NOT NULL,
    fee_type   VARCHAR(50)   NOT NULL,
    claimed    DECIMAL(10, 2) NOT NULL
);
