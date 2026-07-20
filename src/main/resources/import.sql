INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');
INSERT INTO tb_role (authority) VALUES ('ROLE_BARBER');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_user (name, email, phone, password) VALUES ('Emanuel', 'emanuel@gmail.com', '123456', '$2a$10$zjTKwceT42eaSSUfIdK.pO2OwKqXGXAE0wjzjCRGKBNmMMVu.QnSy');
INSERT INTO tb_user (name, email, phone, password) VALUES ('Lucas', 'lucas@gmail.com', '123456', '$2a$10$zjTKwceT42eaSSUfIdK.pO2OwKqXGXAE0wjzjCRGKBNmMMVu.QnSy');
INSERT INTO tb_user (name, email, phone, password) VALUES ('Higor', 'higor@gmail.com', '123456', '$2a$10$zjTKwceT42eaSSUfIdK.pO2OwKqXGXAE0wjzjCRGKBNmMMVu.QnSy');
INSERT INTO tb_user (name, email, phone, password) VALUES ('Flavio', 'flavio@gmail.com', '123456', '$2a$10$zjTKwceT42eaSSUfIdK.pO2OwKqXGXAE0wjzjCRGKBNmMMVu.QnSy');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 3);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4, 2);

INSERT INTO tb_bloqueio (barber_id, start_time, end_time) VALUES (2, '2026-07-04T14:00:00', '2026-07-04T18:00:00');
