INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENTE');
INSERT INTO tb_role (authority) VALUES ('ROLE_BARBEIRO');

INSERT INTO tb_user (nome, email, telefone, senha) VALUES ('Emanuel', 'emanuel@gmail.com', '123456', 'abcde');
INSERT INTO tb_user (nome, email, telefone, senha) VALUES ('Higor', 'higor@gmail.com', '123456', 'abcde');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);
