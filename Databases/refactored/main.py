import random
from faker import Faker

from .crud.factory import build_crud
from .crud.abstract import AbstractCRUD
from .crud.sqlite_impl import SQLiteCRUD
from .crud.mongo_impl import MongoCRUD

fake = Faker()

# ---- Backend Configurations ----
sqlite_cfg = {
    "kind": "sqlite",
    "path": "animals.db",
    "table": "animals"
}

mongo_cfg = {
    "kind": "mongo",
    "uri": "mongodb://SNHU:SNHUCS465@157.245.218.243:27017/AAC?authSource=admin",
    "db": "AAC",
    "collection": "animals"
}   

def generate_animal():
    """
    Use Faker to generate a random animal object.
    """

    animal_type = random.choice(["dog", "cat", "bird", "fish"])
    name = fake.first_name()
    breed = (
        fake.word().capitalize() + " Mix"
        if animal_type in ["dog", "cat"]
        else fake.word().capitalize()
    )

    # Keep in mind, this will not generate all of the fields. This is simply for the purpose of demonstrating
    # Utilizing both mongo and sqlite paradigms in tandem with the same structure.
    return {
        "animal_id": fake.uuid4(),
        "animal_type": animal_type,
        "name": name,
        "breed": breed,
        "color": fake.color_name(),
        "sex_upon_outcome": random.choice(
            ["Neutered Male", "Spayed Female", "Intact Male", "Intact Female"]
        ),
        "date_of_birth": str(fake.date_of_birth(minimum_age=0, maximum_age=15)),
        "outcome_type": random.choice(
            ["Adoption", "Transfer", "Return to Owner", "Euthanasia", "Still in Care"]
        ),
        "outcome_subtype": random.choice(
            ["Foster", "Partner", "Clinic", None]
        ),
    }


def populate_db(crud: AbstractCRUD, count: int = 10):
    ids = []
    for _ in range(count):
        rec = generate_animal()
        inserted = crud.create(rec)
        ids.append(inserted)
    return ids



def main():

    # create our different crud objects
    crud_sqlite: AbstractCRUD = build_crud(sqlite_cfg)
    crud_mongo: AbstractCRUD = build_crud(mongo_cfg)

    # ensure sqlite db is initialized
    crud_sqlite.ensure_default_schema()

    # start a print for SQLIte crud operations
    print(f"\nSQLITE CRUD Operations for database @ {sqlite_cfg['path']}")

    print("\nPopulating the database with 15 random animals...")
    ids = populate_db(crud_sqlite, 15)
    print(f"\nInserted {len(ids)} records with IDs: {ids}")

    print("\nShowing some samples from the created SQLite records...")
    print("\nSample Dogs: ")
    dogs = crud_sqlite.read({"animal_type": "dog"}, limit=5)
    
    for d in dogs:
        print(f"\nDog: {d['name']} ({d['breed']}) - {d['color']}")

    print(f"\nClosing SQLite connection")
    crud_sqlite.close()

    # start a print for Mongo Crud operations
    print(f"\nMONGO CRUD Operations for database @ {mongo_cfg['uri']}")

    print("\nPopulating the database with 15 random animals...")
    ids = populate_db(crud_mongo, 15)
    print(f"\nInserted {len(ids)} records with IDs: {ids}")
    mongoDogs = crud_mongo.read({"animal_type": "dog"}, limit=5)

    print("\nShowing some samples from the created MongoDB records...")
    print("\nSample Dogs: ")
    for md in mongoDogs:
        print(f"\nDog: {md['name']} ({md['breed']}) - {md['color']}")
    
    print(f"\nClosing MongoDB connection")
    crud_mongo.close()


if __name__ == "__main__":
    main()

