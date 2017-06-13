create database foodmatch;

create table foodmatch.deals
(
	id int auto_increment
		primary key,
	uid varchar(45) null,
	left_id int null,
	right_id int null,
	done tinyint null
)
;

create index left_dish_fk_idx
	on deals (left_id)
;

create index right_dish_fk_idx
	on deals (right_id)
;

create table foodmatch.dishes
(
	id int auto_increment
		primary key,
	name varchar(500) null,
	score int null,
	published tinyint null,
	img_url varchar(500) null,
	keywords text default ' ' null
)
;

alter table deals
	add constraint left_dish_fk
		foreign key (left_id) references foodmatch.dishes (id)
;

alter table deals
	add constraint right_dish_fk
		foreign key (right_id) references foodmatch.dishes (id)
;
