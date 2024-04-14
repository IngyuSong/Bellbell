-- basic_notification 테이블 생성
CREATE TABLE IF NOT EXISTS basic_notification (
                                                  is_activated BIT NULL,
                                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                  day VARCHAR(255) NULL,
                                                  time VARCHAR(255) NULL
);

-- member 테이블 생성
CREATE TABLE IF NOT EXISTS member (
                                      member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      email VARCHAR(255) NOT NULL,
                                      nickname VARCHAR(255) NOT NULL
);

-- lunch 테이블 생성
CREATE TABLE IF NOT EXISTS lunch (
                                     basic_notification_id BIGINT NULL,
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     member_id BIGINT NULL,
                                     CONSTRAINT UK_nkxkow9us2il5bovy9g3mg2rc UNIQUE (basic_notification_id),
                                     CONSTRAINT UK_qu62sgdtjp8oxagplyhlel34p UNIQUE (member_id),
                                     CONSTRAINT FKfp9fokqo4lp5ma5fxa7b06x71 FOREIGN KEY (member_id) REFERENCES member (member_id),
                                     CONSTRAINT FKm2uw986eqik27yimrau4l3xgf FOREIGN KEY (basic_notification_id) REFERENCES basic_notification (id)
);

-- menu 테이블 생성
CREATE TABLE IF NOT EXISTS menu (
                                    menu_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    menu VARCHAR(255) NULL
);

-- parcel 테이블 생성
CREATE TABLE IF NOT EXISTS parcel (
                                      member_id BIGINT NULL,
                                      parcel_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      carrier VARCHAR(255) NOT NULL,
                                      tracking_no VARCHAR(255) NOT NULL,
                                      CONSTRAINT FKi7rdl7u6jpqox5e9xfhe29er2 FOREIGN KEY (member_id) REFERENCES member (member_id)
);

-- tracking_info 테이블 생성
CREATE TABLE IF NOT EXISTS tracking_info (
                                             parcel_id BIGINT NULL,
                                             time DATETIME(6) NULL,
                                             tracking_info_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             description VARCHAR(255) NULL,
                                             location VARCHAR(255) NULL,
                                             status VARCHAR(255) NULL,
                                             CONSTRAINT FKasdg9pw4xj39uuebgg0w912sh FOREIGN KEY (parcel_id) REFERENCES parcel (parcel_id)
);

-- user_notification 테이블 생성
CREATE TABLE IF NOT EXISTS user_notification (
                                                 member_id BIGINT NULL,
                                                 user_notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 content VARCHAR(255) NOT NULL,
                                                 day VARCHAR(255) NOT NULL,
                                                 time VARCHAR(255) NOT NULL,
                                                 CONSTRAINT FKt6e86q8los6vw2k2pujevfa4s FOREIGN KEY (member_id) REFERENCES member (member_id)
);

-- weather 테이블 생성
CREATE TABLE IF NOT EXISTS weather (
                                       basic_notification_id BIGINT NULL,
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       member_id BIGINT NULL,
                                       address VARCHAR(255) NULL,
                                       gridx VARCHAR(255) NULL,
                                       gridy VARCHAR(255) NULL,
                                       CONSTRAINT UK_cp0rg92dvopq2ny16458e1sf8 UNIQUE (member_id),
                                       CONSTRAINT UK_gi30voyas6ma9i2n0f25c1gvp UNIQUE (basic_notification_id),
                                       CONSTRAINT FK558x8i5mip3uo4wmocwvrxhna FOREIGN KEY (basic_notification_id) REFERENCES basic_notification (id),
                                       CONSTRAINT FKsxu8hngycm1arp6d3ckiornwm FOREIGN KEY (member_id) REFERENCES member (member_id)
);
