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
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (2, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (3, 0, 0, 0, 3, 0, 3, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (4, 0, 0, 0, 4, 0, 4, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (5, 0, 0, 0, 5, 0, 5, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (6, 0, 0, 0, 6, 0, 6, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (7, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (8, 0, 0, 0, 8, 0, 8, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (9, 0, 0, 0, 9, 0, 9, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (10, 0, 0, 0, 10, 0, 10, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (11, 0, 0, 0, 11, 0, 11, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (12, 0, 0, 0, 12, 0, 12, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (13, 0, 0, 0, 13, 0, 13, 0, 0, 0, 0, 0, 0);
/* Heart */
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (14, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (15, 0, 0, 1, 2, 0, 0, 2, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (16, 0, 0, 1, 3, 0, 0, 3, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (17, 0, 0, 1, 4, 0, 0, 4, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (18, 0, 0, 1, 5, 0, 0, 5, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (19, 0, 0, 1, 6, 0, 0, 6, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (0, 0, 0, 1, 7, 0, 0, 7, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (21, 0, 0, 1, 8, 0, 0, 8, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (22, 0, 0, 1, 9, 0, 0, 9, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (23, 0, 0, 1, 10, 0, 0, 10, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (24, 0, 0, 1, 11, 0, 0, 11, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (25, 0, 0, 1, 12, 0, 0, 0, 0, 0, 0, 24, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (26, 0, 0, 1, 13, 0, 0, 13, 0, 0, 0, 0, 0);
/* Club */
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (27, 0, 0, 2, 1, 0, 1, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (28, 0, 0, 2, 2, 1, 1, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (29, 0, 0, 2, 3, 2, 1, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (30, 0, 0, 2, 4, 2, 2, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (31, 0, 0, 2, 5, 2, 3, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (32, 0, 0, 2, 6, 3, 3, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (33, 0, 0, 2, 7, 4, 3, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (34, 0, 0, 2, 8, 4, 4, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (35, 0, 0, 2, 9, 4, 5, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (36, 0, 0, 2, 10, 5, 5, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (37, 0, 0, 2, 11, 6, 5, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (38, 0, 0, 2, 12, 6, 6, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (39, 0, 0, 2, 13, 7, 6, 0, 0, 0, 0, 0, 0);
/* Diamond */
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (40, 0, 0, 3, 1, 1, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (41, 0, 0, 3, 2, 2, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (42, 0, 0, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (43, 0, 0, 3, 4, 4, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (44, 0, 0, 3, 5, 5, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (45, 0, 0, 3, 6, 6, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (46, 0, 0, 3, 7, 7, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (47, 0, 0, 3, 8, 8, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (48, 0, 0, 3, 9, 9, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (49, 0, 0, 3, 10, 10, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (50, 0, 0, 3, 11, 11, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (51, 0, 0, 3, 12, 12, 0, 0, 0, 0, 0, 0, 1);
INSERT INTO card (id, owner_type, quality, suit, rank_num, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (52, 0, 0, 3, 13, 13, 0, 0, 0, 0, 0, 0, 0);
