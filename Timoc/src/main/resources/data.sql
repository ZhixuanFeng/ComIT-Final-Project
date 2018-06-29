/*
INSERT INTO role (role_id, role)
SELECT 1, "USER"
FROM DUAL
WHERE NOT EXISTS (SELECT * FROM role WHERE role_id = 1);

/*
 *  Initialize role table
 */
INSERT INTO role (role_id, role) VALUES (1, "USER");
INSERT INTO role (role_id, role) VALUES (2, "ADMIN");


/*
 *  Initialize card table
 */
/* Spade */
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (1, 0, 20, 0, 1, 0, 1, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (2, 0, 20, 0, 2, 0, 2, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (3, 0, 20, 0, 3, 0, 3, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (4, 0, 20, 0, 4, 0, 4, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (5, 0, 20, 0, 5, 0, 5, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (6, 0, 20, 0, 6, 0, 6, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (7, 0, 20, 0, 7, 0, 7, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (8, 0, 20, 0, 8, 0, 8, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (9, 0, 20, 0, 9, 0, 9, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (10, 0, 20, 0, 10, 0, 10, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (11, 0, 20, 0, 11, 0, 11, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (12, 0, 20, 0, 12, 0, 12, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (13, 0, 20, 0, 13, 0, 13, 0, 0, 0);
/* Heart */
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (14, 0, 20, 1, 1, 0, 0, 1, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (15, 0, 20, 1, 2, 0, 0, 2, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (16, 0, 20, 1, 3, 0, 0, 3, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (17, 0, 20, 1, 4, 0, 0, 4, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (18, 0, 20, 1, 5, 0, 0, 5, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (19, 0, 20, 1, 6, 0, 0, 6, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (20, 0, 20, 1, 7, 0, 0, 7, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (21, 0, 20, 1, 8, 0, 0, 8, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (22, 0, 20, 1, 9, 0, 0, 9, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (23, 0, 20, 1, 10, 0, 0, 10, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (24, 0, 20, 1, 11, 0, 0, 11, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (25, 0, 20, 1, 12, 0, 0, 12, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (26, 0, 20, 1, 13, 0, 0, 13, 0, 0);
/* Club */
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (27, 0, 20, 2, 1, 0, 1, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (28, 0, 20, 2, 2, 1, 1, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (29, 0, 20, 2, 3, 2, 1, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (30, 0, 20, 2, 4, 2, 2, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (31, 0, 20, 2, 5, 2, 3, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (32, 0, 20, 2, 6, 3, 3, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (33, 0, 20, 2, 7, 4, 3, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (34, 0, 20, 2, 8, 4, 4, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (35, 0, 20, 2, 9, 4, 5, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (36, 0, 20, 2, 10, 5, 5, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (37, 0, 20, 2, 11, 6, 5, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (38, 0, 20, 2, 12, 6, 6, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (39, 0, 20, 2, 13, 7, 6, 0, 0, 0);
/* Diamond */
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (40, 0, 20, 3, 1, 1, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (41, 0, 20, 3, 2, 2, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (42, 0, 20, 3, 3, 3, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (43, 0, 20, 3, 4, 4, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (44, 0, 20, 3, 5, 5, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (45, 0, 20, 3, 6, 6, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (46, 0, 20, 3, 7, 7, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (47, 0, 20, 3, 8, 8, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (48, 0, 20, 3, 9, 9, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (49, 0, 20, 3, 10, 10, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (50, 0, 20, 3, 11, 11, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (51, 0, 20, 3, 12, 12, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, draw) VALUES (52, 0, 20, 3, 13, 13, 0, 0, 0, 0);
