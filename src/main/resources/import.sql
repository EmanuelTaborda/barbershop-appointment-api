INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENTE');
INSERT INTO tb_role (authority) VALUES ('ROLE_BARBEIRO');

INSERT INTO tb_user (nome, email, telefone, senha) VALUES ('Emanuel', 'emanuel@gmail.com', '123456', 'abcde');
INSERT INTO tb_user (nome, email, telefone, senha) VALUES ('Higor', 'higor@gmail.com', '123456', 'abcde');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);

INSERT INTO tb_bloqueio_agenda (barbeiro_id, inicio_bloqueio, fim_bloqueio) VALUES (2, '2026-06-12T14:00:00', '2026-06-12T16:00:00');
