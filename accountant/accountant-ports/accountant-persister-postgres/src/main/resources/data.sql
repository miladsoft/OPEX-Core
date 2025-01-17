INSERT INTO
   pair_config
VALUES
   ('btc_usdt', 'btc', 'usdt', 0.000001, 0.01, 55000),
   ('eth_usdt', 'eth', 'usdt', 0.00001, 0.01, 3800),
   ('nln_usdt', 'nln', 'usdt', 1.0, 0.01, 0.01),
   ('nln_btc', 'nln', 'btc', 1.0, 0.000001, 1 / 5500000) ON CONFLICT DO NOTHING;

INSERT INTO
   pair_fee_config
VALUES
   (1, 'btc_usdt', 'ASK', '*', 0.01, 0.01),
   (2, 'btc_usdt', 'BID', '*', 0.01, 0.01),
   (3, 'nln_usdt', 'ASK', '*', 0.01, 0.01),
   (4, 'nln_usdt', 'BID', '*', 0.01, 0.01),
   (5, 'nln_btc', 'ASK', '*', 0.01, 0.01),
   (6, 'nln_btc', 'BID', '*', 0.01, 0.01),
   (7, 'eth_usdt', 'ASK', '*', 0.01, 0.01),
   (8, 'eth_usdt', 'BID', '*', 0.01, 0.01) ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('pair_fee_config', 'id'), (SELECT MAX(id) FROM pair_fee_config));

COMMIT;
