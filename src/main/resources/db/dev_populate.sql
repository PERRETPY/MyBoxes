insert into location values (gen_random_uuid (), 'Garage', '');
insert into location values (gen_random_uuid (), 'Salon', '');
insert into location values (gen_random_uuid (), 'Cuisine', '');
insert into location values (gen_random_uuid (), 'Bureau', '');

insert into container values (gen_random_uuid (), 'Boite 1', 'Boite', (select id from location where name = 'Garage'));
insert into container values (gen_random_uuid (), 'Boite 2', 'Boite', (select id from location where name = 'Garage'));
insert into container values (gen_random_uuid (), 'Boite 3', 'Boite', (select id from location where name = 'Garage'));
insert into container values (gen_random_uuid (), 'Boite 4', 'Boite', (select id from location where name = 'Garage'));
insert into container values (gen_random_uuid (), 'Caisse outil jaune', 'Caisse', (select id from location where name = 'Garage'));

insert into container values (gen_random_uuid (), 'Meuble télé', 'Meuble', (select id from location where name = 'Salon'));
insert into container values (gen_random_uuid (), 'Étagère grise', 'Meuble', (select id from location where name = 'Salon'));

insert into container values (gen_random_uuid (), 'Étagère noire', 'Meuble', (select id from location where name = 'Bureau'));


insert into item values (gen_random_uuid (), 'Rallonge', 'Électricité', (select id from container where name = 'Boite 1'));
insert into item values (gen_random_uuid (), 'Multiprise', 'Électricité', (select id from container where name = 'Boite 1'));
insert into item values (gen_random_uuid (), 'Duvet Anaïs', 'Camping', (select id from container where name = 'Boite 2'));
insert into item values (gen_random_uuid (), 'Duvet PY', 'Camping', (select id from container where name = 'Boite 2'));
insert into item values (gen_random_uuid (), 'Tente', 'Camping', (select id from container where name = 'Boite 2'));


