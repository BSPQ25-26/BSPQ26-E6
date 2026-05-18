-- ============================================================
-- SEED: Countries, Competitions, Teams — 5 Major Leagues
-- Run this BEFORE seed_matches.sql
-- ============================================================

-- Countries
INSERT INTO country (id, name) VALUES
  (1, 'Spain'),
  (2, 'England'),
  (3, 'Germany'),
  (4, 'Italy'),
  (5, 'France')
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;

-- Competitions
INSERT INTO competition (id, name) VALUES
  (1, 'La Liga'),
  (2, 'Premier League'),
  (3, 'Bundesliga'),
  (4, 'Serie A'),
  (5, 'Ligue 1')
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;

-- ---- La Liga (Spain) ----
INSERT INTO team (id, name, logo_url, fk_country_id) VALUES
  (1,  'Real Madrid',          'https://crests.football-data.org/86.png',  1),
  (2,  'FC Barcelona',         'https://crests.football-data.org/81.png',  1),
  (3,  'Atletico de Madrid',   'https://crests.football-data.org/78.png',  1),
  (4,  'Athletic Club',        'https://crests.football-data.org/77.png',  1),
  (5,  'Real Sociedad',        'https://crests.football-data.org/92.png',  1),
  (6,  'Villarreal CF',        'https://crests.football-data.org/94.png',  1),
  (7,  'Real Betis',           'https://crests.football-data.org/90.png',  1),
  (8,  'Valencia CF',          'https://crests.football-data.org/95.png',  1),
  (9,  'Sevilla FC',           'https://crests.football-data.org/559.png', 1),
  (10, 'Girona FC',            'https://crests.football-data.org/298.png', 1)
ON CONFLICT (id) DO UPDATE SET
  name          = EXCLUDED.name,
  logo_url      = EXCLUDED.logo_url,
  fk_country_id = EXCLUDED.fk_country_id;

-- ---- Premier League (England) ----
INSERT INTO team (id, name, logo_url, fk_country_id) VALUES
  (11, 'Manchester City',      'https://crests.football-data.org/65.png',  2),
  (12, 'Arsenal FC',           'https://crests.football-data.org/57.png',  2),
  (13, 'Liverpool FC',         'https://crests.football-data.org/64.png',  2),
  (14, 'Chelsea FC',           'https://crests.football-data.org/61.png',  2),
  (15, 'Manchester United',    'https://crests.football-data.org/66.png',  2),
  (16, 'Tottenham Hotspur',    'https://crests.football-data.org/73.png',  2),
  (17, 'Newcastle United',     'https://crests.football-data.org/67.png',  2),
  (18, 'Aston Villa',          'https://crests.football-data.org/58.png',  2),
  (19, 'Brighton & Hove',      'https://crests.football-data.org/397.png', 2),
  (20, 'West Ham United',      'https://crests.football-data.org/563.png', 2)
ON CONFLICT (id) DO UPDATE SET
  name          = EXCLUDED.name,
  logo_url      = EXCLUDED.logo_url,
  fk_country_id = EXCLUDED.fk_country_id;

-- ---- Bundesliga (Germany) ----
INSERT INTO team (id, name, logo_url, fk_country_id) VALUES
  (21, 'Bayer Leverkusen',     'https://crests.football-data.org/3.png',   3),
  (22, 'Bayern Munchen',       'https://crests.football-data.org/5.png',   3),
  (23, 'Borussia Dortmund',    'https://crests.football-data.org/4.png',   3),
  (24, 'RB Leipzig',           'https://crests.football-data.org/721.png', 3),
  (25, 'Eintracht Frankfurt',  'https://crests.football-data.org/19.png',  3),
  (26, 'VfB Stuttgart',        'https://crests.football-data.org/10.png',  3),
  (27, 'B. Monchengladbach',   'https://crests.football-data.org/18.png',  3),
  (28, 'Werder Bremen',        'https://crests.football-data.org/12.png',  3),
  (29, 'SC Freiburg',          'https://crests.football-data.org/17.png',  3),
  (30, 'Union Berlin',         'https://crests.football-data.org/28.png',  3)
ON CONFLICT (id) DO UPDATE SET
  name          = EXCLUDED.name,
  logo_url      = EXCLUDED.logo_url,
  fk_country_id = EXCLUDED.fk_country_id;

-- ---- Serie A (Italy) ----
INSERT INTO team (id, name, logo_url, fk_country_id) VALUES
  (31, 'Inter Milan',          'https://crests.football-data.org/108.png', 4),
  (32, 'AC Milan',             'https://crests.football-data.org/98.png',  4),
  (33, 'Juventus FC',          'https://crests.football-data.org/109.png', 4),
  (34, 'SSC Napoli',           'https://crests.football-data.org/113.png', 4),
  (35, 'AS Roma',              'https://crests.football-data.org/100.png', 4),
  (36, 'SS Lazio',             'https://crests.football-data.org/110.png', 4),
  (37, 'Atalanta BC',          'https://crests.football-data.org/102.png', 4),
  (38, 'ACF Fiorentina',       'https://crests.football-data.org/99.png',  4),
  (39, 'Torino FC',            'https://crests.football-data.org/586.png', 4),
  (40, 'Bologna FC',           'https://crests.football-data.org/103.png', 4)
ON CONFLICT (id) DO UPDATE SET
  name          = EXCLUDED.name,
  logo_url      = EXCLUDED.logo_url,
  fk_country_id = EXCLUDED.fk_country_id;

-- ---- Ligue 1 (France) ----
INSERT INTO team (id, name, logo_url, fk_country_id) VALUES
  (41, 'Paris Saint-Germain',  'https://crests.football-data.org/524.png', 5),
  (42, 'Olympique Marseille',  'https://crests.football-data.org/516.png', 5),
  (43, 'AS Monaco',            'https://crests.football-data.org/548.png', 5),
  (44, 'Lille OSC',            'https://crests.football-data.org/521.png', 5),
  (45, 'Olympique Lyonnais',   'https://crests.football-data.org/523.png', 5),
  (46, 'RC Lens',              'https://crests.football-data.org/532.png', 5),
  (47, 'OGC Nice',             'https://crests.football-data.org/522.png', 5),
  (48, 'Stade Rennais',        'https://crests.football-data.org/529.png', 5),
  (49, 'Stade de Reims',       'https://crests.football-data.org/527.png', 5),
  (50, 'RC Strasbourg',        'https://crests.football-data.org/576.png', 5)
ON CONFLICT (id) DO UPDATE SET
  name          = EXCLUDED.name,
  logo_url      = EXCLUDED.logo_url,
  fk_country_id = EXCLUDED.fk_country_id;

-- Reset sequences so future auto-inserts don't conflict
SELECT setval(pg_get_serial_sequence('country',     'id'), (SELECT MAX(id) FROM country));
SELECT setval(pg_get_serial_sequence('competition', 'id'), (SELECT MAX(id) FROM competition));
SELECT setval(pg_get_serial_sequence('team',        'id'), (SELECT MAX(id) FROM team));
