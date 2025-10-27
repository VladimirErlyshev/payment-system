INSERT INTO payments (
  guid,
  inquiry_ref_id,
  amount,
  currency,
  transaction_ref_id,
  status,
  note,
  created_at,
  updated_at
) VALUES
('a668f828-c2c5-4b83-8c41-ddd8b3ac3781','607ed0ea-cb8a-4ff8-a694-1213c314e65c', 99.99, 'USD', 'f113e373-b7b0-4f38-abf6-ccc3a89b8236', 'APPROVED', 'Initial test payment', '2025-01-02 12:00:00+00', '2025-01-02 12:00:00+00'),
('d557c717-f1f4-4e72-8f30-aaa7e2df2670','718fd1fb-dc9b-4ee9-b795-2324d425d76d', 150.50, 'USD', 'a224f484-c8c1-4d49-bc07-ddd4b9ac9347', 'APPROVED', 'Payment for order #1001', '2025-01-02 10:15:00+00', '2025-01-02 10:20:00+00');