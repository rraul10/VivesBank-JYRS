-- Inserción de usuario con roles de USER y ADMIN
INSERT INTO users (guuid, username, password, foto_perfil, created_at, updated_at, is_deleted)
VALUES ('puZjCDm_xCg', 'admin@example.com', '123456Ab@', 'admin.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);

-- Inserción de roles en la tabla de roles (relación con el usuario)
INSERT INTO User_roles (user_id, roles)
VALUES (1, 'USER'),
       (1, 'ADMIN');

-- Inserción en la tabla Admin asociada al usuario creado
INSERT INTO admin (id_admin, guuid, user_id)
VALUES (1, 'puZjCDm_xCg', 1);
