# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy


class ScrapyprojectItem(scrapy.Item):
    # define the fields for your item here like:
    # name = scrapy.Field()
    product_ASIN = scrapy.Field()
    product_name = scrapy.Field()
    product_price = scrapy.Field()
    product_score = scrapy.Field()
    product_keyword = scrapy.Field()
    pass
