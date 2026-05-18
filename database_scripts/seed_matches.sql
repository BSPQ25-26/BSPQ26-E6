-- ============================================================
-- SEED: Matches + Goals — 5 Major Leagues (2024-25 season)
-- Run AFTER seed_teams.sql
-- 15 finished + 5 upcoming per league = 100 matches total
-- ============================================================

-- ==========================
--  LA LIGA  (competition 1)
-- ==========================
INSERT INTO "match" (id, fk_left_team_id, fk_right_team_id, fk_league_id, datetime, left_score, right_score, finished, venue) VALUES
-- finished
( 1,  1,  2, 1, '2024-10-26 21:00:00+02', 3, 2, true,  'Santiago Bernabeu'),
( 2,  2,  3, 1, '2024-09-21 16:15:00+02', 2, 1, true,  'Estadi Olympic Lluis Companys'),
( 3,  3,  9, 1, '2024-09-15 14:00:00+02', 2, 0, true,  'Civitas Metropolitano'),
( 4,  4,  5, 1, '2024-09-28 18:30:00+02', 1, 1, true,  'San Mames'),
( 5,  1,  6, 1, '2024-10-05 21:00:00+02', 4, 1, true,  'Santiago Bernabeu'),
( 6,  9,  7, 1, '2024-10-19 18:30:00+02', 1, 2, true,  'Ramon Sanchez Pizjuan'),
( 7,  2,  1, 1, '2024-11-23 21:00:00+01', 1, 0, true,  'Estadi Olympic Lluis Companys'),
( 8,  3,  4, 1, '2024-11-10 16:15:00+01', 3, 1, true,  'Civitas Metropolitano'),
( 9, 10,  8, 1, '2024-11-03 14:00:00+01', 2, 1, true,  'Estadio Municipal de Montilivi'),
(10,  5,  6, 1, '2024-11-30 18:30:00+01', 0, 0, true,  'Reale Arena'),
(11,  1,  3, 1, '2025-01-25 21:00:00+01', 1, 1, true,  'Santiago Bernabeu'),
(12,  2,  4, 1, '2025-02-08 21:00:00+01', 2, 0, true,  'Estadi Olympic Lluis Companys'),
(13,  6,  9, 1, '2025-02-22 21:00:00+01', 3, 0, true,  'Estadio de la Ceramica'),
(14,  7,  8, 1, '2025-03-08 21:00:00+01', 1, 0, true,  'Estadio Benito Villamarin'),
(15, 10,  5, 1, '2025-03-22 18:30:00+01', 1, 2, true,  'Estadio Municipal de Montilivi'),
-- upcoming
(16,  1,  9, 1, '2026-05-24 21:00:00+02', 0, 0, false, 'Santiago Bernabeu'),
(17,  2,  8, 1, '2026-05-25 18:30:00+02', 0, 0, false, 'Estadi Olympic Lluis Companys'),
(18,  3, 10, 1, '2026-05-31 21:00:00+02', 0, 0, false, 'Civitas Metropolitano'),
(19,  4,  7, 1, '2026-06-07 18:30:00+02', 0, 0, false, 'San Mames'),
(20,  5,  6, 1, '2026-06-14 18:30:00+02', 0, 0, false, 'Reale Arena');

-- ==========================
--  PREMIER LEAGUE  (competition 2)
-- ==========================
INSERT INTO "match" (id, fk_left_team_id, fk_right_team_id, fk_league_id, datetime, left_score, right_score, finished, venue) VALUES
-- finished
(21, 12, 11, 2, '2024-09-22 16:30:00+01', 2, 1, true,  'Emirates Stadium'),
(22, 13, 14, 2, '2024-10-05 17:30:00+01', 3, 1, true,  'Anfield'),
(23, 15, 16, 2, '2024-10-19 17:30:00+01', 0, 3, true,  'Old Trafford'),
(24, 17, 18, 2, '2024-11-02 15:00:00+00', 1, 2, true,  'St. James Park'),
(25, 19, 20, 2, '2024-11-09 15:00:00+00', 2, 0, true,  'Amex Stadium'),
(26, 11, 13, 2, '2024-11-23 17:30:00+00', 2, 2, true,  'Etihad Stadium'),
(27, 14, 12, 2, '2024-12-07 17:30:00+00', 1, 1, true,  'Stamford Bridge'),
(28, 16, 17, 2, '2024-12-14 15:00:00+00', 2, 1, true,  'Tottenham Hotspur Stadium'),
(29, 18, 15, 2, '2025-01-18 15:00:00+00', 2, 0, true,  'Villa Park'),
(30, 20, 19, 2, '2025-01-25 15:00:00+00', 1, 3, true,  'London Stadium'),
(31, 13, 11, 2, '2025-02-01 17:30:00+00', 2, 0, true,  'Anfield'),
(32, 12, 16, 2, '2025-02-15 17:30:00+00', 1, 0, true,  'Emirates Stadium'),
(33, 14, 17, 2, '2025-03-01 15:00:00+00', 3, 2, true,  'Stamford Bridge'),
(34, 11, 18, 2, '2025-03-15 17:30:00+00', 4, 2, true,  'Etihad Stadium'),
(35, 19, 15, 2, '2025-03-29 15:00:00+00', 2, 1, true,  'Amex Stadium'),
-- upcoming
(36, 12, 13, 2, '2026-05-24 16:30:00+01', 0, 0, false, 'Emirates Stadium'),
(37, 11, 14, 2, '2026-05-25 16:30:00+01', 0, 0, false, 'Etihad Stadium'),
(38, 16, 20, 2, '2026-05-31 16:30:00+01', 0, 0, false, 'Tottenham Hotspur Stadium'),
(39, 17, 19, 2, '2026-06-07 16:30:00+01', 0, 0, false, 'St. James Park'),
(40, 15, 18, 2, '2026-06-14 16:30:00+01', 0, 0, false, 'Old Trafford');

-- ==========================
--  BUNDESLIGA  (competition 3)
-- ==========================
INSERT INTO "match" (id, fk_left_team_id, fk_right_team_id, fk_league_id, datetime, left_score, right_score, finished, venue) VALUES
-- finished
(41, 22, 23, 3, '2024-11-02 18:30:00+01', 4, 2, true,  'Allianz Arena'),
(42, 21, 24, 3, '2024-09-28 15:30:00+02', 3, 1, true,  'BayArena'),
(43, 23, 26, 3, '2024-10-19 15:30:00+02', 2, 1, true,  'Signal Iduna Park'),
(44, 25, 28, 3, '2024-10-26 15:30:00+02', 2, 0, true,  'Deutsche Bank Park'),
(45, 24, 21, 3, '2024-11-09 15:30:00+01', 1, 2, true,  'Red Bull Arena'),
(46, 26, 22, 3, '2024-11-23 15:30:00+01', 1, 3, true,  'MHPArena'),
(47, 27, 29, 3, '2024-12-07 15:30:00+01', 2, 2, true,  'Borussia Park'),
(48, 30, 25, 3, '2024-12-14 15:30:00+01', 0, 2, true,  'Stadion An der Alten Forsterei'),
(49, 22, 21, 3, '2025-02-08 18:30:00+01', 3, 3, true,  'Allianz Arena'),
(50, 23, 24, 3, '2025-02-22 15:30:00+01', 1, 0, true,  'Signal Iduna Park'),
(51, 21, 25, 3, '2025-03-08 15:30:00+01', 2, 0, true,  'BayArena'),
(52, 26, 27, 3, '2025-03-15 15:30:00+01', 1, 1, true,  'MHPArena'),
(53, 28, 30, 3, '2025-03-22 15:30:00+01', 2, 1, true,  'Weserstadion'),
(54, 29, 22, 3, '2025-04-05 15:30:00+02', 0, 3, true,  'Europa Park Stadion'),
(55, 24, 23, 3, '2025-04-19 15:30:00+02', 2, 1, true,  'Red Bull Arena'),
-- upcoming
(56, 22, 25, 3, '2026-05-23 18:30:00+02', 0, 0, false, 'Allianz Arena'),
(57, 21, 26, 3, '2026-05-30 15:30:00+02', 0, 0, false, 'BayArena'),
(58, 23, 28, 3, '2026-06-06 15:30:00+02', 0, 0, false, 'Signal Iduna Park'),
(59, 24, 27, 3, '2026-06-13 15:30:00+02', 0, 0, false, 'Red Bull Arena'),
(60, 29, 30, 3, '2026-06-20 15:30:00+02', 0, 0, false, 'Europa Park Stadion');

-- ==========================
--  SERIE A  (competition 4)
-- ==========================
INSERT INTO "match" (id, fk_left_team_id, fk_right_team_id, fk_league_id, datetime, left_score, right_score, finished, venue) VALUES
-- finished
(61, 31, 32, 4, '2024-09-22 20:45:00+02', 2, 1, true,  'Giuseppe Meazza'),
(62, 33, 34, 4, '2024-10-05 20:45:00+02', 1, 0, true,  'Allianz Stadium'),
(63, 35, 36, 4, '2024-10-20 20:45:00+02', 2, 2, true,  'Stadio Olimpico'),
(64, 37, 31, 4, '2024-11-03 20:45:00+01', 0, 2, true,  'Gewiss Stadium'),
(65, 34, 32, 4, '2024-11-10 20:45:00+01', 3, 1, true,  'Stadio Diego Armando Maradona'),
(66, 31, 33, 4, '2024-11-24 20:45:00+01', 1, 0, true,  'Giuseppe Meazza'),
(67, 38, 35, 4, '2024-12-08 20:45:00+01', 1, 1, true,  'Stadio Artemio Franchi'),
(68, 36, 37, 4, '2024-12-15 20:45:00+01', 2, 3, true,  'Stadio Olimpico'),
(69, 40, 39, 4, '2025-01-12 20:45:00+01', 2, 0, true,  'Renato Dall Ara'),
(70, 32, 31, 4, '2025-02-02 20:45:00+01', 2, 3, true,  'San Siro'),
(71, 34, 33, 4, '2025-02-16 20:45:00+01', 1, 2, true,  'Stadio Diego Armando Maradona'),
(72, 37, 35, 4, '2025-03-02 20:45:00+01', 3, 0, true,  'Gewiss Stadium'),
(73, 31, 36, 4, '2025-03-16 20:45:00+01', 4, 1, true,  'Giuseppe Meazza'),
(74, 33, 38, 4, '2025-03-30 20:45:00+02', 2, 1, true,  'Allianz Stadium'),
(75, 39, 40, 4, '2025-04-13 20:45:00+02', 1, 1, true,  'Olimpico Grande Torino'),
-- upcoming
(76, 31, 34, 4, '2026-05-23 20:45:00+02', 0, 0, false, 'Giuseppe Meazza'),
(77, 32, 33, 4, '2026-05-30 20:45:00+02', 0, 0, false, 'San Siro'),
(78, 35, 37, 4, '2026-06-06 20:45:00+02', 0, 0, false, 'Stadio Olimpico'),
(79, 36, 40, 4, '2026-06-13 20:45:00+02', 0, 0, false, 'Stadio Olimpico'),
(80, 38, 39, 4, '2026-06-20 20:45:00+02', 0, 0, false, 'Stadio Artemio Franchi');

-- ==========================
--  LIGUE 1  (competition 5)
-- ==========================
INSERT INTO "match" (id, fk_left_team_id, fk_right_team_id, fk_league_id, datetime, left_score, right_score, finished, venue) VALUES
-- finished
(81, 41, 42, 5, '2024-09-21 21:00:00+02', 3, 0, true,  'Parc des Princes'),
(82, 43, 44, 5, '2024-10-06 17:05:00+02', 2, 1, true,  'Stade Louis II'),
(83, 45, 47, 5, '2024-10-19 17:05:00+02', 1, 1, true,  'Groupama Stadium'),
(84, 46, 48, 5, '2024-11-02 17:05:00+01', 2, 0, true,  'Stade Bollaert-Delelis'),
(85, 42, 43, 5, '2024-11-09 21:00:00+01', 1, 2, true,  'Orange Velodrome'),
(86, 41, 45, 5, '2024-11-24 21:00:00+01', 4, 1, true,  'Parc des Princes'),
(87, 47, 41, 5, '2024-12-07 21:00:00+01', 0, 3, true,  'Allianz Riviera'),
(88, 44, 48, 5, '2024-12-14 17:05:00+01', 2, 1, true,  'Stade Pierre-Mauroy'),
(89, 49, 50, 5, '2025-01-11 17:05:00+01', 1, 0, true,  'Stade Auguste Delaune'),
(90, 42, 41, 5, '2025-01-25 21:00:00+01', 1, 3, true,  'Orange Velodrome'),
(91, 43, 45, 5, '2025-02-09 17:05:00+01', 2, 2, true,  'Stade Louis II'),
(92, 46, 47, 5, '2025-02-22 17:05:00+01', 1, 0, true,  'Stade Bollaert-Delelis'),
(93, 41, 44, 5, '2025-03-09 21:00:00+01', 2, 1, true,  'Parc des Princes'),
(94, 48, 49, 5, '2025-03-22 17:05:00+01', 3, 1, true,  'Roazhon Park'),
(95, 50, 43, 5, '2025-04-06 17:05:00+02', 0, 2, true,  'Stade de la Meinau'),
-- upcoming
(96,  41, 43, 5, '2026-05-23 21:00:00+02', 0, 0, false, 'Parc des Princes'),
(97,  42, 45, 5, '2026-05-30 21:00:00+02', 0, 0, false, 'Orange Velodrome'),
(98,  44, 47, 5, '2026-06-06 17:05:00+02', 0, 0, false, 'Stade Pierre-Mauroy'),
(99,  46, 48, 5, '2026-06-13 17:05:00+02', 0, 0, false, 'Stade Bollaert-Delelis'),
(100, 49, 50, 5, '2026-06-20 17:05:00+02', 0, 0, false, 'Stade Auguste Delaune');

-- Reset match sequence
SELECT setval(pg_get_serial_sequence('"match"', 'id'), (SELECT MAX(id) FROM "match"));

-- ==========================
--  GOALS
-- ==========================
INSERT INTO match_goal (fk_match_id, fk_team_id, goal_minute, stoppage_minute) VALUES
-- Match 1: Real Madrid 3-2 Barcelona
( 1,  1, 23, NULL), ( 1,  2, 44, NULL), ( 1,  1, 67, NULL), ( 1,  2, 75, NULL), ( 1,  1, 89, NULL),
-- Match 2: Barcelona 2-1 Atletico
( 2,  2, 31, NULL), ( 2,  3, 60, NULL), ( 2,  2, 78, NULL),
-- Match 3: Atletico 2-0 Sevilla
( 3,  3, 22, NULL), ( 3,  3, 55, NULL),
-- Match 4: Athletic 1-1 Real Sociedad
( 4,  4, 40, NULL), ( 4,  5, 82, NULL),
-- Match 5: Real Madrid 4-1 Villarreal
( 5,  1, 12, NULL), ( 5,  1, 38, NULL), ( 5,  6, 55, NULL), ( 5,  1, 67, NULL), ( 5,  1, 90, NULL),
-- Match 6: Sevilla 1-2 Real Betis
( 6,  7, 18, NULL), ( 6,  9, 33, NULL), ( 6,  7, 71, NULL),
-- Match 7: Barcelona 1-0 Real Madrid
( 7,  2, 52, NULL),
-- Match 8: Atletico 3-1 Athletic
( 8,  3, 15, NULL), ( 8,  3, 48, NULL), ( 8,  4, 63, NULL), ( 8,  3, 77, NULL),
-- Match 9: Girona 2-1 Valencia
( 9, 10, 34, NULL), ( 9,  8, 55, NULL), ( 9, 10, 68, NULL),
-- Match 10: Real Sociedad 0-0 Villarreal — no goals
-- Match 11: Real Madrid 1-1 Atletico
(11,  1, 30, NULL), (11,  3, 75, NULL),
-- Match 12: Barcelona 2-0 Athletic
(12,  2, 27, NULL), (12,  2, 65, NULL),
-- Match 13: Villarreal 3-0 Sevilla
(13,  6, 20, NULL), (13,  6, 45, NULL), (13,  6, 81, NULL),
-- Match 14: Real Betis 1-0 Valencia
(14,  7, 56, NULL),
-- Match 15: Girona 1-2 Real Sociedad
(15,  5, 22, NULL), (15, 10, 40, NULL), (15,  5, 77, NULL),

-- Match 21: Arsenal 2-1 Man City
(21, 12, 34, NULL), (21, 11, 55, NULL), (21, 12, 78, NULL),
-- Match 22: Liverpool 3-1 Chelsea
(22, 13, 11, NULL), (22, 14, 45, NULL), (22, 13, 56, NULL), (22, 13, 89, NULL),
-- Match 23: Man United 0-3 Tottenham
(23, 16, 22, NULL), (23, 16, 58, NULL), (23, 16, 83, NULL),
-- Match 24: Newcastle 1-2 Aston Villa
(24, 18, 31, NULL), (24, 17, 67, NULL), (24, 18, 84, NULL),
-- Match 25: Brighton 2-0 West Ham
(25, 19, 42, NULL), (25, 19, 77, NULL),
-- Match 26: Man City 2-2 Liverpool
(26, 11, 28, NULL), (26, 13, 44, NULL), (26, 11, 61, NULL), (26, 13, 87, NULL),
-- Match 27: Chelsea 1-1 Arsenal
(27, 14, 33, NULL), (27, 12, 71, NULL),
-- Match 28: Tottenham 2-1 Newcastle
(28, 16, 15, NULL), (28, 17, 45, NULL), (28, 16, 66, NULL),
-- Match 29: Aston Villa 2-0 Man United
(29, 18, 18, NULL), (29, 18, 54, NULL),
-- Match 30: West Ham 1-3 Brighton
(30, 19, 23, NULL), (30, 20, 45, NULL), (30, 19, 67, NULL), (30, 19, 89, NULL),
-- Match 31: Liverpool 2-0 Man City
(31, 13, 39, NULL), (31, 13, 75, NULL),
-- Match 32: Arsenal 1-0 Tottenham
(32, 12, 62, NULL),
-- Match 33: Chelsea 3-2 Newcastle
(33, 14, 12, NULL), (33, 17, 35, NULL), (33, 14, 48, NULL), (33, 14, 77, NULL), (33, 17, 90, NULL),
-- Match 34: Man City 4-2 Aston Villa
(34, 11,  8, NULL), (34, 11, 34, NULL), (34, 18, 55, NULL), (34, 11, 67, NULL), (34, 18, 78, NULL), (34, 11, 84, NULL),
-- Match 35: Brighton 2-1 Man United
(35, 19, 30, NULL), (35, 15, 50, NULL), (35, 19, 71, NULL),

-- Match 41: Bayern 4-2 Dortmund
(41, 22, 10, NULL), (41, 22, 37, NULL), (41, 23, 28, NULL), (41, 23, 55, NULL), (41, 22, 65, NULL), (41, 22, 88, NULL),
-- Match 42: Leverkusen 3-1 Leipzig
(42, 21, 22, NULL), (42, 24, 40, NULL), (42, 21, 55, NULL), (42, 21, 78, NULL),
-- Match 43: Dortmund 2-1 Stuttgart
(43, 23, 31, NULL), (43, 26, 50, NULL), (43, 23, 67, NULL),
-- Match 44: Frankfurt 2-0 Bremen
(44, 25, 44, NULL), (44, 25, 83, NULL),
-- Match 45: Leipzig 1-2 Leverkusen
(45, 24, 25, NULL), (45, 21, 40, NULL), (45, 21, 77, NULL),
-- Match 46: Stuttgart 1-3 Bayern
(46, 22, 12, NULL), (46, 22, 48, NULL), (46, 26, 55, NULL), (46, 22, 89, NULL),
-- Match 47: Gladbach 2-2 Freiburg
(47, 27, 30, NULL), (47, 29, 50, NULL), (47, 27, 75, NULL), (47, 29, 88, NULL),
-- Match 48: Union Berlin 0-2 Frankfurt
(48, 25, 35, NULL), (48, 25, 71, NULL),
-- Match 49: Bayern 3-3 Leverkusen
(49, 22, 15, NULL), (49, 21, 22, NULL), (49, 22, 50, NULL), (49, 21, 65, NULL), (49, 22, 78, NULL), (49, 21, 90, NULL),
-- Match 50: Dortmund 1-0 Leipzig
(50, 23, 55, NULL),
-- Match 51: Leverkusen 2-0 Frankfurt
(51, 21, 33, NULL), (51, 21, 80, NULL),
-- Match 52: Stuttgart 1-1 Gladbach
(52, 26, 40, NULL), (52, 27, 75, NULL),
-- Match 53: Bremen 2-1 Union Berlin
(53, 28, 22, NULL), (53, 30, 48, NULL), (53, 28, 65, NULL),
-- Match 54: Freiburg 0-3 Bayern
(54, 22, 28, NULL), (54, 22, 60, NULL), (54, 22, 85, NULL),
-- Match 55: Leipzig 2-1 Dortmund
(55, 24, 35, NULL), (55, 23, 55, NULL), (55, 24, 77, NULL),

-- Match 61: Inter 2-1 AC Milan
(61, 31, 28, NULL), (61, 32, 45, NULL), (61, 31, 67, NULL),
-- Match 62: Juventus 1-0 Napoli
(62, 33, 55, NULL),
-- Match 63: Roma 2-2 Lazio
(63, 35, 22, NULL), (63, 36, 45, NULL), (63, 35, 78, NULL), (63, 36, 88, NULL),
-- Match 64: Atalanta 0-2 Inter
(64, 31, 40, NULL), (64, 31, 80, NULL),
-- Match 65: Napoli 3-1 AC Milan
(65, 34, 12, NULL), (65, 32, 35, NULL), (65, 34, 50, NULL), (65, 34, 77, NULL),
-- Match 66: Inter 1-0 Juventus
(66, 31, 71, NULL),
-- Match 67: Fiorentina 1-1 Roma
(67, 38, 44, NULL), (67, 35, 66, NULL),
-- Match 68: Lazio 2-3 Atalanta
(68, 37, 15, NULL), (68, 36, 30, NULL), (68, 37, 55, NULL), (68, 36, 70, NULL), (68, 37, 88, NULL),
-- Match 69: Bologna 2-0 Torino
(69, 40, 35, NULL), (69, 40, 78, NULL),
-- Match 70: AC Milan 2-3 Inter
(70, 31, 10, NULL), (70, 32, 20, NULL), (70, 31, 55, NULL), (70, 32, 65, NULL), (70, 31, 88, NULL),
-- Match 71: Napoli 1-2 Juventus
(71, 33, 28, NULL), (71, 34, 45, NULL), (71, 33, 77, NULL),
-- Match 72: Atalanta 3-0 Roma
(72, 37, 22, NULL), (72, 37, 55, NULL), (72, 37, 84, NULL),
-- Match 73: Inter 4-1 Lazio
(73, 31, 15, NULL), (73, 31, 38, NULL), (73, 36, 50, NULL), (73, 31, 67, NULL), (73, 31, 89, NULL),
-- Match 74: Juventus 2-1 Fiorentina
(74, 33, 33, NULL), (74, 38, 55, NULL), (74, 33, 70, NULL),
-- Match 75: Torino 1-1 Bologna
(75, 39, 40, NULL), (75, 40, 75, NULL),

-- Match 81: PSG 3-0 Marseille
(81, 41, 22, NULL), (81, 41, 55, NULL), (81, 41, 88, NULL),
-- Match 82: Monaco 2-1 Lille
(82, 43, 30, NULL), (82, 44, 50, NULL), (82, 43, 67, NULL),
-- Match 83: Lyon 1-1 Nice
(83, 45, 33, NULL), (83, 47, 77, NULL),
-- Match 84: Lens 2-0 Rennes
(84, 46, 40, NULL), (84, 46, 75, NULL),
-- Match 85: Marseille 1-2 Monaco
(85, 43, 22, NULL), (85, 42, 55, NULL), (85, 43, 78, NULL),
-- Match 86: PSG 4-1 Lyon
(86, 41, 10, NULL), (86, 41, 35, NULL), (86, 45, 50, NULL), (86, 41, 65, NULL), (86, 41, 83, NULL),
-- Match 87: Nice 0-3 PSG
(87, 41, 25, NULL), (87, 41, 60, NULL), (87, 41, 89, NULL),
-- Match 88: Lille 2-1 Rennes
(88, 44, 30, NULL), (88, 48, 55, NULL), (88, 44, 70, NULL),
-- Match 89: Reims 1-0 Strasbourg
(89, 49, 45, NULL),
-- Match 90: Marseille 1-3 PSG
(90, 41, 22, NULL), (90, 42, 40, NULL), (90, 41, 66, NULL), (90, 41, 88, NULL),
-- Match 91: Monaco 2-2 Lyon
(91, 43, 25, NULL), (91, 45, 45, NULL), (91, 45, 70, NULL), (91, 43, 80, NULL),
-- Match 92: Lens 1-0 Nice
(92, 46, 55, NULL),
-- Match 93: PSG 2-1 Lille
(93, 41, 33, NULL), (93, 44, 55, NULL), (93, 41, 77, NULL),
-- Match 94: Rennes 3-1 Reims
(94, 48, 20, NULL), (94, 49, 40, NULL), (94, 48, 55, NULL), (94, 48, 85, NULL),
-- Match 95: Strasbourg 0-2 Monaco
(95, 43, 35, NULL), (95, 43, 78, NULL);

-- Reset match_goal sequence
SELECT setval(pg_get_serial_sequence('match_goal', 'id'), (SELECT MAX(id) FROM match_goal));
