# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html
import psycopg2


class ScrapyprojectPipeline(object):

    #def __init__(self):
	#self.conn = psycopg2.connect(host = 'localhost', user = 'power_user', password = '$poweruser', dbname = 'shirts')
	#self.cur = self.connection.cursor()

    def open_spider(self, spider):
	hostname = 'localhost'
	username = 'power_user'
	password = '$poweruser'
	database = 'shirts'
	self.connection = psycopg2.connect(host = hostname, user = username, password = password, dbname = database)
	self.cur = self.connection.cursor()

    def close_spider(self, spider):
	self.cur.close()
	self.connection.close()

    def process_item(self, item, spider):
	query = """INSERT INTO shirts_attributes(asin, name, price, rank, keyword) VALUES(%s, %s, %s, %s, %s)"""
	
        asin = item['product_ASIN']
	asin = ''.join(asin)
	
        name = item['product_name']
	name = ''.join(name)
	
        price = item['product_price']
	price = ''.join(price)
	
        rank = item['product_score']
	rank = ''.join(rank)
	rank = rank.strip()
	rank = int(rank)
        
        keyword = item['product_keyword']
        keyword = ''.join(keyword)
        
        self.cur.execute(query, (asin, name, price, rank, keyword))
	self.connection.commit()
	return item
