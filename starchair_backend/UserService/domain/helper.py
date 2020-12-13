# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:20 下午

@Author: fduxuan

Desc: mongo orm model

"""
from pymongo import database, collection
import datetime
import os
import base64
from pymongo.errors import DuplicateKeyError
from middleware.errors import DUPLICATED_MONGO_KEY, NO_RECORD


def get_uuid():
    bs = str(datetime.datetime.now()).encode()
    bs += os.urandom(32)
    return base64.urlsafe_b64encode(bs).decode().replace("=", "")


def get_now():
    return datetime.datetime.now(datetime.timezone.utc)


class Model:
    mongo_id = "_id"
    created_at = "created_at"

    coll_name = None

    def __init__(self, mongo_db=None, coll_name=None):
        """
        init a model
        :param mongo_db: mongo db object
        :param coll_name: collection name
        """
        self.mongo_db = None
        self.coll = None
        if mongo_db is not None or coll_name is not None:
            self.setup(mongo_db, coll_name)

    def setup(self, mongo_db: database.Database, coll_name: str):
        if mongo_db is not None and self.mongo_db is None:
            self.mongo_db = mongo_db
        if coll_name is not None:
            self.coll_name = coll_name
        self.coll: collection.Collection = mongo_db[self.coll_name]

    @property
    def collection(self):
        return self.coll_name

    @property
    def database(self):
        return self.mongo_db.name

    def create(self, data=None, **kwargs):
        """
        create a new model
        :param data:
        :param kwargs:
        :return:
        """
        if data is None:
            data = dict()
        data.update(kwargs)
        data['created_at'] = get_now().isoformat()
        if '_id' not in data:
            data['_id'] = get_uuid()
        try:
            result = self.coll.insert_one(data)
        except DuplicateKeyError as exception:
            raise DUPLICATED_MONGO_KEY
        return result.inserted_id

    def find_one(self, query=None, project=None, to_raise=False):
        """
        get a mongo object

        :param query: mongo query
        :param project: mongo project
        :param to_raise:
        :return:
        """
        obj = self.coll.find_one(query, projection=project)
        if obj is None:
            if to_raise:
                raise NO_RECORD
            return None
        else:
            return obj

    def find(
            self,
            query=None,
            project=None,
            limit=500,
            skip=0,
            sort=None,
            extra_filter=None
    ):  # at most 500 data
        if sort is None:
            sort = [('created_at', -1)]
        cursor = self.coll.find(
            filter=query,
            projection=project,
            limit=limit,
            skip=skip,
            sort=sort
        )
        return list(cursor)

    def find_from_args(self, column_name, data_json):
        # 自动寻找单个列名过滤
        query = {column_name: data_json.get(column_name)}
        obj = self.coll.find_one(query)
        return obj

    def delete_one(self, query):
        self.coll.delete_one(filter=query)

    def delete_many(self, query):
        self.coll.delete_many(query)

    def update_one(self, query, doc):
        if "_id" in doc:
            doc.pop("_id")
        if "created_at" in doc:
            doc.pop("created_at")
        self.coll.update_one(query, {"$set": doc})

    def update_many(self, query, doc):
        if "_id" in doc:
            doc.pop("_id")
        if "created_at" in doc:
            doc.pop("created_at")
        self.coll.update_many(query, doc)


