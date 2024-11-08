DROP TABLE IF EXISTS users cascade;
DROP TABLE IF EXISTS categories cascade;
DROP TABLE IF EXISTS events cascade;
DROP TABLE IF EXISTS requests cascade;
DROP TABLE IF EXISTS compilations cascade;
DROP TABLE IF EXISTS event_compilation cascade;

CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name    VARCHAR(250)                            NOT NULL,
    email   VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS categories
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation         VARCHAR(2000)                           NOT NULL,
    category_id        BIGINT                                  NOT NULL,
    confirmed_requests BIGINT,
    created_on         TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    description        VARCHAR(7000)                           NOT NULL,
    date               TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    initiator_id       BIGINT                                  NOT NULL,
    lat                REAL                                    NOT NULL,
    lon                REAL                                    NOT NULL,
    paid               BOOL                                    NOT NULL,
    participant_limit  BIGINT,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOL,
    state              VARCHAR(64)                             NOT NULL,
    title              VARCHAR(120)                            NOT NULL,
    views              BIGINT,
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT check_title_length CHECK (length(title) >= 3 AND length(title) <= 120),
    CONSTRAINT check_description_length CHECK (length(description) >= 20 AND length(description) <= 7000),
    CONSTRAINT check_annotation_length CHECK (length(annotation) >= 20 AND length(annotation) <= 2000),
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE,
    FOREIGN KEY (initiator_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id     BIGINT                                  NOT NULL,
    requester_id BIGINT                                  NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    status       VARCHAR(16)                             NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT UQ_EVENT_WITH_REQUESTER UNIQUE (event_id, requester_id),
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    FOREIGN KEY (requester_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title          VARCHAR(120)                            NOT NULL,
    pinned         BOOL                                    NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (id),
    CONSTRAINT UQ_COMPILATIONS_TITLE UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS event_compilation
(
    event_id       BIGINT REFERENCES events (id) ON DELETE CASCADE,
    compilation_id BIGINT REFERENCES compilations (id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, compilation_id),
    CONSTRAINT UQ_EVENT_WITH_compilation UNIQUE (event_id, compilation_id)
);