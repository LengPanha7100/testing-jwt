CREATE TABLE app_users
(
    user_id        serial PRIMARY KEY NOT NULL,
    full_name VARCHAR(50)        NOT NULL,
    email VARCHAR(100) NOT NULL ,
    username     VARCHAR(50)        NOT NULL,
    password  VARCHAR(255)       NOT NULL,
    is_verify BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE TABLE app_roles
(
    role_id        serial PRIMARY KEY NOT NULL,
    name VARCHAR(50)        NOT NULL
);

CREATE TABLE app_user_role
(
    user_id INT NOT NULL REFERENCES app_users (user_id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES app_roles (role_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE otp(
    otp_id SERIAL PRIMARY KEY NOT NULL ,
    otp VARCHAR(20) NOT NULL ,
    expiration TIMESTAMP NOT NULL ,
    user_id INT NOT NULL ,
    CONSTRAINT fk_userId FOREIGN KEY (user_id) REFERENCES app_users(user_id) ON DELETE CASCADE  ON UPDATE CASCADE
);

SELECT * FROM app_users WHERE email = 'panha789@gmail.com';


SELECT * FROM otp WHERE otp ='754425';