CREATE database IF NOT EXISTS risk_assessment;

USE risk_assessment;

CREATE TABLE IF NOT EXISTS `Users` (
    `id` INT unsigned NOT NULL AUTO_INCREMENT,
    `login` VARCHAR(256) NOT NULL,
    `password` VARCHAR(256) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Companies` (
	`id` INT unsigned NOT NULL AUTO_INCREMENT,
	`own_working_capital` FLOAT zerofill,
	`own_capital` FLOAT zerofill,
	`st_assets` FLOAT zerofill,
	`st_obligations` FLOAT zerofill,
	`net_profit` FLOAT zerofill,
	`assets` FLOAT zerofill,
	`obligations` FLOAT zerofill,
	`revenue` FLOAT zerofill,
	`name` VARCHAR(256),
	`user_id` INT unsigned NOT NULL,
	PRIMARY KEY (`id`),
	FOREIGN KEY (`user_id`) REFERENCES Users(`id`)
);

CREATE TABLE IF NOT EXISTS `Authorizations` (
    `id` INT unsigned NOT NULL AUTO_INCREMENT,
    `token` VARCHAR(256) NOT NULL,
    `user_id` INT unsigned NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT FOREIGN KEY (`user_id`) REFERENCES Users(`id`)
);