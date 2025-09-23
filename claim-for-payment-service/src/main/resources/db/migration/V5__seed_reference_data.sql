-- Seed claims linked to the submission
INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT'121120/467', 'Giordano', 'Family', '2025-03-18', 'Escape', 234.56, '550e8400-e29b-41d4-a716-446655440000', '123e4567-e89b-12d3-a456-426614174000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Giordano'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT'100323/098', 'Amoto', 'Immigration and Asylum', '2025-03-14', 'Fixed', 56.00, '550e8400-e29b-41d4-a716-446655440000', '123e4567-e89b-12d3-a456-426614174000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Amoto'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT'121120/678', 'DeMello', 'Immigration and Asylum', '2025-03-13', 'Hourly', 456.01, '550e8400-e29b-41d4-a716-446655440000', '123e4567-e89b-12d3-a456-426614174000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'DeMello'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT'121120/678', 'Omar', 'Immigration and Asylum', '2025-03-12', 'Hourly', 456.01, '550e8400-e29b-41d4-a716-446655440000', '123e4567-e89b-12d3-a456-426614174000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Omar'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT'121120/678', 'Abdelazim', 'Family', '2025-03-11', 'Hourly', 234.56, '550e8400-e29b-41d4-a716-446655440000', '123e4567-e89b-12d3-a456-426614174000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Abdelazim'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT'121120/765', 'Simpson', 'Family', '2025-03-07', 'Fixed', 234.56, '550e8400-e29b-41d4-a716-446655440000', '123e4567-e89b-12d3-a456-426614174000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Simpson'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT'121120/678', 'Gruffalo', 'Immigration and Asylum', '2025-03-02', 'Hourly', 456.01, '550e8400-e29b-41d4-a716-446655440000', '123e4567-e89b-12d3-a456-426614174000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Gruffalo'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT'121120/678', 'O''Connor', 'Family', '2025-03-01', 'Fixed', 234.56, '550e8400-e29b-41d4-a716-446655440000', '123e4567-e89b-12d3-a456-426614174000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'O''Connor'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT'100323/567', 'Tony', 'Immigration and Asylum', '2025-03-01', 'Fixed', 56.00, '550e8400-e29b-41d4-a716-446655440000', '123e4567-e89b-12d3-a456-426614174000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Tony'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT'100323/234', 'Bianchi', 'Immigration and Asylum', '2025-03-01', 'Fixed', 56.00, '550e8400-e29b-41d4-a716-446655440000', '123e4567-e89b-12d3-a456-426614174000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Bianchi'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT'100323/765', 'McKenna', 'Immigration and Asylum', '2025-03-01', 'Fixed', 56.00, '550e8400-e29b-41d4-a716-446655440000', '123e4567-e89b-12d3-a456-426614174000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'McKenna'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT '240301/101', 'Johnson', 'Immigration and Asylum', '2025-03-02', 'Hourly', 429.01, '6c5c142d-cb45-4ef6-865f-d4ecce40811c', 'd9c4b277-941c-451c-81c4-6b46b7f7ab59' WHERE NOT EXISTS (
    SELECT 1 FROM claims WHERE client = 'Johnson'
);

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT '240301/102', 'Anderson', 'Family', '2025-03-01', 'Fixed', 234.56, 'c45ae7b7-95e7-4c2f-b3b7-b95b03f71cf8', 'd9c4b277-941c-451c-81c4-6b46b7f7ab59' WHERE NOT EXISTS (
    SELECT 1 FROM claims WHERE client = 'Anderson'
);

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT '240301/103', 'Thompson', 'Immigration and Asylum', '2025-03-01', 'Fixed', 56.00, 'a173ef60-58c0-4d7f-9400-1d97f1adf287', 'd9c4b277-941c-451c-81c4-6b46b7f7ab59' WHERE NOT EXISTS (
    SELECT 1 FROM claims WHERE client = 'Thompson'
);

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT '240301/104', 'Harrison', 'Immigration and Asylum', '2025-03-01', 'Fixed', 76.00, 'f92c91f4-66e0-4cb2-8eae-0b8a0b4b5d73', 'd9c4b277-941c-451c-81c4-6b46b7f7ab59' WHERE NOT EXISTS (
    SELECT 1 FROM claims WHERE client = 'Harrison'
);

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id, provider_user_id)
SELECT '240301/105', 'Mitchell', 'Immigration and Asylum', '2025-03-01', 'Fixed', 52.00, '2c9b3892-9de7-4e76-b7c6-6d96fa36a6b3', 'd9c4b277-941c-451c-81c4-6b46b7f7ab59' WHERE NOT EXISTS (
    SELECT 1 FROM claims WHERE client = 'Mitchell'
);

