CREATE TABLE `chat_socket_poli`.`User` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  `LastName` varchar(50) NOT NULL,
  `Identification` varchar(15) DEFAULT NULL,
  `Email` varchar(50) DEFAULT NULL,
  `UserName` varchar(15) NOT NULL,
  `Password` varchar(150) NOT NULL,
  `IsOnline` tinyint DEFAULT NULL COMMENT 'true = 1; false = 0;',
  PRIMARY KEY (`Id`)
);

CREATE TABLE `chat_socket_poli`.`Chatroom` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  `OwnerId` int NOT NULL,
  `Members` int NOT NULL,
  `IsPrivate` tinyint DEFAULT 1 COMMENT 'true = 1; false = 0;',
  `IsActive` tinyint DEFAULT NULL COMMENT 'true = 1; false = 0;',
   PRIMARY KEY (`Id`),
   CONSTRAINT FK_Chatroom_User FOREIGN KEY (OwnerId) REFERENCES User(Id)
);

CREATE TABLE `chat_socket_poli`.`Message` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `Content` varchar(500) NOT NULL,
  `ChatroomId` int NOT NULL,
  `UserId` int NOT NULL,
  `CreateDate` Datetime NOT NULL,
   PRIMARY KEY (`Id`),
   CONSTRAINT FK_Message_Chatroom FOREIGN KEY (ChatroomId) REFERENCES Chatroom(Id),
   CONSTRAINT FK_Message_User FOREIGN KEY (UserId) REFERENCES User(Id)
);

CREATE TABLE `chat_socket_poli`.`ChatroomMember` (
  `ChatroomId` int NOT NULL,
  `MemberUserId` int NOT NULL,
  `JoinedDate` Datetime NOT NULL,
  CONSTRAINT FK_ChatroomMember_Chatroom FOREIGN KEY (ChatroomId) REFERENCES Chatroom(Id),
  CONSTRAINT FK_ChatroomMember_User FOREIGN KEY (MemberUserId) REFERENCES User(Id),
  CONSTRAINT UC_Chatroom_Member UNIQUE (ChatroomId, MemberUserId)
);