-- User 
-- Usuarios base.

LOCK TABLES `chat_socket_poli`.`User` WRITE;

INSERT INTO `chat_socket_poli`.`User` 
VALUES 
(1,'Admin','Admin','10000001','adminpoli@poligran.edu.co','admin','123456',0),
(2,'Juan','Perez','10000002','juanperez@poligran.edu.co','poli01','123456',0),
(3,'Maria','Martinez','10000003','mariamartinez@poligran.edu.co','poli03','123456',0);

UNLOCK TABLES;


--------

LOCK TABLES `chat_socket_poli`.`Chatroom` WRITE;

INSERT INTO `chat_socket_poli`.`Chatroom` 
VALUES 
(1,'Administradors',1,3,0,1),
(2,'Contadores',1,2,1,1),
(3,'Coordinadores',3,2,1,1);

UNLOCK TABLES;

--------


LOCK TABLES `chat_socket_poli`.`ChatroomMember` WRITE;

INSERT INTO `chat_socket_poli`.`ChatroomMember` 
VALUES
(1, 1, NOW()), -- Administradoes
(1, 2, NOW()), -- Administradoes
(1, 3, NOW()), -- Administradoes
(2, 1, NOW()), -- Contadores
(2, 2, NOW()), -- Contadores
(3, 3, NOW()), -- Coordinadores
(3, 2, NOW()) -- Coordinadores
;

UNLOCK TABLES;
