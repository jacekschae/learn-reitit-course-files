-- account
truncate table account cascade;
insert into account ("uid", "name")
values ('auth0|5ef440986e8fbb001355fd9c', 'Auth0'),
       ('mike@mailinator.com', 'Mike'),
       ('jade@mailinator.com', 'Jade'),
       ('mark@mailinator.com', 'Mark');

-- recipe
truncate table recipe cascade;
insert into recipe (recipe_id, "public", prep_time, "name", img, favorite_count, "uid")
values ('a3dde84c-4a33-45aa-b0f3-4bf9ac997680', false, 45, 'Splitony''s Pizza', 'https://res.cloudinary.com/schae/image/upload/f_auto,h_400,q_80/v1548183465/cheffy/recipe/pizza.jpg', 5, 'auth0|5ef440986e8fbb001355fd9c'),
       ('a1995316-80ea-4a98-939d-7c6295e4bb46', true, 5, 'Avocado Salad', 'https://res.cloudinary.com/schae/image/upload/f_auto,h_400,q_80/v1548183354/cheffy/recipe/vegie-salad.jpg', 5, 'jade@mailinator.com');

-- step
truncate table step cascade;
insert into step (step_id, "sort", description, recipe_id)
values ('867ed4bf-4628-48f4-944d-e6b7786bfa92', 1, 'First Step', 'a3dde84c-4a33-45aa-b0f3-4bf9ac997680'),
       ('803307da-8dec-4c1b-a0f2-36742ac0e7f2', 2, 'Second Step', 'a3dde84c-4a33-45aa-b0f3-4bf9ac997680'),
       ('22a82a84-91cc-40e2-8775-d5bee9d188ff', 3, 'Second Step', 'a1995316-80ea-4a98-939d-7c6295e4bb46');

-- ingredient
truncate table ingredient cascade;
insert into ingredient (ingredient_id, "sort", "name", amount, "measure", recipe_id)
values ('27b1f44c-2852-416d-960e-3ee7d23ee713', 1, 'Flower', 250, 'grams', 'a3dde84c-4a33-45aa-b0f3-4bf9ac997680'),
       ('c89e6054-5e4f-48f2-b6d4-f037460ef72e', 2, 'Flower', 250, 'grams', 'a3dde84c-4a33-45aa-b0f3-4bf9ac997680'),
       ('aaa7ab14-efd7-45a1-ac86-aa6bfe13a2ab', 3, 'Flower', 250, 'grams', 'a1995316-80ea-4a98-939d-7c6295e4bb46');

-- conversation
truncate table conversation cascade;
insert into conversation (conversation_id, "uid", notifications)
values ('8d4ab926-d5cc-483d-9af0-19627ed468eb', 'auth0|5ef440986e8fbb001355fd9c', 2),
       ('8d4ab926-d5cc-483d-9af0-19627ed468eb', 'mark@mailinator.com', 0),
       ('362d06c7-2702-4273-bcc3-0c04d2753b6f', 'auth0|5ef440986e8fbb001355fd9c', 0),
       ('362d06c7-2702-4273-bcc3-0c04d2753b6f', 'jade@mailinator.com', 1),
       ('2019887e-ae38-4c21-b7a2-2971d43d74b7', 'jade@mailinator.com', 1),
       ('2019887e-ae38-4c21-b7a2-2971d43d74b7', 'mark@mailinator.com', 0);

-- message
truncate table message cascade;
insert into message (message_id, message_body, created_at, conversation_id, "uid")
values ('302a4672-9f92-48d5-8c43-7d07f0c3104f', '1st message', '2019-11-23 14:00:00', '8d4ab926-d5cc-483d-9af0-19627ed468eb', 'auth0|5ef440986e8fbb001355fd9c'),
       ('c0b708e7-07f1-462b-bf3b-f96518d1195e', '2nd message', '2019-11-25 14:00:00', '8d4ab926-d5cc-483d-9af0-19627ed468eb', 'mark@mailinator.com'),
       ('d2dd907b-18c3-4cf4-b331-1742e641c424', '3rd message', '2019-11-24 14:00:00', '8d4ab926-d5cc-483d-9af0-19627ed468eb', 'mark@mailinator.com'),
       ('3465c69d-8d49-4ec6-af79-31162038240d', '1st message', '2019-11-26 14:00:00', '362d06c7-2702-4273-bcc3-0c04d2753b6f', 'auth0|5ef440986e8fbb001355fd9c'),
       ('73edeb98-8d7b-4efa-8ba5-9d09baaf6b83', '2nd message', '2019-11-23 14:00:00', '362d06c7-2702-4273-bcc3-0c04d2753b6f', 'jade@mailinator.com'),
       ('754cef74-6b48-4d6a-bf02-9a0d09207468', '2nd message', '2019-11-23 14:00:00', '2019887e-ae38-4c21-b7a2-2971d43d74b7', 'jade@mailinator.com'),
       ('f9d8d63b-ed6a-4be3-93d2-aff370fe1a25', '2nd message', '2019-11-23 14:00:00', '2019887e-ae38-4c21-b7a2-2971d43d74b7', 'mark@mailinator.com');

-- recipe_favorite
truncate table recipe_favorite cascade;
insert into recipe_favorite ("uid", recipe_id)
values ('auth0|5ef440986e8fbb001355fd9c', 'a3dde84c-4a33-45aa-b0f3-4bf9ac997680'),
       ('jade@mailinator.com', 'a3dde84c-4a33-45aa-b0f3-4bf9ac997680');