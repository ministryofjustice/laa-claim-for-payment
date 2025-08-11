-- Create a submission
INSERT INTO submissions (
    id,
    friendly_id,
    provider_user_id,
    provider_office_id,
    submission_type_code,
    submission_date,
    submission_period_start_date,
    submission_period_end_date,
    schedule_id
) SELECT
    '550e8400-e29b-41d4-a716-446655440000', -- fixed UUID (so claims can reference it)
    'LAA-001',
    '123e4567-e89b-12d3-a456-426614174000',
    '22222222-2222-2222-2222-222222222222',
    'TEST',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'SCH-001'
 WHERE NOT EXISTS (
  SELECT 1 FROM submissions
  WHERE id = '550e8400-e29b-41d4-a716-446655440000'
);

-- Seed claims linked to the submission
INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id)
SELECT'121120/467', 'Giordano', 'Family', '2025-03-18', 'Escape', 234.56, '550e8400-e29b-41d4-a716-446655440000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Giordano'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id)
SELECT'100323/098', 'Amoto', 'Immigration and Asylum', '2025-03-14', 'Fixed', 56.00, '550e8400-e29b-41d4-a716-446655440000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Amoto'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id)
SELECT'121120/678', 'DeMello', 'Immigration and Asylum', '2025-03-13', 'Hourly', 456.01, '550e8400-e29b-41d4-a716-446655440000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'DeMello'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id)
SELECT'121120/678', 'Omar', 'Immigration and Asylum', '2025-03-12', 'Hourly', 456.01, '550e8400-e29b-41d4-a716-446655440000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Omar'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id)
SELECT'121120/678', 'Abdelazim', 'Family', '2025-03-11', 'Hourly', 234.56, '550e8400-e29b-41d4-a716-446655440000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Abdelazim'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id)
SELECT'121120/765', 'Simpson', 'Family', '2025-03-07', 'Fixed', 234.56, '550e8400-e29b-41d4-a716-446655440000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Simpson'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id)
SELECT'121120/678', 'Gruffalo', 'Immigration and Asylum', '2025-03-02', 'Hourly', 456.01, '550e8400-e29b-41d4-a716-446655440000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Gruffalo'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id)
SELECT'121120/678', 'O''Connor', 'Family', '2025-03-01', 'Fixed', 234.56, '550e8400-e29b-41d4-a716-446655440000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'O''Connor'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id)
SELECT'100323/567', 'Tony', 'Immigration and Asylum', '2025-03-01', 'Fixed', 56.00, '550e8400-e29b-41d4-a716-446655440000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Tony'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id)
SELECT'100323/234', 'Bianchi', 'Immigration and Asylum', '2025-03-01', 'Fixed', 56.00, '550e8400-e29b-41d4-a716-446655440000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'Bianchi'
);;

INSERT INTO claims (ufn, client, category, concluded, fee_type, claimed, submission_id)
SELECT'100323/765', 'McKenna', 'Immigration and Asylum', '2025-03-01', 'Fixed', 56.00, '550e8400-e29b-41d4-a716-446655440000' WHERE NOT EXISTS (
    SELECT 1 FROM CLAIMS WHERE CLIENT = 'McKenna'
);;
