module net.cryptic_game.backend.base {
    exports net.cryptic_game.backend.base.sql.models;
    exports net.cryptic_game.backend.base.utils;
    exports net.cryptic_game.backend.base;
    exports net.cryptic_game.backend.base.sql;
    exports net.cryptic_game.backend.base.config;

    requires com.google.gson;
    requires slf4j.api;
    requires io.netty.all;
    requires org.hibernate.orm.core;
    requires bcrypt;
    requires commons.validator;
    requires io.sentry;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires reflections;
}
