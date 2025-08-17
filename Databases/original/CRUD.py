from pymongo import MongoClient
from bson.objectid import ObjectId

class AnimalShelter(object):
    """ CRUD operations for Animal collection in MongoDB """

    def __init__(self, username, password):
        # Initializing the MongoClient. This helps to 
        # access the MongoDB databases and collections.
        # This is hard-wired to use the aac database, the 
        # animals collection, and the aac user.
        # Definitions of the connection string variables are
        # unique to the individual Apporto environment.
        #
        # You must edit the connection variables below to reflect
        # your own instance of MongoDB!
        #
        # Connection Variables
        #
        USER = username
        PASS = password
        HOST = ''
        PORT = 31365
        DB = 'AAC'
        COL = 'animals'
        #
        # Initialize Connection
        #
        self.client = MongoClient('mongodb://%s:%s@%s:%d' % (USER,PASS,HOST,PORT))
        self.database = self.client['%s' % (DB)]
        self.collection = self.database['%s' % (COL)]
    
    def create(self, animal):
        """
        Insert a new document into the collection, Create part of CRUD.
        :param aninmal: dictionary containing the animal data
        :return: True/False if successful or failed
        """

        # Check if the animal is a dictionary
        if not isinstance(animal, dict):
            print("Animal must be a dictionary")
            return False

        try:
            # Insert the animal into the collection
            result = self.collection.insert_one(animal)
            return True
        except Exception as e:
            print(f"Error inserting document: {e}")
            return False
    
    def read(self, query):
        """
        Read documents from the collection, Read part of CRUD.
        :param query: dictionary containing the query to find documents
        :return: results or an empty list on success/failure
        """

        # null check
        if query is None:
            print("Query is null")
            return []
        
        # Check if the query is a dictionary
        if not isinstance(query, dict):
            print("Query must be a dictionary")
            return []
        
        try:
            # Find documents in the collection that match the query
            results = self.collection.find(query)
            # Converts the results to a list and return it
            return [doc for doc in results]
        except Exception as e:
            print(f"Error reading documents: {e}")
            return []
    
    def update(self, query, update_values):
        """
        Update documents in the collection
        :param query: the dictionary to match documents for the update
        :param update_values: dictionary of values to update
        :return: number of documents modified
        """
        
        # validate input fields
        if not isinstance(query, dict) or not isinstance(update_values, dict):
            print("Both query and update_values must be dictionaries")
            return 0
        
        # ensure success, return the modified value if successful
        try: 
            result = self.collection.update_many(query, {"$set": update_values})
            return result.modified_count
        except Exception as e:
            # return 0 if an error is caught
            print(f"Error updating documents: {e}")
            return 0
    
    def delete(self, query):
        """
        Delete documents from the collection.
        :param query: dictionary to match documents to
        :return: number of documents deleted
        """
        
        #validate input field
        if not isinstance(query, dict):
            print("Query must be a dictionary")
            return 0
        
        # return deleted count
        try:
            result = self.collection.delete_many(query)
            return result.deleted_count
        except Exception as e:
            # return 0 if error is caught
            print(f"Error deleting documents: {e}")
            return 0