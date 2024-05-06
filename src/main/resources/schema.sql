CREATE TABLE reservation_time
(
    id       BIGINT NOT NULL AUTO_INCREMENT,
    start_at TIME   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE theme
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    thumbnail   VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE reservation
(
    id       BIGINT NOT NULL AUTO_INCREMENT,
    date     DATE   NOT NULL,
    time_id  BIGINT,
    theme_id BIGINT,                             -- 컬럼 추가
    PRIMARY KEY (id),
    FOREIGN KEY (time_id) REFERENCES reservation_time (id),
    FOREIGN KEY (theme_id) REFERENCES theme (id) -- 외래키 추가
);

CREATE TABLE member
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE reservation_list
(

    id             BIGINT NOT NULL AUTO_INCREMENT,
    member_id      BIGINT,
    reservation_id BIGINT,
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (reservation_id) REFERENCES reservation (id) ON DELETE CASCADE,
    PRIMARY KEY (id)
);
