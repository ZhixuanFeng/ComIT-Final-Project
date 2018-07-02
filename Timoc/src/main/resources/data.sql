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
/* Diamond */
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (2, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (3, 0, 0, 2, 3, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (4, 0, 0, 3, 4, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (5, 0, 0, 4, 5, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (6, 0, 0, 5, 6, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (7, 0, 0, 6, 7, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (8, 0, 0, 7, 8, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (9, 0, 0, 8, 9, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (10, 0, 0, 9, 10, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (11, 0, 0, 10, 11, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (12, 0, 0, 11, 12, 0, 0, 0, 0, 0, 0, 1);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (13, 0, 0, 12, 13, 0, 0, 0, 0, 0, 0, 0);
/* Club */
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (14, 0, 0, 13, 0, 1, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (15, 0, 0, 14, 1, 1, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (16, 0, 0, 15, 2, 1, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (17, 0, 0, 16, 2, 2, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (18, 0, 0, 17, 2, 3, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (19, 0, 0, 18, 3, 3, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (20, 0, 0, 19, 4, 3, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (21, 0, 0, 20, 4, 4, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (22, 0, 0, 21, 4, 5, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (23, 0, 0, 22, 5, 5, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (24, 0, 0, 23, 6, 5, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (25, 0, 0, 24, 6, 6, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (26, 0, 0, 25, 7, 6, 0, 0, 0, 0, 0, 0);
/* Heart */
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (27, 0, 0, 26, 0, 0, 1, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (28, 0, 0, 27, 0, 0, 2, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (29, 0, 0, 28, 0, 0, 3, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (30, 0, 0, 29, 0, 0, 4, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (31, 0, 0, 30, 0, 0, 5, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (32, 0, 0, 31, 0, 0, 6, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (33, 0, 0, 32, 0, 0, 7, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (34, 0, 0, 33, 0, 0, 8, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (35, 0, 0, 34, 0, 0, 9, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (36, 0, 0, 35, 0, 0, 10, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (37, 0, 0, 36, 0, 0, 11, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (38, 0, 0, 37, 0, 0, 0, 0, 0, 0, 24, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (39, 0, 0, 38, 0, 0, 13, 0, 0, 0, 0, 0);
/* Spade */
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (40, 0, 0, 39, 0, 1, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (41, 0, 0, 40, 0, 2, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (42, 0, 0, 41, 0, 3, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (43, 0, 0, 42, 0, 4, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (44, 0, 0, 43, 0, 5, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (45, 0, 0, 44, 0, 6, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (46, 0, 0, 45, 0, 7, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (47, 0, 0, 46, 0, 8, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (48, 0, 0, 47, 0, 9, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (49, 0, 0, 48, 0, 10, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (50, 0, 0, 49, 0, 11, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (51, 0, 0, 50, 0, 12, 0, 0, 0, 0, 0, 0);
INSERT INTO card (id, owner_type, quality, indecks, attack, block, heal, mana, random, taunt, revive, aoe) VALUES (52, 0, 0, 51, 0, 13, 0, 0, 0, 0, 0, 0);
